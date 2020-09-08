package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;

public class AccessSpaceConfigResponse implements Serializable
{
    
    private static final long serialVersionUID = -9027720847832122162L;
    
    private Long accountId;
    
    private String clientName;
    
    private Long clientType;
    
    private Date createdAt;
    
    private String id;
    
    private Date modifiedAt;
    
    private Long netRegionId;
    
    private String netRegionName;
    
    private String operation;
    
    private Long safeRoleId;
    
    private String safeRoleName;
    
    private Long targetSafeRoleId;
    
    private String targetSafeRoleName;
    
    private String downLoadResrouceTypeIds;
    
    private String previewResourceTypeIds;
    
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

    public AccessSpaceConfigResponse()
    {
        
    }
    
    public AccessSpaceConfigResponse(AccessSpaceConfig spaceConfig)
    {
        accountId = spaceConfig.getAccountId();
        clientName = spaceConfig.getClientName();
        clientType = spaceConfig.getClientType();
        createdAt = spaceConfig.getCreatedAt();
        id = spaceConfig.getId();
        modifiedAt = spaceConfig.getModifiedAt();
        netRegionId = spaceConfig.getNetRegionId();
        netRegionName = spaceConfig.getNetRegionName();
        safeRoleId = spaceConfig.getSafeRoleId();
        safeRoleName = spaceConfig.getSafeRoleName();
        targetSafeRoleId = spaceConfig.getTargetSafeRoleId();
        targetSafeRoleName = spaceConfig.getTargetSafeRoleName();
    }
    
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
    
    public String getOperation()
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
    
    public void setOperation(String operation)
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
