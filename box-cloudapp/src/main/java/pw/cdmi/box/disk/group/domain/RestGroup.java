package pw.cdmi.box.disk.group.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RestGroup
{
    private Long id;
    
    private String name;
    
    private String description;
    
    private Long createdBy;
    
    private Long createdAt;
    
    private Long ownedBy;
    
    private Long modifiedAt;
    
    private Long modifiedBy;
    
    private String status;
    
    private String type;
    
    private Integer maxMembers;
    
    @JsonIgnore
    private Long parent;
    
    private String appId;
    
    private Long accountId;
    
    public RestGroup()
    {
        
    }
    
    public RestGroup(Group group)
    {
        this.id = group.getId();
        this.appId = group.getAppId();
        this.description = group.getDescription();
        this.maxMembers = group.getMaxMembers();
        this.name = group.getName();
        this.ownedBy = group.getOwnedBy();
        this.parent = group.getParent();
        this.status = transStatus(group.getStatus());
        if (group.getCreatedAt() != null)
        {
            this.createdAt = group.getCreatedAt().getTime() / 1000 * 1000;
        }
        this.type = transType(group.getType());
        if (group.getModifiedAt() != null)
        {
            this.modifiedAt = group.getModifiedAt().getTime() / 1000 * 1000;
        }
        this.modifiedBy = group.getModifiedBy();
        this.createdBy = group.getCreatedBy();
        this.accountId = group.getAccountId();
        
    }
    
    private String transType(byte type2)
    {
        if (type2 == GroupConstants.GROUP_TYPE_PUBLIC)
        {
            return GroupConstants.TYPE_PUBLIC;
        }
        return GroupConstants.TYPE_PRIVATE;
    }
    
    private String transStatus(byte status2)
    {
        if (status2 == GroupConstants.GROUP_STATUS_DISABLE)
        {
            return GroupConstants.STATUS_DISABLE;
        }
        return GroupConstants.STATUS_ENABLE;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
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
    
    public Long getCreatedBy()
    {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public Long getCreatedAt()
    {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public Long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public Long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public void setModifiedAt(Long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    public Long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public void setModifiedBy(Long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public Integer getMaxMembers()
    {
        return maxMembers;
    }
    
    public void setMaxMembers(Integer maxMembers)
    {
        this.maxMembers = maxMembers;
    }
    
    public Long getParent()
    {
        return parent;
    }
    
    public void setParent(Long parent)
    {
        this.parent = parent;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public Long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }
    
}
