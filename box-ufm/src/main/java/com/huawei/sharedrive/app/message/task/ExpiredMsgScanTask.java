package com.huawei.sharedrive.app.message.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.common.systemtask.domain.ScanTableInfo;
import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.app.common.systemtask.domain.UserDBInfo;
import com.huawei.sharedrive.app.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.app.common.systemtask.service.UserDBInfoService;
import com.huawei.sharedrive.app.utils.BusinessConstants;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.RandomGUID;

@Component("expiredMsgScanTask")
public class ExpiredMsgScanTask extends QuartzJobTask
{
    
    private static final int TABLE_COUNT = 10;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredMsgScanTask.class);
    
    @Autowired
    private SystemTaskService systemTaskService;
    
    @Autowired
    private UserDBInfoService userDBInfoService;
    
    @Autowired
    private ConfigManager configManager;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        
        // 清除上次生成的任务
        deleteLastExecuteTasks();
        
        // 生成表扫描任务
        String parentTaskId = generateTask();
        
        if (StringUtils.isNotBlank(parentTaskId))
        {
            // 发送通知
            configManager.setConfig(ExpiredMsgScanTask.class.getSimpleName(), parentTaskId);
            record.setSuccess(true);
            record.setOutput("Generate message cleanup task success");
        }
        else
        {
            String message = "Generate message cleanup task failed";
            LOGGER.warn(message);
            record.setSuccess(false);
            record.setOutput(message);
        }
        
    }
    
    private SystemTask createParentTask()
    {
        SystemTask task = new SystemTask();
        task.setCreateTime(new Date());
        task.setTaskId(new RandomGUID().getValueAfterMD5());
        task.setState(SystemTask.TASK_STATE_BEGIN);
        task.setTaskKey(TaskKeyConstant.EXPIRED_MSG_SCAN_TASK);
        return task;
    }
    
    private SystemTask createSubTask(SystemTask parentTask, UserDBInfo dbInfo, String tableName,
        int tableNumber)
    {
        SystemTask task = new SystemTask();
        Date date = new Date();
        
        task.setTaskId(new RandomGUID().getValueAfterMD5() + '_' + tableName);
        task.setpTaskId(parentTask.getTaskId());
        task.setState(SystemTask.TASK_STATE_BEGIN);
        task.setTaskKey(TaskKeyConstant.EXPIRED_MSG_SCAN_TASK);
        task.setCreateTime(date);
        ScanTableInfo tableInfo = new ScanTableInfo();
        tableInfo.setDbName(dbInfo.getDbName());
        tableInfo.setDbNumber(dbInfo.getDbNumber());
        tableInfo.setTableName(tableName);
        tableInfo.setTableNumber(tableNumber);
        tableInfo.setLastModfied(date);
        task.setTaskInfo(ScanTableInfo.toJsonStr(tableInfo));
        return task;
    }
    
    /**
     * 删除上一次执行的扫描任务
     * 
     */
    private void deleteLastExecuteTasks()
    {
        try
        {
            List<SystemTask> systemTaskList = systemTaskService.listSystemTaskByTaskKey(TaskKeyConstant.EXPIRED_MSG_SCAN_TASK);
            if (CollectionUtils.isEmpty(systemTaskList))
            {
                return;
            }
            
            List<SystemTask> systemTasks = systemTaskService.listSystemTaskByParentTaskId(systemTaskList.get(0)
                .getpTaskId());
            
            showLastExecutionResult(systemTasks);
            
            // 删除上一次执行的任务
            systemTaskService.deleteTaskByTaskKey(TaskKeyConstant.EXPIRED_MSG_SCAN_TASK);
            
        }
        catch (Exception e)
        {
            LOGGER.error("delete Last Execute Tasks failed", e);
            systemTaskService.deleteTaskByTaskKey(TaskKeyConstant.EXPIRED_MSG_SCAN_TASK);
        }
        
    }
    
    /**
     * 生成过期消息清理任务
     * 
     * @return
     */
    private String generateTask()
    {
        List<UserDBInfo> dbInfos = userDBInfoService.listAll();
        
        if (CollectionUtils.isEmpty(dbInfos))
        {
            LOGGER.error("DB info is empty, generate task failed!");
            return null;
        }
        
        List<SystemTask> allTask = new ArrayList<SystemTask>(BusinessConstants.INITIAL_CAPACITIES);
        SystemTask parentTask = createParentTask();
        allTask.add(parentTask);
        
        SystemTask subTask = null;
        for (UserDBInfo dbInfo : dbInfos)
        {
            for (int tableSuffix = 0; tableSuffix < TABLE_COUNT; tableSuffix++)
            {
                subTask = createSubTask(parentTask, dbInfo, "message_" + tableSuffix, tableSuffix);
                allTask.add(subTask);
            }
        }
        
        // 添加任务表
        systemTaskService.createTask(allTask);
        return parentTask.getTaskId();
    }
    
    private void showLastExecutionResult(List<SystemTask> systemTaskList)
    {
        if (CollectionUtils.isNotEmpty(systemTaskList))
        {
            return;
        }
        int completedCount = 0;
        for (SystemTask subTask : systemTaskList)
        {
            if (StringUtils.isNotBlank(subTask.getTaskInfo()))
            {
                ScanTableInfo info = ScanTableInfo.toObject(subTask.getTaskInfo());
                if (subTask.getState() == SystemTask.TASK_STATE_END)
                {
                    completedCount++;
                    LOGGER.info("Task [" + info.toStr() + "] is completed.");
                }
                else
                {
                    LOGGER.warn("Task [" + info.toStr() + "] is not completed" + ", current state: "
                        + subTask.getState());
                }
            }
            
        }
        LOGGER.info("The last execution results: Total number of tasks:" + systemTaskList.size()
            + ". The number of tasks completed: " + completedCount);
    }
    
}
