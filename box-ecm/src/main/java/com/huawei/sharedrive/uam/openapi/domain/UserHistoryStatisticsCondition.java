package com.huawei.sharedrive.uam.openapi.domain;

import java.util.Date;

public class UserHistoryStatisticsCondition
{
    
    private String appId;
    
    private String authAppId;
    
    private Date beginTime;
    
    private Date endTime;
    
    private String interval;
    
    private Integer regionId;
    
    public String getAppId()
    {
        return appId;
    }
    
    public String getAuthAppId()
    {
        return authAppId;
    }
    
    public Date getBeginTime()
    {
        if (null != beginTime)
        {
            return (Date) beginTime.clone();
        }
        return null;
    }
    
    public Date getEndTime()
    {
        if (null != endTime)
        {
            return (Date) endTime.clone();
        }
        return null;
    }
    
    public String getInterval()
    {
        return interval;
    }
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setAuthAppId(String authAppId)
    {
        this.authAppId = authAppId;
    }
    
    public void setBeginTime(Date beginTime)
    {
        if (null != beginTime)
        {
            this.beginTime = (Date) beginTime.clone();
        }
        else
        {
            this.beginTime = null;
        }
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
    
    public void setInterval(String interval)
    {
        this.interval = interval;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
}
