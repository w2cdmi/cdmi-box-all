package com.huawei.sharedrive.isystem.common.systemtask.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.common.systemtask.dao.SystemTaskDAO;
import com.huawei.sharedrive.isystem.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.isystem.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.isystem.util.PropertiesUtils;

@Service("systemTaskService")
public class SystemTaskServiceImpl implements SystemTaskService
{
    @Autowired
    private SystemTaskDAO systemTaskDAO;
    
    @Override
    public boolean checkExistingTask(String pTask)
    {
        return systemTaskDAO.checkExistingTask(pTask);
    }
    
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
    public int deleteTaskByTaskKey(String taskKey)
    {
        return systemTaskDAO.deleteTaskByTaskKey(taskKey);
    }
    
    @Override
    public int deleteUnExecuteTask(String taskKey)
    {
        return systemTaskDAO.deleteByTaskKeyAndStatus(taskKey, SystemTask.TASK_STATE_BEGIN);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SystemTask getOneWaitingExecuteTask(String pTaskId, String taskKey)
    {
        SystemTask task = new SystemTask();
        task.setpTaskId(pTaskId);
        task.setTaskKey(taskKey);
        SystemTask subTask = systemTaskDAO.getOneWaitingExeTaskByTaskKey(task);
        if (null == subTask)
        {
            return null;
        }
        String agent = PropertiesUtils.getProperty("self.privateAddr", "127.0.0.1");
        subTask.setExeAgent(agent);
        if (1 == systemTaskDAO.updateExecuteAgent(subTask))
        {
            return subTask;
        }
        return null;
        
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
    public List<SystemTask> listSystemTaskByParentTaskId(String pTaskId)
    {
        return systemTaskDAO.listSystemTaskByPTaskID(pTaskId);
    }
    
    @Override
    public List<SystemTask> listSystemTaskByTaskKey(String taskKey)
    {
        return systemTaskDAO.listSystemTaskByTaskKey(taskKey);
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
    public int updateExecuteAgent(String agent, String taskId)
    {
        SystemTask systemTask = new SystemTask();
        systemTask.setExeAgent(agent);
        systemTask.setTaskId(taskId);
        return systemTaskDAO.updateExecuteAgent(systemTask);
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
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateTask(SystemTask task)
    {
        
        systemTaskDAO.updateTask(task);
    }
    
    @Override
    public int updateTaskState(int state, String taskId)
    {
        SystemTask systemTask = new SystemTask();
        systemTask.setState(state);
        systemTask.setTaskId(taskId);
        return systemTaskDAO.updateTaskState(systemTask);
    }
    
}
