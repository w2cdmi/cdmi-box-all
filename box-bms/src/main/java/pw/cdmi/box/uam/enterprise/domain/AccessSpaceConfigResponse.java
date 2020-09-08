package pw.cdmi.box.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;

public class AccessSpaceConfigResponse implements Serializable
{
    
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
    
    public Long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }
    
    public String getClientName()
    {
        return clientName;
    }
    
    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }
    
    public Long getClientType()
    {
        return clientType;
    }
    
    public void setClientType(Long clientType)
    {
        this.clientType = clientType;
    }
    
    public Date getCreatedAt()
    {
        return createdAt == null ? null : (Date) createdAt.clone();
    }
    
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = (createdAt == null ? null : (Date) createdAt.clone());
    }
    
    public Date getModifiedAt()
    {
        return modifiedAt == null ? null : (Date) modifiedAt.clone();
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        this.modifiedAt = (modifiedAt == null ? null : (Date) modifiedAt.clone());
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public Long getNetRegionId()
    {
        return netRegionId;
    }
    
    public void setNetRegionId(Long netRegionId)
    {
        this.netRegionId = netRegionId;
    }
    
    public String getNetRegionName()
    {
        return netRegionName;
    }
    
    public void setNetRegionName(String netRegionName)
    {
        this.netRegionName = netRegionName;
    }
    
    public String getOperation()
    {
        return operation;
    }
    
    public void setOperation(String operation)
    {
        this.operation = operation;
    }
    
    public Long getSafeRoleId()
    {
        return safeRoleId;
    }
    
    public void setSafeRoleId(Long safeRoleId)
    {
        this.safeRoleId = safeRoleId;
    }
    
    public String getSafeRoleName()
    {
        return safeRoleName;
    }
    
    public void setSafeRoleName(String safeRoleName)
    {
        this.safeRoleName = safeRoleName;
    }
    
    public Long getTargetSafeRoleId()
    {
        return targetSafeRoleId;
    }
    
    public void setTargetSafeRoleId(Long targetSafeRoleId)
    {
        this.targetSafeRoleId = targetSafeRoleId;
    }
    
    public String getTargetSafeRoleName()
    {
        return targetSafeRoleName;
    }
    
    public void setTargetSafeRoleName(String targetSafeRoleName)
    {
        this.targetSafeRoleName = targetSafeRoleName;
    }
    
}
