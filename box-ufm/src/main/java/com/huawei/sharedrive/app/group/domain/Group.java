package com.huawei.sharedrive.app.group.domain;

import java.util.Date;

public class Group
{
    
    /** 群组ID */
    private long id;
    
    /** 群组名称 */
    private String name;
    
    /** 群组描述 */
    private String description;
    
    /** 创建者ID */
    private long createdBy;
    
    /** 创建时间 */
    private Date createdAt;
    
    /** 拥有者 */
    private long ownedBy;
    
    /** 更新时间 */
    private Date modifiedAt;
    
    /** 最后更新者 */
    private long modifiedBy;
    
    /** 状态 0:可用 1：停用 */
    private byte status;
    
    /** 群组权限 0：私有群 1：公有群 */
    private Byte type;
    
    /** 最大成员数 -1：无限制 */
    private int maxMembers;
    
    /** 扩展字段 默认值0 */
    private long parent;
    
    /** 应用ID */
    private String appId;
    
    /** 企业ID */
    private Long accountId;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
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
    
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
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
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt == null)
        {
            this.modifiedAt = null;
        }
        else
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
    }
    
    public long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public void setModifiedBy(long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public Byte getType()
    {
        return type;
    }
    
    public void setType(Byte type)
    {
        this.type = type;
    }
    
    public int getMaxMembers()
    {
        return maxMembers;
    }
    
    public void setMaxMembers(int maxMembers)
    {
        this.maxMembers = maxMembers;
    }
    
    public long getParent()
    {
        return parent;
    }
    
    public void setParent(long parent)
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
