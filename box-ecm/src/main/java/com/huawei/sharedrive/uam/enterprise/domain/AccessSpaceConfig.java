package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;

public class AccessSpaceConfig implements Serializable
{
    public static final byte ALL = -1;
    
    public static final long ALL_SELECT = -1;
    
    public static final String CACHE_KEY_PREFIX_ID = "access_space_config_id_";
    
    public static final String SELCT_ALL = "all";
    
    public static final byte STATUS_DISABLE = 1;
    
    public static final String STATUS_DISABLE_STR = "disable";
    
    public static final byte STATUS_ENABLE = 0;
    
    public static final String STATUS_ENABLE_STR = "enable";
    
    /**
     * 
     */
    private static final long serialVersionUID = -9166371120680282826L;
    
    private Long accountId;
    
    private String clientName;
    
    private Long clientType;
    
    private Date createdAt;
    
    private String id;
    
    private Date modifiedAt;
    
    private Long netRegionId;
    
    private String netRegionName;
    
    private Long operation;
    
    private Long safeRoleId;
    
    private String safeRoleName;
    
    private Long targetSafeRoleId;
    
    private String targetSafeRoleName;
    
    public Long getAccountId()
    {
        return accountId;
    }
    
    public String getClientName()
    {
        return clientName;
    }
    
    public Long getClientType()
    {
        return clientType;
    }
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
    }
    
    public String getId()
    {
        return id;
    }
    
    public Date getModifiedAt()
    {
        if (null != modifiedAt)
        {
            return (Date) modifiedAt.clone();
        }
        return null;
    }
    
    public Long getNetRegionId()
    {
        return netRegionId;
    }
    
    public String getNetRegionName()
    {
        return netRegionName;
    }
    
    public Long getOperation()
    {
        return operation;
    }
    
    public Long getSafeRoleId()
    {
        return safeRoleId;
    }
    
    public String getSafeRoleName()
    {
        return safeRoleName;
    }
    
    public Long getTargetSafeRoleId()
    {
        return targetSafeRoleId;
    }
    
    public String getTargetSafeRoleName()
    {
        return targetSafeRoleName;
    }
    
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }
    
    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }
    
    public void setClientType(Long clientType)
    {
        this.clientType = clientType;
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
    
    public void setId(String id)
    {
        this.id = id;
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
    
    public void setNetRegionId(Long netRegionId)
    {
        this.netRegionId = netRegionId;
    }
    
    public void setNetRegionName(String netRegionName)
    {
        this.netRegionName = netRegionName;
    }
    
    public void setOperation(Long operation)
    {
        this.operation = operation;
    }
    
    public void setSafeRoleId(Long safeRoleId)
    {
        this.safeRoleId = safeRoleId;
    }
    
    public void setSafeRoleName(String safeRoleName)
    {
        this.safeRoleName = safeRoleName;
    }
    
    public void setTargetSafeRoleId(Long targetSafeRoleId)
    {
        this.targetSafeRoleId = targetSafeRoleId;
    }
    
    public void setTargetSafeRoleName(String targetSafeRoleName)
    {
        this.targetSafeRoleName = targetSafeRoleName;
    }
    
}
