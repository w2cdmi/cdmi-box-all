package com.huawei.sharedrive.app.spacestatistics.domain;

import java.io.Serializable;
import java.util.Date;

public class TemporaryUserInfo implements Serializable
{
    
    private static final long serialVersionUID = 5931724243162196426L;
    
    private long ownedBy;
    
    private Long accountId;
    
    private Long spaceUsed;
    
    private Long spaceChanged;
    
    private Date createdAt;
    
    private Long currentFileCount;
    
    private Long changedFileCount;
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public Long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public Long getSpaceChanged()
    {
        return spaceChanged;
    }
    
    public void setSpaceChanged(Long spaceChanged)
    {
        this.spaceChanged = spaceChanged;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt != null)
        {
            return (Date) createdAt.clone();
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
    
    public Long getCurrentFileCount()
    {
        return currentFileCount;
    }
    
    public void setCurrentFileCount(Long currentFileCount)
    {
        this.currentFileCount = currentFileCount;
    }
    
    public Long getChangedFileCount()
    {
        return changedFileCount;
    }
    
    public void setChangedFileCount(Long changedFileCount)
    {
        this.changedFileCount = changedFileCount;
    }
    
}
