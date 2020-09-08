package com.huawei.sharedrive.uam.teamspace.domain;

public class RestTeamSpaceCreateRequest
{
    protected String name;
    
    protected String description;
    
    protected Long spaceQuota;
    
    protected Integer status;
    
    protected Integer maxVersions;
    
    protected Integer maxMembers;
    
    private Byte regionId;

    protected Long ownerBy;

    protected int type = 0;

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
    
    public Long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public void setSpaceQuota(Long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public Integer getStatus()
    {
        return status;
    }
    
    public void setStatus(Integer status)
    {
        this.status = status;
    }
    
    public Integer getMaxVersions()
    {
        return maxVersions;
    }
    
    public void setMaxVersions(Integer maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public Integer getMaxMembers()
    {
        return maxMembers;
    }
    
    public void setMaxMembers(Integer maxMembers)
    {
        this.maxMembers = maxMembers;
    }

    public Long getOwnerBy() {
        return ownerBy;
    }

    public void setOwnerBy(Long ownerBy) {
        this.ownerBy = ownerBy;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

	public Byte getRegionId() {
		return regionId;
	}

	public void setRegionId(Byte regionId) {
		this.regionId = regionId;
	}
}
