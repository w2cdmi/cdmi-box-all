package pw.cdmi.box.uam.httpclient.domain;

import java.util.Date;

public class RestTeamSpaceInfo
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
    
    public long getId()
    {
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
        return createdAt == null ? null : (Date) createdAt.clone();
    }
    
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = (createdAt == null ? null : (Date) createdAt.clone());
    }
    
    //
    // public String getTeamRole()
    // {
    // return teamRole;
    // }
    //
    // public void setTeamRole(String teamRole)
    // {
    // this.teamRole = teamRole;
    // }
    //
    
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
    
}
