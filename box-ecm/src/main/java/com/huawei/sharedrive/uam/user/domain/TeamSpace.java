package com.huawei.sharedrive.uam.user.domain;

import java.io.Serializable;
import java.util.Date;

public class TeamSpace implements Serializable
{
    
    private static final long serialVersionUID = -2877327421977085389L;
    
    private String appId;
    
    private Date createdAt;
    
    private long createdBy;
    
    private String createdByUserName;
    
    private long curNumbers;
    
    private String description;
    
    private long id;
    
    private int maxMembers;
    
    private int maxVersions;
    
    private String name;
    
    private long ownedBy;
    
    private String ownedByUserName;
    
    private long spaceQuota;
    
    private long spaceUsed;
    
    private byte status;
    
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
    
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    public String getCreatedByUserName()
    {
        return createdByUserName;
    }
    
    public long getCurNumbers()
    {
        return curNumbers;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public long getId()
    {
        return id;
    }
    
    public int getMaxMembers()
    {
        return maxMembers;
    }
    
    public int getMaxVersions()
    {
        return maxVersions;
    }
    
    public String getName()
    {
        return name;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public String getOwnedByUserName()
    {
        return ownedByUserName;
    }
    
    public long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public byte getStatus()
    {
        return status;
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
    
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setCreatedByUserName(String createdByUserName)
    {
        this.createdByUserName = createdByUserName;
    }
    
    public void setCurNumbers(long curNumbers)
    {
        this.curNumbers = curNumbers;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setMaxMembers(int maxMembers)
    {
        this.maxMembers = maxMembers;
    }
    
    public void setMaxVersions(int maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setOwnedByUserName(String ownedByUserName)
    {
        this.ownedByUserName = ownedByUserName;
    }
    
    public void setSpaceQuota(long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public void setSpaceUsed(long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
}
