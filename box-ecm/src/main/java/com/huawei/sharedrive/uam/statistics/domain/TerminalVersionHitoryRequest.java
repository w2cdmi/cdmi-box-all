package com.huawei.sharedrive.uam.statistics.domain;

import java.io.Serializable;
import java.util.Date;

public class TerminalVersionHitoryRequest implements Serializable
{
    
    private static final long serialVersionUID = 7565066207224586515L;
    
    private Date beginTime;
    
    private Date endTime;
    
    private String interval;
    
    private int deviceType;
    
    public Date getBeginTime()
    {
        if (null != beginTime)
        {
            return (Date) beginTime.clone();
        }
        return null;
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
    
    public String getInterval()
    {
        return interval;
    }
    
    public void setInterval(String interval)
    {
        this.interval = interval;
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
}
