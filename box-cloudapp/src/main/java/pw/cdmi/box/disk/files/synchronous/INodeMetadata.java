package pw.cdmi.box.disk.files.synchronous;

import pw.cdmi.box.disk.client.domain.node.INode;

public class INodeMetadata
{
    
    private Long contentCreatedAt;
    
    private Long contentModifiedAt;
    
    private Long createdAt;
    
    private long id;
    
    private Long modifiedAt;
    
    private String name;
    
    private String objectId;
    
    private long ownedBy;
    
    private long parentId;
    
    private String sha1 = "";
    
    private long size;
    
    private byte status;
    
    private int syncStatus;
    
    private long syncVersion;
    
    private byte type;
    
    public INodeMetadata(INode node)
    {
        this.id = node.getId();
        this.type = node.getType();
        this.name = node.getName();
        this.parentId = node.getParentId();
        this.size = node.getSize();
        this.status = node.getStatus();
        this.ownedBy = node.getOwnedBy();
        this.objectId = node.getObjectId();
        this.sha1 = node.getSha1();
        this.syncVersion = node.getSyncVersion();
        this.syncStatus = node.getSyncStatus();
        
        if (null != node.getCreatedAt())
        {
            this.createdAt = node.getCreatedAt().getTime();
        }
        
        if (null != node.getModifiedAt())
        {
            this.modifiedAt = node.getModifiedAt().getTime();
        }
        
        if (null != node.getContentCreatedAt())
        {
            this.contentCreatedAt = node.getContentCreatedAt().getTime();
        }
        
        if (null != node.getContentModifiedAt())
        {
            this.contentModifiedAt = node.getContentModifiedAt().getTime();
        }
        
    }
    
    public Long getContentCreatedAt()
    {
        return contentCreatedAt;
    }
    
    public Long getContentModifiedAt()
    {
        return contentModifiedAt;
    }
    
    public Long getCreatedAt()
    {
        return createdAt;
    }
    
    public long getId()
    {
        return id;
    }
    
    public Long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public long getParentId()
    {
        return parentId;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public int getSyncStatus()
    {
        return syncStatus;
    }
    
    public long getSyncVersion()
    {
        return syncVersion;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setContentCreatedAt(Long contentCreatedAt)
    {
        this.contentCreatedAt = contentCreatedAt;
    }
    
    public void setContentModifiedAt(Long contentModifiedAt)
    {
        this.contentModifiedAt = contentModifiedAt;
    }
    
    public void setCreatedAt(Long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setModifiedAt(Long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setParentId(long parentId)
    {
        this.parentId = parentId;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public void setSyncStatus(int syncStatus)
    {
        this.syncStatus = syncStatus;
    }
    
    public void setSyncVersion(long syncVersion)
    {
        this.syncVersion = syncVersion;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
}
