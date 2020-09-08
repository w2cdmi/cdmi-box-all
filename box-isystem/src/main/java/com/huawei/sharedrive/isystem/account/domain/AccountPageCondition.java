package com.huawei.sharedrive.isystem.account.domain;

import java.util.Date;

import pw.cdmi.box.domain.PageRequest;

public class AccountPageCondition
{
    private Date startTime;
    
    private Date endTime;
    
    private String name;
    
    private String appId;
    
    /** 分页参数 */
    private PageRequest pageRequest;

    public Date getStartTime()
    {
        if (this.startTime != null)
        {
            return new Date(this.startTime.getTime());
        }
        return null;
    }

    public void setStartTime(Date startTime)
    {
        if(startTime != null)
        {
            this.startTime = new Date(startTime.getTime());
        }
        else
        {
            this.startTime = null;
        }
    }

    public Date getEndTime()
    {
        if (this.endTime != null)
        {
            return new Date(this.endTime.getTime());
        }
        return null;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime != null ? new Date(endTime.getTime()) : null;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
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
