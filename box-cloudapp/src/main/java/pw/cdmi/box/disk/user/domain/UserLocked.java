package pw.cdmi.box.disk.user.domain;

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
    
    private Date loginDate;
    
    private int loginTimes;
    
    public String getAppId()
    {
        return appId;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public Date getLockedAt()
    {
        if (lockedAt == null)
        {
            return null;
        }
        return (Date) lockedAt.clone();
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
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
    }
    
    public void setLockedAt(Date lockedAt)
    {
        if (lockedAt == null)
        {
            this.lockedAt = null;
        }
        else
        {
            this.lockedAt = (Date) lockedAt.clone();
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
