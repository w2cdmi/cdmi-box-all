package com.huawei.sharedrive.uam.teamspace.domain;

public class RestTeamSpaceModifyRequest
{
    protected String name;
    
    protected String description;
    
    protected Long spaceQuota;
    
    protected Integer status;
    
    protected Integer maxVersions;
    
    protected Integer maxMembers;
    
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
}
