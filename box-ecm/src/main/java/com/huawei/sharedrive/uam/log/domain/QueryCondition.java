package com.huawei.sharedrive.uam.log.domain;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import pw.cdmi.box.domain.PageRequest;

public class QueryCondition
{
    private int operateType = -1;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    
    private String admin;
    
    private String operater;
    
    private PageRequest pageRequest;
    
    private long adminId;
    
    private String appId;
    
    public int getOperateType()
    {
        return operateType;
    }
    
    public void setOperateType(int operateType)
    {
        this.operateType = operateType;
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
    
    public String getOperater()
    {
        return operater;
    }
    
    public void setOperater(String operater)
    {
        this.operater = operater;
    }
    
    public long getAdminId()
    {
        return adminId;
    }
    
    public void setAdminId(long adminId)
    {
        this.adminId = adminId;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
}
