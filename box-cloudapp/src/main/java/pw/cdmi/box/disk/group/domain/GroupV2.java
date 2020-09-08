package pw.cdmi.box.disk.group.domain;

import java.util.Date;

public class GroupV2
{
    public static final int DEFAULT_MAX_MEMBERS = 99999999;
    
    public static final String DEFAULT_STATUS = "enable";
    
    public static final String DEFAULT_TYPE = "public";
    
    private String appId;
    
    private Date createdAt;
    
    private long createdBy;
    
    private String description;
    
    private long id;
    
    private int maxMembers = DEFAULT_MAX_MEMBERS;
    
    private Date modifiedAt;
    
    private long modifiedBy;
    
    private String name;
    
    private long ownedBy;
    
    private long parent;
    
    private String status = DEFAULT_STATUS;
    
    private String type = DEFAULT_TYPE;
    
    public String getAppId()
    {
        return appId;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public long getId()
    {
        return id;
    }
    
    public int getMaxMembers()
    {
        return maxMembers;
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public String getName()
    {
        return name;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public long getParent()
    {
        return parent;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
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
    
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setMaxMembers(int maxMembers)
    {
        this.maxMembers = maxMembers;
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
    
    public void setModifiedBy(long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setParent(long parent)
    {
        this.parent = parent;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
}
