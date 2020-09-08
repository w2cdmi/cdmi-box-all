package com.huawei.sharedrive.app.openapi.domain.group;

import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class RestGroupMembershipsReuqest
{
    
    private String name;
    
    private String description;
    
    private byte status;
    
    private byte type;
    
    private Long ownedBy;
    
    private Long maxMembers;
    
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
    
    public byte getType()
    {
        return type;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public Long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public Long getMaxMembers()
    {
        return maxMembers;
    }
    
    public void setMaxMembers(Long maxMembers)
    {
        this.maxMembers = maxMembers;
    }
    
    public void checkParameter()
    {
        if (ownedBy != null)
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownedBy);
        }
        
        if (maxMembers != null)
        {
            FilesCommonUtils.checkNonNegativeIntegers(maxMembers);
        }
    }
    
}
