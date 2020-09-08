package com.huawei.sharedrive.app.core.backtask.reallydeletetask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.app.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.app.core.job.Task;
import com.huawei.sharedrive.app.core.job.ThreadPool;
import com.huawei.sharedrive.app.message.task.ExpiredMsgCleanTask;
import com.huawei.sharedrive.app.plugins.scan.task.SystemScanTask;

import pw.cdmi.core.utils.SpringContextUtil;

public class CommonScanExeThread extends Thread
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonScanExeThread.class);
    
    private SystemTaskService systemTaskService;
    
    private final static int MAX_FAILED_NUMBER = 10;
    
    private boolean reSet = false;
    
    private String pTaskId = null;
    
    private String taskKey = null;
    
    private final static int SLEEP_TIME = 1000;
    
    public CommonScanExeThread(String pTaskId, String taskKey)
    {
        this.pTaskId = pTaskId;
        this.taskKey = taskKey;
        systemTaskService = (SystemTaskService) SpringContextUtil.getBean("systemTaskService");
    }
    
    @Override
    public void run()
    {
        
        try
        {
            runWithRetry();
        }
        catch (Exception e)
        {
            LOGGER.info(e.getMessage(), e);
        }
        
    }
    
    public void setPTaskAndReSet(String pTaskId)
    {
        this.pTaskId = pTaskId;
        this.reSet = true;
    }
    
    /**
     * 添加任务
     * 
     * @param task
     */
    private void addTask(SystemTask task)
    {
        Task taskRunable = null;
        while (true)
        {
            switch (taskKey)
            {
                case TaskKeyConstant.OBJECT_SCAN_TABLE_TASK:
                    taskRunable = new ObjectScanSingleTableTask(task);
                    break;
                case TaskKeyConstant.FILE_SCAN_TABLE_TASK:
                    taskRunable = new FileScanSingleTableTask(task);
                    break;
                case TaskKeyConstant.EXPIRED_MSG_SCAN_TASK:
                    taskRunable = new ExpiredMsgCleanTask(task);
                    break;
                case TaskKeyConstant.SYSTEM_SCAN_TASK:
                    taskRunable = new SystemScanTask(task);
                    break;
                default:
                    break;
            }
            if (!ThreadPool.addTask(taskRunable))
            {
                
                // 等待任务执行
                try
                {
                    waitForSomeTimes(SLEEP_TIME);
                    continue;
                }
                catch (InterruptedException e)
                {
                    LOGGER.warn(e.getMessage(), e);
                    if (reSet)
                    {
                        break;
                    }
                }
            }
            break;
        }
    }
    
    /**
     * 检测是否还存在任务
     * 
     * @param pTaskId
     * @return
     */
    private boolean checkExistingTask(String pTaskId)
    {
        return systemTaskService.checkExistingTask(pTaskId);
    }
    
    private boolean isNeedRetry(int getFailedCount)
    {
        return getFailedCount >= MAX_FAILED_NUMBER && !checkExistingTask(pTaskId);
    }
    
    private void runWithRetry()
    {
        LOGGER.info("CommonScanExeThread  begin");
        int getFailedCount = 0;
        SystemTask task = null;
        
        while (true)
        {
            task = systemTaskService.getOneWaitingExecuteTask(pTaskId, taskKey);
            
            // 是否获取到任务，存在任务则添加
            if (null != task)
            {
                addTask(task);
            }
            else
            {
                getFailedCount++;
                // 获取一下任务
                if (isNeedRetry(getFailedCount))
                {
                    break;
                }
                try
                {
                    waitForSomeTimes(SLEEP_TIME);
                }
                catch (InterruptedException e)
                {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
        
        LOGGER.info("CommonScanExeThread  end");
    }
    
    private void waitForSomeTimes(long times) throws InterruptedException
    {
        Thread.sleep(times);
    }
}
