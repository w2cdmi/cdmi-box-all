package com.huawei.sharedrive.uam.log.domain;

import java.util.Date;

import pw.cdmi.box.domain.PageRequest;

public class QueryEventCondition
{
    private String type;
    
    private Date startTime;
    
    private Date endTime;
    
    private String admin;
    
    private PageRequest pageRequest;
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public Date getStartTime()
    {
        if (null != startTime)
        {
            return (Date) startTime.clone();
        }
        return null;
    }
    
    public void setStartTime(Date startTime)
    {
        if (null != startTime)
        {
            this.startTime = (Date) startTime.clone();
        }
        else
        {
            this.startTime = null;
        }
    }
    
    public Date getEndTime()
    {
        if (null != endTime)
        {
            return (Date) endTime.clone();
        }
        return null;
    }
    
    public void setEndTime(Date endTime)
    {
        if (null != endTime)
        {
            this.endTime = (Date) endTime.clone();
        }
        else
        {
            this.endTime = null;
        }
    }
    
    public String getAdmin()
    {
        return admin;
    }
    
    public void setAdmin(String admin)
    {
        this.admin = admin;
    }
    
    public PageRequest getPageRequest()
    {
        return pageRequest;
    }
    
    public void setPageRequest(PageRequest pageRequest)
    {
        this.pageRequest = pageRequest;
    }
}
