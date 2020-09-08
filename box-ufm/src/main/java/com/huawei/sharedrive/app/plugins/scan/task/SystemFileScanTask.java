package com.huawei.sharedrive.app.plugins.scan.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.app.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.app.core.job.Task;
import com.huawei.sharedrive.app.core.job.ThreadPool;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component("systemFileScanTask")
public class SystemFileScanTask extends QuartzJobTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemFileScanTask.class);
    
    private final static int SLEEP_TIME = 10000;
    
    @Autowired
    private SystemTaskService systemTaskService;
    
    private SystemTask getOneWaitingSystemTask()
    {
        LOGGER.info("get waiting systemtasks");
        SystemTask task = systemTaskService.getOneWaitingExecuteTask(null, TaskKeyConstant.SYSTEM_SCAN_TASK);
        return task;
        
    }
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        SystemTask systemScanTask = getOneWaitingSystemTask();
        if (systemScanTask == null)
        {
            return;
        }
        Task taskRunable = new SystemScanTask(systemScanTask);
        while (!ThreadPool.addTask(taskRunable))
        {
            // 等待任务执行
            waitForNext();
        }
        
    }
    
    private void waitForNext()
    {
        try
        {
            Thread.sleep(SLEEP_TIME);
        }
        catch (InterruptedException e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
