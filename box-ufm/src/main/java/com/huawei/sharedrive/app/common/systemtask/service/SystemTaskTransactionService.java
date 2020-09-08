package com.huawei.sharedrive.app.common.systemtask.service;

import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;

public interface SystemTaskTransactionService
{
    SystemTask getOneWaitingTask(String pTaskId, String taskKey);
    
    SystemTask lockAndUpdateOneWaitingExeTask(SystemTask subTask);
    
    
}
