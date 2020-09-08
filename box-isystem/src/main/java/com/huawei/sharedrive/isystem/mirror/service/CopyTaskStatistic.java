package com.huawei.sharedrive.isystem.mirror.service;

import java.util.Date;

import pw.cdmi.core.utils.JsonUtils;

/**
 * 任务数统计
 * 
 * @author c00287749
 * 
 */

public class CopyTaskStatistic
{
    
    private Date statTime;
    
    // 当前总任务数
    private long allTaskNum;
    
    private long allSize;
    
    // 正在执行的任务数
    private long exeingTaskNum;
    
    private long exeingTaskSize;
    
    // 等待执行的任务数
    private long waitingTaskNum;
    
    private long waitingTaskSize;
    
    // 定时执行的任务数
    private long noactivateTaskNum;
    
    private long noactivateTaskSize;
    
    // 执行错误的任务
    private long failedTaskNum;
    
    private long failedTaskSize;
    
    public long getFailedTaskNum()
    {
        return failedTaskNum;
    }
    
    public void setFailedTaskNum(long failedTaskNum)
    {
        this.failedTaskNum = failedTaskNum;
    }
    
    public long getAllTaskNum()
    {
        return allTaskNum;
    }
    
    public void setAllTaskNum(long allTaskNum)
    {
        this.allTaskNum = allTaskNum;
    }
    
    public long getExeingTaskNum()
    {
        return exeingTaskNum;
    }
    
    public void setExeingTaskNum(long exeingTaskNum)
    {
        this.exeingTaskNum = exeingTaskNum;
    }
    
    public long getWaitingTaskNum()
    {
        return waitingTaskNum;
    }
    
    public void setWaitingTaskNum(long waitingTaskNum)
    {
        this.waitingTaskNum = waitingTaskNum;
    }
    
    public long getNoactivateTaskNum()
    {
        return noactivateTaskNum;
    }
    
    public void setNoactivateTaskNum(long noactivateTaskNum)
    {
        this.noactivateTaskNum = noactivateTaskNum;
    }
    
    public long getExeingTaskSize()
    {
        return exeingTaskSize;
    }
    
    public void setExeingTaskSize(long exeingTaskSize)
    {
        this.exeingTaskSize = exeingTaskSize;
    }
    
    public long getWaitingTaskSize()
    {
        return waitingTaskSize;
    }
    
    public void setWaitingTaskSize(long waitingTaskSize)
    {
        this.waitingTaskSize = waitingTaskSize;
    }
    
    public long getNoactivateTaskSize()
    {
        return noactivateTaskSize;
    }
    
    public void setNoactivateTaskSize(long noactivateTaskSize)
    {
        this.noactivateTaskSize = noactivateTaskSize;
    }
    
    public long getFailedTaskSize()
    {
        return failedTaskSize;
    }
    
    public void setFailedTaskSize(long failedTaskSize)
    {
        this.failedTaskSize = failedTaskSize;
    }
    
    public String toOutJsonStr()
    {
        return JsonUtils.toJson(this);
    }
    
    public long getAllSize()
    {
        return allSize;
    }
    
    public void setAllSize(long allSize)
    {
        this.allSize = allSize;
    }
    
    public Date getStatTime()
    {
        return statTime != null ? new Date(statTime.getTime()) : null;
    }
    
    public void setStatTime(Date statTime)
    {
        this.statTime = statTime != null ? new Date(statTime.getTime()) : null;
    }
    
}
