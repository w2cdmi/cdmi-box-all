package com.huawei.sharedrive.app.common.systemtask.dao;

import java.util.List;

import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;

import pw.cdmi.box.domain.Limit;

public interface SystemTaskDAO
{
    
    /**
     * 创建任务
     * 
     * @param Task
     * @return
     */
    void createTask(SystemTask task);
    
    /**
     * 更新任务
     * 
     * @param Task
     * @return
     */
    int updateTask(SystemTask task);
    
    /**
     * 更新运行状态(任务状态, 运行时间)
     * 
     * @param Task
     * @return
     */
    int updateExecuteState(SystemTask task);
    
    /**
     * 检测任务是否存有执行中
     * 
     * @param Task
     * @return
     */
    boolean checkTaskIsRuning(SystemTask task);
    
    /**
     * 更新任务的执行Agent
     * 
     * @param Task
     * @return
     */
    int updateExecuteAgent(SystemTask task);
    
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
     * 获取一个等待执行的任务
     * 
     * @param taskKey
     * @return
     */
    SystemTask getOneWaitingExeTaskByTaskKey(SystemTask task);
    
    /**
     *获取到的具体执行任务并加锁，避免该任务同时被多个节点获取并执行
     * @param taskKey
     * @return
     */
    SystemTask getOneWaitingExeTaskAndLock(SystemTask task);
    
    
    /**
     * 删除任务
     * 
     * @param taskID
     * @return
     */
    int deleteByTaskID(String taskID);
    
    /**
     * 删除任务
     * 
     * @param pTaskID
     * @return
     */
    int deleteByPTaskID(String pTaskID);
    
    /**
     * 删除任务
     * @param taskKey
     * @return
     */
    int deleteTaskByTaskKey(String taskKey);
    
    int updateTaskState(SystemTask task);
    
    boolean checkExistingTask(String pTask);
    
    /**
     * 获取某种任务数量，需要填写taskKey和state值
     * @param task
     * @return
     */
    long getSystemTaskTotalsByTaskKeyAndState(SystemTask task);
    
}
