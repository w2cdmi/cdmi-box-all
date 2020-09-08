package com.huawei.sharedrive.isystem.user.domain;

import java.io.Serializable;
import java.util.Date;

public class ManagerLocked implements Serializable
{
    
    private static final long serialVersionUID = -4629230336901587022L;
    
    private Date createdAt;
    
    private Date lockedAt;
    
    private int loginFailTimes;
    
    private String loginName;
    
    public Date getCreatedAt()
    {
        if (this.createdAt != null)
        {
            return new Date(this.createdAt.getTime());
        }
        return null;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt != null)
        {
            this.createdAt = new Date(createdAt.getTime());
        }
    }
    
    public Date getLockedAt()
    {
        if (this.lockedAt != null)
        {
            return new Date(this.lockedAt.getTime());
        }
        return null;
    }
    
    public void setLockedAt(Date lockedAt)
    {
        if (lockedAt != null)
        {
            this.lockedAt = new Date(lockedAt.getTime());
        }
    }
    
    public int getLoginFailTimes()
    {
        return loginFailTimes;
    }
    
    public void setLoginFailTimes(int loginFailTimes)
    {
        this.loginFailTimes = loginFailTimes;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
}
