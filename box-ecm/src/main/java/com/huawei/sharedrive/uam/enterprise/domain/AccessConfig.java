package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AccessConfig implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    public static final String CACHE_KEY_PREFIX_ID = "access_config_id_";
    
    public static final String CACHE_KEY_PREFIX_ACCOUNT_ID = "access_config_accountid_";
    
    public static final String STATUS_ENABLE_STR = "enable";
    
    public static final String STATUS_DISABLE_STR = "disable";
    
    public static final byte STATUS_ENABLE = 0;
    
    public static final byte STATUS_DISABLE = 1;
    
    private long id;
    
    private Long accountId;
    
    private Long safeRoleId;
    
    private String safeRoleName;
    
    private Long spaceRoleId;
    
    private String spaceRoleName;
    
    private Long netRegionId;
    
    private String netRegionName;
    
    private Long resourceTypeId;
    
    private String resourceTypeName;
    
    private Long clientType;
    
    private String clientName;
    
    private String operation;
    
    private List<OperationType> listOperationType;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private String downLoadResrouceTypeIds;
    
    private String previewResourceTypeIds;
    
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
    
    public String getOperation()
    {
        return operation;
    }
    
    public void setOperation(String operation)
    {
        this.operation = operation;
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
    
    public List<OperationType> getListOperationType()
    {
        return listOperationType;
    }
    
    public void setListOperationType(List<OperationType> listOperationType)
    {
        this.listOperationType = listOperationType;
    }
    
    public String getSafeRoleName()
    {
        return safeRoleName;
    }
    
    public void setSafeRoleName(String safeRoleName)
    {
        this.safeRoleName = safeRoleName;
    }
    
    public String getClientName()
    {
        return clientName;
    }
    
    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }
    
    public Long getSafeRoleId()
    {
        return safeRoleId;
    }
    
    public void setSafeRoleId(Long safeRoleId)
    {
        this.safeRoleId = safeRoleId;
    }
    
    public Long getNetRegionId()
    {
        return netRegionId;
    }
    
    public void setNetRegionId(Long netRegionId)
    {
        this.netRegionId = netRegionId;
    }
    
    public Long getResourceTypeId()
    {
        return resourceTypeId;
    }
    
    public void setResourceTypeId(Long resourceTypeId)
    {
        this.resourceTypeId = resourceTypeId;
    }
    
    public Long getClientType()
    {
        return clientType;
    }
    
    public void setClientType(Long clientType)
    {
        this.clientType = clientType;
    }
    
    public String getDownLoadResrouceTypeIds()
    {
        return downLoadResrouceTypeIds;
    }
    
    public void setDownLoadResrouceTypeIds(String downLoadResrouceTypeIds)
    {
        this.downLoadResrouceTypeIds = downLoadResrouceTypeIds;
    }
    
    public String getPreviewResourceTypeIds()
    {
        return previewResourceTypeIds;
    }
    
    public void setPreviewResourceTypeIds(String previewResourceTypeIds)
    {
        this.previewResourceTypeIds = previewResourceTypeIds;
    }
    
    public Long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }
    
    public Long getSpaceRoleId()
    {
        return spaceRoleId;
    }

    public void setSpaceRoleId(Long spaceRoleId)
    {
        this.spaceRoleId = spaceRoleId;
    }

    public String getSpaceRoleName()
    {
        return spaceRoleName;
    }

    public void setSpaceRoleName(String spaceRoleName)
    {
        this.spaceRoleName = spaceRoleName;
    }
    
}
