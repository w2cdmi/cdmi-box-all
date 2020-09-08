package com.huawei.sharedrive.app.core.job;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import com.huawei.sharedrive.app.files.service.job.AyncDeleteObjectTask;
import com.huawei.sharedrive.app.files.service.job.DedupObjectTask;

public class DeleteObjectRejectedhandler implements RejectedExecutionHandler
{
    
    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadpoolexecutor)
    {
        if (runnable instanceof DedupObjectTask)
        {
            DedupObjectTask task = (DedupObjectTask) runnable;
            new AyncDeleteObjectTask(task.getFileService(), task.getObjectReference()).execute();
        }
    }
    
}
