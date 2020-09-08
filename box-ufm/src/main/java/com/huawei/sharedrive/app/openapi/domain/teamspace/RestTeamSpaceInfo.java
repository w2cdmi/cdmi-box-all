package com.huawei.sharedrive.app.openapi.domain.teamspace;

import java.util.Date;

import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.utils.Constants;

import pw.cdmi.core.utils.DateUtils;

public class RestTeamSpaceInfo
{
    private Long id;
    
    private String name;

    private String description;
    
    private Byte status;
    
    private Long curNumbers;
    
    private Long ownerBy;
    
    private Long ownedBy;
    
    private Long createdBy;
    
    private String ownedByUserName;
    
    private String ownerByUserName;
    
    private String createdByUserName;
    
    private Date createdAt;
    
    private Long spaceQuota;
    
    private Long spaceUsed;
    
    private Integer maxMembers;
    
    private Integer maxVersions;
    
    private Byte newFileMsg;
    
    private int regionId;
    
    private int type;
    
	public RestTeamSpaceInfo()
    {
        
    }
    
    public RestTeamSpaceInfo(TeamSpace teamSpace)
    {
        this.id = teamSpace.getCloudUserId();
        this.name = teamSpace.getName();
        this.description = teamSpace.getDescription();
        this.status = (byte) teamSpace.getStatus();
        this.curNumbers = teamSpace.getCurNumbers();
        this.ownedBy = teamSpace.getOwnerBy();
        this.ownerBy = teamSpace.getOwnerBy();
        this.createdBy = teamSpace.getCreatedBy();
        long createAtTime = DateUtils.getDateTime(teamSpace.getCreatedAt()) / 1000 * 1000;
        this.createdAt = new Date(createAtTime);
        this.spaceQuota = teamSpace.getSpaceQuota();
        this.spaceUsed = teamSpace.getSpaceUsed();
        this.createdByUserName = teamSpace.getCreatedByUserName();
        this.ownedByUserName = teamSpace.getOwnerByUserName();
        this.maxMembers = teamSpace.getMaxMembers();
        this.maxVersions = teamSpace.getMaxVersions();
        this.type=teamSpace.getType();
        
        this.regionId = teamSpace.getRegionId();
        
        // 兼容老的客户端,IOS/Android
        if (Constants.IS_FILED_NAME_COMPATIBLE)
        {
            this.setOwnerBy(teamSpace.getOwnerBy());
            this.setOwnerByUserName(teamSpace.getOwnerByUserName());
        }
        
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public Long getCreatedBy()
    {
        return createdBy;
    }
    
    public String getCreatedByUserName()
    {
        return createdByUserName;
    }
    
    public Long getCurNumbers()
    {
        return curNumbers;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public Integer getMaxMembers()
    {
        return maxMembers;
    }
    
    public Integer getMaxVersions()
    {
        return maxVersions;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Byte getNewFileMsg()
    {
        return newFileMsg;
    }
    
//    public Long getOwnedBy()
//    {
//        return ownedBy;
//    }
    
    public String getOwnedByUserName()
    {
        return ownedByUserName;
    }
    
    public Long getOwnerBy()
    {
        return ownerBy;
    }
    
    public String getOwnerByUserName()
    {
        return ownerByUserName;
    }
    
    public Long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public Byte getStatus()
    {
        return status;
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
    
    public void setCreatedBy(Long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setCreatedByUserName(String createdByUserName)
    {
        this.createdByUserName = createdByUserName;
    }
    
    public void setCurNumbers(Long curNumbers)
    {
        this.curNumbers = curNumbers;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public void setMaxMembers(Integer maxMembers)
    {
        this.maxMembers = maxMembers;
    }
    
    public void setMaxVersions(Integer maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setNewFileMsg(Byte newFileMsg)
    {
        this.newFileMsg = newFileMsg;
    }
    
//    public void setOwnedBy(Long ownedBy)
//    {
//        this.ownedBy = ownedBy;
//    }
    
    public void setOwnedByUserName(String ownedByUserName)
    {
        this.ownedByUserName = ownedByUserName;
    }
    
    public void setOwnerBy(Long ownerBy)
    {
        this.ownerBy = ownerBy;
    }
    
    public void setOwnerByUserName(String ownerByUserName)
    {
        this.ownerByUserName = ownerByUserName;
    }
    
    public void setSpaceQuota(Long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public void setStatus(Byte status)
    {
        this.status = status;
    }

    public int getRegionId()
    {
        return regionId;
    }

    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

	public Long getOwnedBy() {
		return ownedBy;
	}

	public void setOwnedBy(Long ownedBy) {
		this.ownedBy = ownedBy;
	}
    
    
}
