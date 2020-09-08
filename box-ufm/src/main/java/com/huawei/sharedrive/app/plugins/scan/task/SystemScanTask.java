package com.huawei.sharedrive.app.plugins.scan.task;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.common.systemtask.domain.ScanTableInfo;
import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.app.core.job.Task;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileService;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;
import com.huawei.sharedrive.app.plugins.scan.manager.SecurityScanManager;

import pw.cdmi.core.utils.SpringContextUtil;

/**
 * 安全扫描任务
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-3
 * @see
 * @since
 */
public class SystemScanTask extends Task
{
    private static final int WAIT_TIME_SECONDS = 30000;
    
    private static final int MAX_RESULT_SIZE = 1000;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemScanTask.class);
    
    // 任务对象
    private SystemTask systemTask;
    
    private SecurityScanManager securityScanManager;
    
    private SystemTaskService systemTaskService;
    
    private FileService fileService;
    
    public SystemScanTask(SystemTask systemTask)
    {
        this.systemTask = systemTask;
        systemTaskService = (SystemTaskService) SpringContextUtil.getBean("systemTaskService");
        securityScanManager = (SecurityScanManager) SpringContextUtil.getBean("securityScanManager");
        fileService = (FileService) SpringContextUtil.getBean("fileService");
    }
    
    @Override
    public void execute()
    {
        try
        {
            ScanTableInfo task = parse(systemTask);
            if (task == null)
            {
                LOGGER.info("Task info is null, task id: {}", systemTask.getTaskId());
                return;
            }
            
            LOGGER.info("Executing system scan task, task id {}, task info [{}, {}]",
                systemTask.getTaskId(),
                task.getDbNumber(),
                task.getTableName());
            
            // 更新任务执行状态
            systemTaskService.updateExecuteState(SystemTask.TASK_STATE_RUNING,
                new Date(),
                systemTask.getTaskId());
            
            long offset = 0;
            List<INode> list = null;
            while (true)
            {
                list = fileService.listFileAndVersions(task.getDbNumber(),
                    task.getTableNumber(),
                    offset,
                    MAX_RESULT_SIZE);
                if (CollectionUtils.isEmpty(list))
                {
                    break;
                }
                doTask(list);
                offset += MAX_RESULT_SIZE;
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Executing system scan task failed. Task id: {} ", systemTask.getTaskId(), e);
        }
        finally
        {
            // 更新任务状态为完成
            systemTaskService.updateTaskState(SystemTask.TASK_STATE_END, systemTask.getTaskId());
        }
        
    }
    
    @Override
    public String getName()
    {
        return "ExpiredMsgCleanTask";
    }
    
    public void waitSomeTimes()
    {
        try
        {
            Thread.sleep(WAIT_TIME_SECONDS);
        }
        catch (InterruptedException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
    private void doTask(List<INode> list)
    {
        // 如果扫描任务表数据量过大, 暂停添加扫描任务
        while (!isAllowedToAddTask())
        {
            waitSomeTimes();
        }
        try
        {
            for (INode node : list)
            {
                securityScanManager.sendSystemScanTask(node, SecurityScanTask.PRIORITY_NORMAL);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Send scan task faild!", e);
        }
        
    }
    
    private boolean isAllowedToAddTask()
    {
        int totalTasks = securityScanManager.getTotalTasks(SecurityScanTask.STATUS_ALL);
        return totalTasks < SecurityScanTask.MAX_TASK_NUM;
    }
    
    private ScanTableInfo parse(SystemTask systemTask)
    {
        String taskInfo = systemTask.getTaskInfo();
        if (StringUtils.isBlank(taskInfo))
        {
            return null;
        }
        return ScanTableInfo.toObject(taskInfo);
    }
    
}
