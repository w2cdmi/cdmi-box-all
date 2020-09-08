package com.huawei.sharedrive.isystem.plugin.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.isystem.common.systemtask.domain.ScanTableInfo;
import com.huawei.sharedrive.isystem.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.isystem.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.isystem.common.systemtask.domain.UserDBInfo;
import com.huawei.sharedrive.isystem.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.isystem.common.systemtask.service.UserDBInfoService;
import com.huawei.sharedrive.isystem.exception.BusinessException;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.core.utils.RandomGUID;
import pw.cdmi.core.utils.SpringContextUtil;

public class SystemSecurityScanTask implements Runnable
{
    
    private static final String SYSTEM_SCAN_TASK_START_KEY = "system_scan_task_start";
    
    public static final int TABLE_COUNT = 500;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemSecurityScanTask.class);
    
    private SystemTaskService systemTaskService;
    
    private UserDBInfoService userDBInfoService;
    
    private ConfigManager configManager;
    
    private boolean timelyExecute;
    
    public SystemSecurityScanTask(boolean timelyExecute)
    {
        systemTaskService = (SystemTaskService) SpringContextUtil.getBean("systemTaskService");
        userDBInfoService = (UserDBInfoService) SpringContextUtil.getBean("userDBInfoService");
        configManager = (ConfigManager) SpringContextUtil.getBean("configManager");
        this.timelyExecute = timelyExecute;
    }
    
    public SystemSecurityScanTask()
    {
        systemTaskService = (SystemTaskService) SpringContextUtil.getBean("systemTaskService");
        userDBInfoService = (UserDBInfoService) SpringContextUtil.getBean("userDBInfoService");
        configManager = (ConfigManager) SpringContextUtil.getBean("configManager");
    }
    
    @Override
    public void run()
    {
        // 清除上次生成的任务
        deleteLastExecuteTasks();
        
        // 生成表扫描任务
        String parentTaskId = generateTask();
        
        if (StringUtils.isBlank(parentTaskId))
        {
            String message = "Generate message cleanup task failed";
            LOGGER.warn(message);
        }
        
        if (timelyExecute)
        {
            // 发送通知
            configManager.setConfig(SYSTEM_SCAN_TASK_START_KEY, parentTaskId);
        }
        
    }
    
    private SystemTask createParentTask()
    {
        SystemTask task = new SystemTask();
        task.setCreateTime(new Date());
        task.setTaskId(new RandomGUID().getValueAfterMD5());
        task.setState(SystemTask.TASK_STATE_BEGIN);
        task.setTaskKey(TaskKeyConstant.SYSTEM_SCAN_TASK);
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
        task.setTaskKey(TaskKeyConstant.SYSTEM_SCAN_TASK);
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
            List<SystemTask> systemTaskList = systemTaskService.listSystemTaskByTaskKey(TaskKeyConstant.SYSTEM_SCAN_TASK);
            if (CollectionUtils.isEmpty(systemTaskList))
            {
                return;
            }
            
            List<SystemTask> systemTasks = systemTaskService.listSystemTaskByParentTaskId(systemTaskList.get(0)
                .getpTaskId());
            
            showLastExecutionResult(systemTasks);
            
            // 删除上一次执行的任务
            systemTaskService.deleteTaskByTaskKey(TaskKeyConstant.SYSTEM_SCAN_TASK);
            
        }
        catch (Exception e)
        {
            systemTaskService.deleteTaskByTaskKey(TaskKeyConstant.SYSTEM_SCAN_TASK);
            throw new BusinessException(e);
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
        
        List<SystemTask> allTask = new ArrayList<SystemTask>(10);
        SystemTask parentTask = createParentTask();
        allTask.add(parentTask);
        
        SystemTask subTask;
        for (UserDBInfo dbInfo : dbInfos)
        {
            for (int tableSuffix = 0; tableSuffix < TABLE_COUNT; tableSuffix++)
            {
                subTask = createSubTask(parentTask, dbInfo, "inode_" + tableSuffix, tableSuffix);
                allTask.add(subTask);
            }
        }
        
        // 添加任务表
        systemTaskService.createTask(allTask);
        return parentTask.getTaskId();
    }
    
    private void showLastExecutionResult(List<SystemTask> systemTaskList) throws BusinessException
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
    
    public boolean isTimelyExecute()
    {
        return timelyExecute;
    }
    
    public void setTimelyExecute(boolean timelyExecute)
    {
        this.timelyExecute = timelyExecute;
    }
    
}
