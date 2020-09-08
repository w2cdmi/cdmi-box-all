package com.huawei.sharedrive.isystem.common.systemtask.domain;

import java.util.Date;

/**
 * 
 * @author c00287749
 * 
 */
public class SystemTask
{
    
    public final static int TASK_STATE_BEGIN = 0;
    
    public final static int TASK_STATE_RUNING = 1;
    
    public final static int TASK_STATE_END = 2;
    
    public final static int TASK_STATE_ERROR = 3;
    
    private String taskId;
    
    private String pTaskId;
    
    private int state;
    
    private Date createTime;
    
    private String exeAgent;
    
    private String taskInfo;
    
    private Date exeUpdateTime;
    
    private String exeRuningInfo;
    
    private String taskKey;
    
    private long timeout;
    
    public Date getCreateTime()
    {
        return createTime != null ? new Date(createTime.getTime()) : null;
    }
    
    public String getExeAgent()
    {
        return exeAgent;
    }
    
    public String getExeRuningInfo()
    {
        return exeRuningInfo;
    }
    
    public Date getExeUpdateTime()
    {
        return exeUpdateTime != null ? new Date(exeUpdateTime.getTime()) : null;
    }
    
    public String getpTaskId()
    {
        return pTaskId;
    }
    
    public int getState()
    {
        return state;
    }
    
    public String getTaskId()
    {
        return taskId;
    }
    
    public String getTaskInfo()
    {
        return taskInfo;
    }
    
    public String getTaskKey()
    {
        return taskKey;
    }
    
    public long getTimeout()
    {
        return timeout;
    }
    
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
    }
    
    public void setExeAgent(String exeAgent)
    {
        this.exeAgent = exeAgent;
    }
    
    public void setExeRuningInfo(String exeRuningInfo)
    {
        this.exeRuningInfo = exeRuningInfo;
    }
    
    public void setExeUpdateTime(Date exeUpdateTime)
    {
        this.exeUpdateTime = exeUpdateTime != null ? new Date(exeUpdateTime.getTime()) : null;
    }
    
    public void setpTaskId(String pTaskId)
    {
        this.pTaskId = pTaskId;
    }
    
    public void setState(int state)
    {
        this.state = state;
    }
    
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }
    
    public void setTaskInfo(String taskInfo)
    {
        this.taskInfo = taskInfo;
    }
    
    public void setTaskKey(String taskKey)
    {
        this.taskKey = taskKey;
    }
    
    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }
    
}
