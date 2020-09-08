package com.huawei.sharedrive.isystem.plugin.domain;

import java.io.Serializable;

public class KIAProgress implements Serializable
{
    private static final long serialVersionUID = 1241630980502677315L;
    
    // 总共任务数
    private Integer total;
    
    // 已完成任务数
    private Integer completed;
    
    // 等待执行任务数
    private Integer waiting;
    
    public Integer getCompleted()
    {
        return completed;
    }
    
    public Integer getTotal()
    {
        return total;
    }
    
    public Integer getWaiting()
    {
        return waiting;
    }
    
    public void setCompleted(Integer completed)
    {
        this.completed = completed;
    }
    
    public void setTotal(Integer total)
    {
        this.total = total;
    }
    
    public void setWaiting(Integer waiting)
    {
        this.waiting = waiting;
    }
    
}
