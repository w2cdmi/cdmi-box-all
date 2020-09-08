package pw.cdmi.box.uam.user.domain;

import java.io.Serializable;
import java.util.Date;

public class ManagerLocked implements Serializable
{
    
    private static final long serialVersionUID = -4629230336901587022L;
    
    private Date createdAt;
    
    private Date lockedAt;
    
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
    
    private int loginFailTimes;
    
    private String loginName;
    
}
