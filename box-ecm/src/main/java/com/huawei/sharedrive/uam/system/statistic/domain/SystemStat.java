package com.huawei.sharedrive.uam.system.statistic.domain;

import java.io.Serializable;
import java.util.Date;

public class SystemStat implements Serializable
{
    private static final long serialVersionUID = 1609773054375275677L;
    
    private Date statDate;
    
    private Date createDate;
    
    private long totalUser;
    
    private long loginUserCount;
    
    private long webAccessAgentCount;
    
    private long pcAccessAgentCount;
    
    private long androidAccessCount;
    
    private long iosAccessCount;
    
    private String appId;
    
    public Date getStatDate()
    {
        if (null != statDate)
        {
            return (Date) statDate.clone();
        }
        return null;
    }
    
    public void setStatDate(Date statDate)
    {
        if (null != statDate)
        {
            this.statDate = (Date) statDate.clone();
        }
        else
        {
            this.statDate = null;
        }
    }
    
    public Date getCreateDate()
    {
        if (null != createDate)
        {
            return (Date) createDate.clone();
        }
        return null;
    }
    
    public void setCreateDate(Date createDate)
    {
        if (null != createDate)
        {
            this.createDate = (Date) createDate.clone();
        }
        else
        {
            this.createDate = null;
        }
    }
    
    public long getTotalUser()
    {
        return totalUser;
    }
    
    public void setTotalUser(long totalUser)
    {
        this.totalUser = totalUser;
    }
    
    public long getLoginUserCount()
    {
        return loginUserCount;
    }
    
    public void setLoginUserCount(long loginUserCount)
    {
        this.loginUserCount = loginUserCount;
    }
    
    public long getWebAccessAgentCount()
    {
        return webAccessAgentCount;
    }
    
    public void setWebAccessAgentCount(long webAccessAgentCount)
    {
        this.webAccessAgentCount = webAccessAgentCount;
    }
    
    public long getPcAccessAgentCount()
    {
        return pcAccessAgentCount;
    }
    
    public void setPcAccessAgentCount(long pcAccessAgentCount)
    {
        this.pcAccessAgentCount = pcAccessAgentCount;
    }
    
    public long getAndroidAccessCount()
    {
        return androidAccessCount;
    }
    
    public void setAndroidAccessCount(long androidAccessCount)
    {
        this.androidAccessCount = androidAccessCount;
    }
    
    public long getIosAccessCount()
    {
        return iosAccessCount;
    }
    
    public void setIosAccessCount(long iosAccessCount)
    {
        this.iosAccessCount = iosAccessCount;
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
