package com.huawei.sharedrive.app.spacestatistics.domain;

import java.io.Serializable;
import java.util.Date;

public class ClearRecycleBinRecord implements Serializable
{
    
    private static final long serialVersionUID = -7154537638445478550L;
    
    private long ownedBy;
    
    private Date createdAt;
    
    private long accountId;
    
    public ClearRecycleBinRecord(long ownedBy, Date createdAt, long accountId)
    {
        super();
        this.ownedBy = ownedBy;
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
        this.accountId = accountId;
    }
    
    public ClearRecycleBinRecord()
    {
        super();
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public Date getCreatedAt()
    {
        if(null != createdAt)
        {
            return (Date)createdAt.clone();
        }
        return null;
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
}
