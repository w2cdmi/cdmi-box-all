package pw.cdmi.box.disk.teamspace.domain;

import java.util.Date;

public class RestTeamSpaceInfo
{
    private long id;
    
    private String name;

    private int type;

    private String description;
    
    private byte status;
    
    private long curNumbers;
    
    private long ownedBy;
    
    private long createdBy;
    
    private String ownedByUserName;
    
    private String createdByUserName;
    
    private Date createdAt;
    
    private long spaceQuota;
    
    private long spaceUsed;
    
    private int maxMembers;
    
    private int maxVersions;
    
    private String uploadNotice;

    public Date getCreatedAt() {
        if (createdAt == null) {
            return null;
        }
        return (Date) createdAt.clone();
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
    
    public String getUploadNotice()
    {
        return uploadNotice;
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

    public void setUploadNotice(String uploadNotice) {
        this.uploadNotice = uploadNotice;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
