package com.huawei.sharedrive.app.common.systemtask.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.common.systemtask.dao.SystemTaskDAO;
import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.service.SystemTaskTransactionService;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

@Service("systemTaskTransactionService")
public class SystemTaskTransactionServiceImpl implements SystemTaskTransactionService
{
    
    @Autowired
    private SystemTaskDAO systemTaskDAO;
    
    
    @Override
    public SystemTask getOneWaitingTask(String pTaskId, String taskKey)
    {
        SystemTask task = new SystemTask();
        task.setpTaskId(pTaskId);
        task.setTaskKey(taskKey);
        return systemTaskDAO.getOneWaitingExeTaskByTaskKey(task);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SystemTask lockAndUpdateOneWaitingExeTask(SystemTask subTask)
    {
        SystemTask resubTask = systemTaskDAO.getOneWaitingExeTaskAndLock(subTask);
        if(null == resubTask)
        {
            return null;
        }
        
        if(subTask.getState() != resubTask.getState())
        {
            return null;
        }
        
        String agent = PropertiesUtils.getProperty("self.privateAddr", "127.0.0.1");
        resubTask.setExeAgent(agent);
        resubTask.setExeUpdateTime(new Date());
        resubTask.setState(SystemTask.TASK_STATE_RUNING);
        if (1 == systemTaskDAO.updateTask(resubTask))
        {
            return resubTask;
        }
        
        return null;
    }
    
}
