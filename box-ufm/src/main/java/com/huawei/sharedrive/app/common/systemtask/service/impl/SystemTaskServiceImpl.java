package com.huawei.sharedrive.app.common.systemtask.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.common.systemtask.dao.SystemTaskDAO;
import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.domain.TaskExecuteInfo;
import com.huawei.sharedrive.app.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.app.common.systemtask.service.SystemTaskTransactionService;

@Service("systemTaskService")
public class SystemTaskServiceImpl implements SystemTaskService
{
    @Autowired
    private SystemTaskDAO systemTaskDAO;
    
    @Autowired
    private SystemTaskTransactionService systemTaskTransactionService;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createTask(List<SystemTask> taskList)
    {
        for (SystemTask task : taskList)
        {
            systemTaskDAO.createTask(task);
        }
    }
    
    @Override
    public int updateExecuteState(int state, Date updateTime, String taskId)
    {
        SystemTask systemTask = new SystemTask();
        systemTask.setState(state);
        systemTask.setExeUpdateTime(updateTime);
        systemTask.setTaskId(taskId);
        return systemTaskDAO.updateExecuteState(systemTask);
    }
    
    @Override
    public int updateExecuteAgent(String agent, String taskId)
    {
        SystemTask systemTask = new SystemTask();
        systemTask.setExeAgent(agent);
        systemTask.setTaskId(taskId);
        return systemTaskDAO.updateExecuteAgent(systemTask);
    }
    
    @Override
    public List<SystemTask> listSystemTaskByTaskKey(String taskKey)
    {
        return systemTaskDAO.listSystemTaskByTaskKey(taskKey);
    }
    
    @Override
    public List<SystemTask> listSystemTaskByParentTaskId(String pTaskId)
    {
        return systemTaskDAO.listSystemTaskByPTaskID(pTaskId);
    }
    
    @Override
    public SystemTask getOneWaitingExecuteTask(String pTaskId, String taskKey)
    {
        SystemTask subTask = null;
        SystemTask lockTask = null;
        while (true)
        {
            subTask = systemTaskTransactionService.getOneWaitingTask(pTaskId, taskKey);
            if (null == subTask)
            {
                return null;
            }
            
            lockTask = systemTaskTransactionService.lockAndUpdateOneWaitingExeTask(subTask);
            if (null != lockTask)
            {
                return lockTask;
            }
        }
    }
    
    @Override
    public int deleteTaskByTaskKey(String taskKey)
    {
        return systemTaskDAO.deleteTaskByTaskKey(taskKey);
    }
    
    @Override
    public int updateTaskState(int state, String taskId)
    {
        SystemTask systemTask = new SystemTask();
        systemTask.setState(state);
        systemTask.setTaskId(taskId);
        return systemTaskDAO.updateTaskState(systemTask);
    }
    
    @Override
    public boolean checkExistingTask(String pTask)
    {
        return systemTaskDAO.checkExistingTask(pTask);
    }
    
    @Override
    public List<SystemTask> listSystemTaskByTaskKeyAndState(String taskKey, int state)
    {
        SystemTask task = new SystemTask();
        task.setTaskKey(taskKey);
        task.setState(state);
        return systemTaskDAO.listSystemTaskByTaskKeyAndState(task);
    }
    
    @Override
    public long getSystemTaskTotalsByTaskKeyAndState(String taskKey, int state)
    {
        SystemTask task = new SystemTask();
        task.setTaskKey(taskKey);
        task.setState(state);
        return systemTaskDAO.getSystemTaskTotalsByTaskKeyAndState(task);
    }
    
    @Override
    public TaskExecuteInfo getTaskExecuteInfo(String taskKey)
    {
        TaskExecuteInfo info = new TaskExecuteInfo();
        SystemTask task = new SystemTask();
        task.setTaskKey(taskKey);
        task.setState(SystemTask.TASK_STATE_BEGIN);
        long taskStateBeginNumber = systemTaskDAO.getSystemTaskTotalsByTaskKeyAndState(task);
        info.setTaskStateBeginNumber(taskStateBeginNumber);
        
        task.setState(SystemTask.TASK_STATE_RUNING);
        long taskStateRunningNumber = systemTaskDAO.getSystemTaskTotalsByTaskKeyAndState(task);
        info.setTaskStateRunningNumber(taskStateRunningNumber);
        
        task.setState(SystemTask.TASK_STATE_END);
        long taskStateEndNumber = systemTaskDAO.getSystemTaskTotalsByTaskKeyAndState(task);
        info.setTaskStateEndNumber(taskStateEndNumber);
        
        task.setState(SystemTask.TASK_STATE_ERROR);
        long taskStateErrorNumber = systemTaskDAO.getSystemTaskTotalsByTaskKeyAndState(task);
        info.setTaskStateErrorNumber(taskStateErrorNumber);
        
        return info;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateTask(SystemTask task)
    {
        
        systemTaskDAO.updateTask(task);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void createSingleTask(SystemTask task)
    {
        systemTaskDAO.createTask(task);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int deleteTask(String taskID)
    {
        
        return systemTaskDAO.deleteByTaskID(taskID);
    }
    
}
