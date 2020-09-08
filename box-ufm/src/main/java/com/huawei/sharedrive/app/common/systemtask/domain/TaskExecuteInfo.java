package com.huawei.sharedrive.app.common.systemtask.domain;

/**
 * 执行任务信息
 * 
 * @author c00287749
 * 
 */
public class TaskExecuteInfo
{
    private long taskStateBeginNumber =0L;
    
    private long taskStateRunningNumber =0L;
    
    private long taskStateEndNumber =0L;
    
    private long taskStateErrorNumber =0L;
    
    public long getTaskStateBeginNumber()
    {
        return taskStateBeginNumber;
    }
    
    public void setTaskStateBeginNumber(long taskStateBeginNumber)
    {
        this.taskStateBeginNumber = taskStateBeginNumber;
    }
    
    public long getTaskStateRunningNumber()
    {
        return taskStateRunningNumber;
    }
    
    public void setTaskStateRunningNumber(long taskStateRunningNumber)
    {
        this.taskStateRunningNumber = taskStateRunningNumber;
    }
    
    public long getTaskStateEndNumber()
    {
        return taskStateEndNumber;
    }
    
    public void setTaskStateEndNumber(long taskStateEndNumber)
    {
        this.taskStateEndNumber = taskStateEndNumber;
    }
    
    public long getTaskStateErrorNumber()
    {
        return taskStateErrorNumber;
    }
    
    public void setTaskStateErrorNumber(long taskStateErrorNumber)
    {
        this.taskStateErrorNumber = taskStateErrorNumber;
    }
    
}
