package com.huawei.sharedrive.uam.user.domain;

import java.io.Serializable;
import java.util.Date;

public class UserLocked implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -9042194348664524129L;
    
    private String appId;
    
    private Date createdAt;
    
    private Date lockedAt;
    
    private int loginFailTimes;
    
    private String loginName;
    
    private long userId;
    
    private String userName;
    
    private Date loginDate;
    
    private int loginTimes;
    
    public String getAppId()
    {
        return appId;
    }
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
    }
    
    public Date getLockedAt()
    {
        if (null != lockedAt)
        {
            return (Date) lockedAt.clone();
        }
        return null;
    }
    
    public int getLoginFailTimes()
    {
        return loginFailTimes;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (null != createdAt)
        {
            this.createdAt = (Date) createdAt.clone();
        }
        else
        {
            this.createdAt = null;
        }
    }
    
    public void setLockedAt(Date lockedAt)
    {
        if (null != lockedAt)
        {
            this.lockedAt = (Date) lockedAt.clone();
        }
        else
        {
            this.lockedAt = null;
        }
        
    }
    
    public void setLoginFailTimes(int loginFailTimes)
    {
        this.loginFailTimes = loginFailTimes;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public String getUserName()
    {
        return userName;
    }
    
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
    public Date getLoginDate()
    {
        return loginDate != null ? new Date(loginDate.getTime()) : null;
    }
    
    public void setLoginDate(Date loginDate)
    {
        this.loginDate = loginDate != null ? new Date(loginDate.getTime()) : null;
    }
    
    public int getLoginTimes()
    {
        return loginTimes;
    }
    
    public void setLoginTimes(int loginTimes)
    {
        this.loginTimes = loginTimes;
    }
}
