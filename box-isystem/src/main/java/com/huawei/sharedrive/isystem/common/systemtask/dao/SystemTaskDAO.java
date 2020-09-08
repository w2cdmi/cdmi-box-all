package com.huawei.sharedrive.isystem.common.systemtask.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.common.systemtask.domain.SystemTask;

import pw.cdmi.box.domain.Limit;

public interface SystemTaskDAO
{
    
    boolean checkExistingTask(String pTask);
    
    /**
     * 检测任务是否存有执行中
     * 
     * @param Task
     * @return
     */
    boolean checkTaskIsRuning(SystemTask task);
    
    /**
     * 创建任务
     * 
     * @param Task
     * @return
     */
    void createTask(SystemTask task);
    
    /**
     * 删除任务
     * 
     * @param pTaskID
     * @return
     */
    int deleteByPTaskID(String pTaskID);
    
    /**
     * 删除任务
     * 
     * @param taskID
     * @return
     */
    int deleteByTaskID(String taskID);
    
    /**
     * 根据key和任务状态删除任务
     * 
     * @param taskKey
     * @param status
     * @return
     */
    int deleteByTaskKeyAndStatus(String taskKey, int status);
    
    /**
     * 删除任务
     * 
     * @param taskKey
     * @return
     */
    int deleteTaskByTaskKey(String taskKey);
    
    /**
     * 获取一个等待执行的任务
     * 
     * @param taskKey
     * @return
     */
    SystemTask getOneWaitingExeTaskByTaskKey(SystemTask task);
    
    /**
     * 获取某种任务数量，需要填写taskKey和state值
     * 
     * @param task
     * @return
     */
    long getSystemTaskTotalsByTaskKeyAndState(SystemTask task);
    
    /**
     * 根据父任务获取子任务
     * 
     * @param pTask
     * @return
     */
    
    List<SystemTask> listSystemTaskByPTaskID(String pTask);
    
    /**
     * 根据父任务获取子任务
     * 
     * @param pTask
     * @param limit
     * @return
     */
    List<SystemTask> listSystemTaskByPTaskID(String pTask, Limit limit);
    
    /**
     * 获取任务
     * 
     * @param taskKey
     * @return
     */
    List<SystemTask> listSystemTaskByTaskKey(String taskKey);
    
    /**
     * 根据状态获取任务列举，需要填写taskKey和state值
     */
    List<SystemTask> listSystemTaskByTaskKeyAndState(SystemTask task);
    
    /**
     * 更新任务的执行Agent
     * 
     * @param Task
     * @return
     */
    int updateExecuteAgent(SystemTask task);
    
    /**
     * 更新运行状态(任务状态, 运行时间)
     * 
     * @param Task
     * @return
     */
    int updateExecuteState(SystemTask task);
    
    /**
     * 更新任务
     * 
     * @param Task
     * @return
     */
    int updateTask(SystemTask task);
    
    int updateTaskState(SystemTask task);
    
}
