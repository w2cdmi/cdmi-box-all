package pw.cdmi.box.uam.user.domain;

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
    
    /** 用戶userid */
    private String userName;
    
    /** 用戶登錄錯誤時間 */
    private Date loginDate;
    
    /** 用戶登錄錯誤次數 */
    private int loginTimes;
    
    public String getAppId()
    {
        return appId;
    }
    
    public Date getCreatedAt()
    {
        return createdAt == null ? null : (Date) createdAt.clone();
    }
    
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = (createdAt == null ? null : (Date) createdAt.clone());
    }
    
    public Date getLockedAt()
    {
        return lockedAt == null ? null : (Date) lockedAt.clone();
    }
    
    public void setLockedAt(Date lockedAt)
    {
        this.lockedAt = (lockedAt == null ? null : (Date) lockedAt.clone());
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
