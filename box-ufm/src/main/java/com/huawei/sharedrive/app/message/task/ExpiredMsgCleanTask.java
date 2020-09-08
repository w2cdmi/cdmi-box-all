package com.huawei.sharedrive.app.message.task;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.common.systemtask.domain.ScanTableInfo;
import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.app.core.job.Task;
import com.huawei.sharedrive.app.message.service.MessageService;

import pw.cdmi.core.utils.SpringContextUtil;

/**
 * 过期消息清理任务
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2015-4-3
 * @see  
 * @since  
 */
public class ExpiredMsgCleanTask extends Task
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredMsgCleanTask.class);

    // 任务对象
    private SystemTask systemTask;

    private MessageService messageService;
    
    private SystemTaskService systemTaskService;
    
    public ExpiredMsgCleanTask(SystemTask systemTask)
    {
        this.systemTask = systemTask;
        messageService = (MessageService) SpringContextUtil.getBean("messageService");
        systemTaskService = (SystemTaskService) SpringContextUtil.getBean("systemTaskService");
    }
    
    @Override
    public void execute()
    {
        try
        {
            ScanTableInfo task = parse(systemTask);
            if(task == null)
            {
                LOGGER.info("Task info is null, task id: {}", systemTask.getTaskId());
                return;
            }
            
            LOGGER.info("Executing message clean task, task id {}, task info [{}, {}]", systemTask.getTaskId(), task.getDbNumber(), task.getTableName());
            
            // 更新任务执行状态
            systemTaskService.updateExecuteState(SystemTask.TASK_STATE_RUNING, new Date(), systemTask.getTaskId());
            
            // 清除过期消息
            int result = messageService.cleanExpiredMessage(task.getDbNumber(), task.getTableNumber());
            LOGGER.info("Message clean task completed, Clean result: {}", result);
            
            // 更新任务状态为完成
            systemTaskService.updateTaskState(SystemTask.TASK_STATE_END, systemTask.getTaskId());
        }
        catch (Exception e)
        {
            LOGGER.error("Clean message failed. Task id: {} ", systemTask.getTaskId(), e);
        }
        
    }
    
    @Override
    public String getName()
    {
        return "ExpiredMsgCleanTask";
    }

    private ScanTableInfo parse(SystemTask systemTask)
    {
        String taskInfo = systemTask.getTaskInfo();
        if(StringUtils.isBlank(taskInfo))
        {
            return null;
        }
        return ScanTableInfo.toObject(taskInfo);
    }
    
}
