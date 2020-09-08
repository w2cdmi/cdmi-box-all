package com.huawei.sharedrive.isystem.common.systemtask.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.isystem.common.systemtask.dao.SystemTaskDAO;
import com.huawei.sharedrive.isystem.common.systemtask.domain.SystemTask;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;

@Repository
public class SystemTaskDAOImpl extends AbstractDAOImpl implements SystemTaskDAO
{
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean checkExistingTask(String pTask)
    {
        if (null != sqlMapClientTemplate.queryForObject("SystemTask.checkExistingTask", pTask))
        {
            return true;
        }
        return false;
        
    }
    
    @Override
    public boolean checkTaskIsRuning(SystemTask task)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @SuppressWarnings({"deprecation"})
    @Override
    public void createTask(SystemTask task)
    {
        
        sqlMapClientTemplate.insert("SystemTask.insert", task);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int deleteByPTaskID(String pTaskID)
    {
        
        return sqlMapClientTemplate.delete("SystemTask.deleteByPTaskID", pTaskID);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int deleteByTaskID(String taskID)
    {
        return sqlMapClientTemplate.delete("SystemTask.deleteByTaskID", taskID);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int deleteByTaskKeyAndStatus(String taskKey, int status)
    {
        SystemTask task = new SystemTask();
        task.setTaskKey(taskKey);
        task.setState(status);
        return sqlMapClientTemplate.delete("SystemTask.deleteByTaskKeyAndStatus", task);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int deleteTaskByTaskKey(String taskKey)
    {
        return sqlMapClientTemplate.delete("SystemTask.deleteByTaskKey", taskKey);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public SystemTask getOneWaitingExeTaskByTaskKey(SystemTask task)
    {
        
        return (SystemTask) sqlMapClientTemplate.queryForObject("SystemTask.getOneTaskByTaskKey", task);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public long getSystemTaskTotalsByTaskKeyAndState(SystemTask task)
    {
        return (Long) sqlMapClientTemplate.queryForObject("SystemTask.getSystemTaskTotalsByTaskKeyAndState",
            task);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<SystemTask> listSystemTaskByPTaskID(String pTask)
    {
        
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("pTask", pTask);
        return sqlMapClientTemplate.queryForList("SystemTask.listSystemTaskByPTaskID", map);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<SystemTask> listSystemTaskByPTaskID(String pTask, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("pTask", pTask);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("SystemTask.listSystemTaskByPTaskID", map);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<SystemTask> listSystemTaskByTaskKey(String taskKey)
    {
        
        return sqlMapClientTemplate.queryForList("SystemTask.listSystemTaskByTaskKey", taskKey);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<SystemTask> listSystemTaskByTaskKeyAndState(SystemTask task)
    {
        
        return sqlMapClientTemplate.queryForList("SystemTask.listSystemTaskByTaskKeyAndState", task);
    }
    
    @SuppressWarnings({"deprecation"})
    @Override
    public int updateExecuteAgent(SystemTask task)
    {
        return sqlMapClientTemplate.update("SystemTask.updateExecuteAgent", task);
    }
    
    @SuppressWarnings({"deprecation"})
    @Override
    public int updateExecuteState(SystemTask task)
    {
        
        return sqlMapClientTemplate.update("SystemTask.updateExecuteState", task);
    }
    
    @SuppressWarnings({"deprecation"})
    @Override
    public int updateTask(SystemTask task)
    {
        
        return sqlMapClientTemplate.update("SystemTask.update", task);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int updateTaskState(SystemTask task)
    {
        return sqlMapClientTemplate.update("SystemTask.updateState", task);
    }
    
}
