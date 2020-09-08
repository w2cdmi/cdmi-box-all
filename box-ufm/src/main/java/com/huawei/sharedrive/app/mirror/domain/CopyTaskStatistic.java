package com.huawei.sharedrive.app.mirror.domain;

import java.util.Date;

import pw.cdmi.core.utils.JsonUtils;

/**
 * 任务数统计
 * 
 * @author c00287749
 * 
 * 
 */

public class CopyTaskStatistic
{
    
    private long allSize;
    
    private long allTaskNum;
    
    private long exeingTaskNum;
    
    private long exeingTaskSize;
    
    private long failedTaskNum;
    
    private long failedTaskSize;
    
    // 定时执行的任务数
    private long noactivateTaskNum;
    
    private long noactivateTaskSize;
    
    private Date statTime;
    
    private long waitingTaskNum;
    
    private long waitingTaskSize;
    
    public long getAllSize()
    {
        return allSize;
    }
    
    public long getAllTaskNum()
    {
        return allTaskNum;
    }
    
    public long getExeingTaskNum()
    {
        return exeingTaskNum;
    }
    
    public long getExeingTaskSize()
    {
        return exeingTaskSize;
    }
    
    public long getFailedTaskNum()
    {
        return failedTaskNum;
    }
    
    public long getFailedTaskSize()
    {
        return failedTaskSize;
    }
    
    public long getNoactivateTaskNum()
    {
        return noactivateTaskNum;
    }
    
    public long getNoactivateTaskSize()
    {
        return noactivateTaskSize;
    }
    
    public Date getStatTime()
    {
        if (statTime == null)
        {
            return null;
        }
        return (Date) statTime.clone();
    }
    
    public long getWaitingTaskNum()
    {
        return waitingTaskNum;
    }
    
    public long getWaitingTaskSize()
    {
        return waitingTaskSize;
    }
    
    public void setAllSize(long allSize)
    {
        this.allSize = allSize;
    }
    
    public void setAllTaskNum(long allTaskNum)
    {
        this.allTaskNum = allTaskNum;
    }
    
    public void setExeingTaskNum(long exeingTaskNum)
    {
        this.exeingTaskNum = exeingTaskNum;
    }
    
    public void setExeingTaskSize(long exeingTaskSize)
    {
        this.exeingTaskSize = exeingTaskSize;
    }
    
    public void setFailedTaskNum(long failedTaskNum)
    {
        this.failedTaskNum = failedTaskNum;
    }
    
    public void setFailedTaskSize(long failedTaskSize)
    {
        this.failedTaskSize = failedTaskSize;
    }
    
    public void setNoactivateTaskNum(long noactivateTaskNum)
    {
        this.noactivateTaskNum = noactivateTaskNum;
    }
    
    public void setNoactivateTaskSize(long noactivateTaskSize)
    {
        this.noactivateTaskSize = noactivateTaskSize;
    }
    
    public void setStatTime(Date statTime)
    {
        if (statTime == null)
        {
            this.statTime = null;
        }
        else
        {
            this.statTime = (Date) statTime.clone();
        }
    }
    
    public void setWaitingTaskNum(long waitingTaskNum)
    {
        this.waitingTaskNum = waitingTaskNum;
    }
    
    public void setWaitingTaskSize(long waitingTaskSize)
    {
        this.waitingTaskSize = waitingTaskSize;
    }
    
    public String toOutJsonStr()
    {
        return JsonUtils.toJson(this);
    }
    
}
