package com.huawei.sharedrive.uam.enterpriseuser.domain;

import java.io.Serializable;
import java.util.Date;

public class LdapCheck implements Serializable
{
    private static final long serialVersionUID = 9097124770941983177L;
    
    public static final Byte CHECK_FINISHED = 1;
    
    public static final Byte CHECK_UNFINISH = 2;
    
    private Long enterpriseId;
    
    private Date lastCheckTime;
    
    private Byte checkStatus = 1;
    
    private Long userCount = 0L;
    
    public Long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public Date getLastCheckTime()
    {
        if (null != lastCheckTime)
        {
            return (Date) lastCheckTime.clone();
        }
        return null;
    }
    
    public void setLastCheckTime(Date lastCheckTime)
    {
        if (null != lastCheckTime)
        {
            this.lastCheckTime = (Date) lastCheckTime.clone();
        }
        else
        {
            this.lastCheckTime = null;
        }
    }
    
    public Byte getCheckStatus()
    {
        return checkStatus;
    }
    
    public void setCheckStatus(Byte checkStatus)
    {
        this.checkStatus = checkStatus;
    }
    
    public Long getUserCount()
    {
        return userCount;
    }
    
    public void setUserCount(Long userCount)
    {
        this.userCount = userCount;
    }
    
}
