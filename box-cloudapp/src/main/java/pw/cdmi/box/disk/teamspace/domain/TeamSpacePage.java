package pw.cdmi.box.disk.teamspace.domain;

import java.util.Date;

public class TeamSpacePage
{
    private long id;
    
    private String name;
    
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
    
    private long memberId;
    
    private int roleType;
    
    private String userType;

    public long getId() {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public long getCurNumbers()
    {
        return curNumbers;
    }
    
    public void setCurNumbers(long curNumbers)
    {
        this.curNumbers = curNumbers;
    }
    
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
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
    
    public String getCreatedByUserName()
    {
        return createdByUserName;
    }
    
    public void setCreatedByUserName(String createdByUserName)
    {
        this.createdByUserName = createdByUserName;
    }
    
    public long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public void setSpaceQuota(long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public void setSpaceUsed(long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public String getOwnedByUserName()
    {
        return ownedByUserName;
    }
    
    public void setOwnedByUserName(String ownedByUserName)
    {
        this.ownedByUserName = ownedByUserName;
    }
    
    public int getMaxMembers()
    {
        return maxMembers;
    }
    
    public void setMaxMembers(int maxMembers)
    {
        this.maxMembers = maxMembers;
    }
    
    public int getMaxVersions()
    {
        return maxVersions;
    }
    
    public void setMaxVersions(int maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public long getMemberId()
    {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
    
    public int getRoleType()
    {
        return roleType;
    }
    
    public void setRoleType(int roleType)
    {
        this.roleType = roleType;
    }
    
    public String getUserType()
    {
        return userType;
    }
    
    public void setUserType(String userType)
    {
        this.userType = userType;
    }
    
}
