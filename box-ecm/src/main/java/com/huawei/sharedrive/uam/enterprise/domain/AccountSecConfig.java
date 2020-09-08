package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;

public class AccountSecConfig implements Serializable
{  
    private static final long serialVersionUID = 2625282806063396120L;
    
    public final static byte ENABLE = 1;
    
    public final static byte DISABLE = 0;
    
    private int accountId;
    
    private Date createdAt;
    
    private byte enableFileSec;
    
    private byte enableSpaceSec;
    
    private byte enableFileCopySec;
    
    private Date modifiedAt;
    
    public int getAccountId()
    {
        return accountId;
    }
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
    }
    
    public byte getEnableFileSec()
    {
        return enableFileSec;
    }
    
    public byte getEnableSpaceSec()
    {
        return enableSpaceSec;
    }
    
    public Date getModifiedAt()
    {
        if (null != modifiedAt)
        {
            return (Date) modifiedAt.clone();
        }
        return null;
    }
    
    public void setAccountId(int accountId)
    {
        this.accountId = accountId;
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
    
    public void setEnableFileSec(byte enableFileSec)
    {
        this.enableFileSec = enableFileSec;
    }
    
    public void setEnableSpaceSec(byte enableSpaceSec)
    {
        this.enableSpaceSec = enableSpaceSec;
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
    
    public byte getEnableFileCopySec()
    {
        return enableFileCopySec;
    }
    
    public void setEnableFileCopySec(byte enableFileCopySec)
    {
        this.enableFileCopySec = enableFileCopySec;
    }
    
}
