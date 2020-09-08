package com.huawei.sharedrive.app.common.systemtask.service;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.domain.TaskExecuteInfo;

public interface SystemTaskService
{
    
    /**
     * 创建任务
     * 
     * @param Task
     * @return
     */
    void createTask(List<SystemTask> task);
    
    /**
     * 创建单个任务
     * @param task
     */
    void createSingleTask(SystemTask task);
    
    /** 
     * 更新任务执行情况(最后更新时间, 当前状态)
     * 
     * @param state
     * @param time
     * @param taskId
     * @return 
     */
    int updateExecuteState(int state, Date updateTime, String taskId);
    
    /** 
     * 更新任务的执行agent
     * 
     * @param agent
     * @param taskId
     * @return 
     */
    int updateExecuteAgent(String agent,  String taskId);
    
    /**
     * 获取某一类型的任务
     * 
     * @param taskKey
     * @return
     */
    List<SystemTask> listSystemTaskByTaskKey(String taskKey);
    
    /**
     * 获取某一任务的状态
     * @param taskKey
     * @param state
     * @return
     */
    List<SystemTask> listSystemTaskByTaskKeyAndState(String taskKey,int state);
    
    
    /**
     * 获取某一任务的状态
     * @param taskKey
     * @param state
     * @return
     */
    long getSystemTaskTotalsByTaskKeyAndState(String taskKey,int state);
    
    /**
     * 根据父任务获取子任务
     * 
     * @param pTask
     * @return
     */
    List<SystemTask> listSystemTaskByParentTaskId(String pTaskId);
    
    /**
     * 获取一个等待执行的任务
     * 
     * @param taskKey
     * @return
     */
    SystemTask getOneWaitingExecuteTask(String pTaskId, String taskKey);
    
    /**
     * 删除任务
     * @param taskKey
     * @return
     */
    int deleteTaskByTaskKey(String taskKey);
    
    /** 
     * 更新任务状态
     * 
     * @param state
     * @param taskId
     * @return 
     */
    int updateTaskState(int state, String taskId);
    
    /** 
     * 判断是否还有需要执行的任务
     * 
     * @param pTask
     * @return 
     */
    boolean checkExistingTask(String pTask);
    
    /**
     * 获取任务执行信息
     * @param taskKe
     * @return
     */
    TaskExecuteInfo getTaskExecuteInfo(String taskKey);
    
    /**
     * 更新任务
     * @param task
     * @return
     */
    void updateTask(SystemTask task);
    
    
    /**
     * 删除任务
     * @param task
     * @return
     */
    int deleteTask(String taskID);
    
   
}
