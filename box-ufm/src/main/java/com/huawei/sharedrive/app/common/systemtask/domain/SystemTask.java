package com.huawei.sharedrive.app.common.systemtask.domain;

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
    
    public String getTaskId()
    {
        return taskId;
    }
    
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }
    
    public String getpTaskId()
    {
        return pTaskId;
    }
    
    public void setpTaskId(String pTaskId)
    {
        this.pTaskId = pTaskId;
    }
    
    public int getState()
    {
        return state;
    }
    
    public void setState(int state)
    {
        this.state = state;
    }
    
    public Date getCreateTime()
    {
        if (createTime == null)
        {
            return null;
        }
        return (Date) createTime.clone();
    }
    
    public void setCreateTime(Date createTime)
    {
        if (createTime == null)
        {
            this.createTime = null;
        }
        else
        {
            this.createTime = (Date) createTime.clone();
        }
    }
    
    public String getExeAgent()
    {
        return exeAgent;
    }
    
    public void setExeAgent(String exeAgent)
    {
        this.exeAgent = exeAgent;
    }
    
    public String getTaskInfo()
    {
        return taskInfo;
    }
    
    public void setTaskInfo(String taskInfo)
    {
        this.taskInfo = taskInfo;
    }
    
    public Date getExeUpdateTime()
    {
        if (exeUpdateTime == null)
        {
            return null;
        }
        return (Date) exeUpdateTime.clone();
    }
    
    public void setExeUpdateTime(Date exeUpdateTime)
    {
        if (exeUpdateTime == null)
        {
            this.exeUpdateTime = null;
        }
        else
        {
            this.exeUpdateTime = (Date) exeUpdateTime.clone();
        }
    }
    
    public String getTaskKey()
    {
        return taskKey;
    }
    
    public void setTaskKey(String taskKey)
    {
        this.taskKey = taskKey;
    }
    
    public long getTimeout()
    {
        return timeout;
    }
    
    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }
    
    public String getExeRuningInfo()
    {
        return exeRuningInfo;
    }
    
    public void setExeRuningInfo(String exeRuningInfo)
    {
        this.exeRuningInfo = exeRuningInfo;
    }
    
}
