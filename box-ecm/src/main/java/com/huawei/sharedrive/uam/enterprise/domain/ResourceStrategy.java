package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;

public class ResourceStrategy implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    public static final String CACHE_KEY_PREFIX_ID = "resource_strategy_id_";
    
    private long id;
    
    private Long netRegionId;
    
    private long accountId;
    
    private Long securityRoleId;
    
    private Long resourceSecurityLevelId;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private String location;
    
    private String safeRoleName;
    
    private String netRegionName;
    
    private String resourceTypeName;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
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
    
    public Date getModifiedAt()
    {
        if (null != modifiedAt)
        {
            return (Date) modifiedAt.clone();
        }
        return null;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (null != modifiedAt)
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
        else
        {
            this.modifiedAt = null;
        }
    }
    
    public Long getNetRegionId()
    {
        return netRegionId;
    }
    
    public void setNetRegionId(Long netRegionId)
    {
        this.netRegionId = netRegionId;
    }
    
    public Long getSecurityRoleId()
    {
        return securityRoleId;
    }
    
    public void setSecurityRoleId(Long securityRoleId)
    {
        this.securityRoleId = securityRoleId;
    }
    
    public Long getResourceSecurityLevelId()
    {
        return resourceSecurityLevelId;
    }
    
    public void setResourceSecurityLevelId(Long resourceSecurityLevelId)
    {
        this.resourceSecurityLevelId = resourceSecurityLevelId;
    }
    
    public String getLocation()
    {
        return location;
    }
    
    public void setLocation(String location)
    {
        this.location = location;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public String getSafeRoleName()
    {
        return safeRoleName;
    }
    
    public void setSafeRoleName(String safeRoleName)
    {
        this.safeRoleName = safeRoleName;
    }
    
    public String getNetRegionName()
    {
        return netRegionName;
    }
    
    public void setNetRegionName(String netRegionName)
    {
        this.netRegionName = netRegionName;
    }
    
    public String getResourceTypeName()
    {
        return resourceTypeName;
    }
    
    public void setResourceTypeName(String resourceTypeName)
    {
        this.resourceTypeName = resourceTypeName;
    }
    
}
