package com.huawei.sharedrive.uam.enterprise.domain;

import java.util.Date;

public class AccountSecConfigExt
{
    
    private int accountId;
    
    private Date createdAt;
    
    private Byte enableFileSec;
    
    private Byte enableSpaceSec;
    
    private Byte enableFileCopySec;
    
    private Date modifiedAt;
    
    public int getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(int accountId)
    {
        this.accountId = accountId;
    }
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
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
    
    public Byte getEnableFileSec()
    {
        return enableFileSec;
    }
    
    public void setEnableFileSec(Byte enableFileSec)
    {
        this.enableFileSec = enableFileSec;
    }
    
    public Byte getEnableSpaceSec()
    {
        return enableSpaceSec;
    }
    
    public void setEnableSpaceSec(Byte enableSpaceSec)
    {
        this.enableSpaceSec = enableSpaceSec;
    }
    
    public Date getModifiedAt()
    {
        if (null != modifiedAt)
        {
            return (Date) modifiedAt.clone();
        }
        return null;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (null != modifiedAt)
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
        else
        {
            this.modifiedAt = null;
        }
    }
    
    public Byte getEnableFileCopySec()
    {
        return enableFileCopySec;
    }
    
    public void setEnableFileCopySec(Byte enableFileCopySec)
    {
        this.enableFileCopySec = enableFileCopySec;
    }
    
}
