package com.huawei.sharedrive.uam.httpclient.rest.response;

public class FolderResponse extends INodeResponse
{
    private static final long serialVersionUID = -6799941849842958057L;
    
    private String description;
    
    private long size;
    
    private String version;
    
    private String status;
    
    private long createdAt;
    
    private long modifiedAt;
    
    private String ownerBy;
    
    private String createdBy;
    
    private String modifiedBy;
    
    private long contentCreatedAt;
    
    private long contentModifiedAt;
    
    private String parent;
    
    private Boolean isShare;
    
    private Boolean isSync;
    
    private Boolean isSharelink;
    
    private Boolean isEncrypt;
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public long getCreatedAt()
    {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public void setModifiedAt(long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    public String getOwnerBy()
    {
        return ownerBy;
    }
    
    public void setOwnerBy(String ownerBy)
    {
        this.ownerBy = ownerBy;
    }
    
    public String getCreatedBy()
    {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public String getModifiedBy()
    {
        return modifiedBy;
    }
    
    public void setModifiedBy(String modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public long getContentCreatedAt()
    {
        return contentCreatedAt;
    }
    
    public void setContentCreatedAt(long contentCreatedAt)
    {
        this.contentCreatedAt = contentCreatedAt;
    }
    
    public long getContentModifiedAt()
    {
        return contentModifiedAt;
    }
    
    public void setContentModifiedAt(long contentModifiedAt)
    {
        this.contentModifiedAt = contentModifiedAt;
    }
    
    public String getParent()
    {
        return parent;
    }
    
    public void setParent(String parent)
    {
        this.parent = parent;
    }
    
    public Boolean getIsShare()
    {
        return isShare;
    }
    
    public void setIsShare(Boolean isShare)
    {
        this.isShare = isShare;
    }
    
    public Boolean getIsSync()
    {
        return isSync;
    }
    
    public void setIsSync(Boolean isSync)
    {
        this.isSync = isSync;
    }
    
    public Boolean getIsSharelink()
    {
        return isSharelink;
    }
    
    public void setIsSharelink(Boolean isSharelink)
    {
        this.isSharelink = isSharelink;
    }
    
    public Boolean getIsEncrypt()
    {
        return isEncrypt;
    }
    
    public void setIsEncrypt(Boolean isEncrypt)
    {
        this.isEncrypt = isEncrypt;
    }
    
    @Override
    public String toString()
    {
        return "FolderResponse [id=" + this.getId() + ", type=" + this.getType() + ", name=" + this.getName()
            + ", description=" + description + ", size=" + size + ", version=" + version + ", status="
            + status + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + ", ownerBy=" + ownerBy
            + ", createdBy=" + createdBy + ", modifiedby=" + modifiedBy + ", contencreatedAt="
            + contentCreatedAt + ", contentmodifiedAt=" + contentModifiedAt + ", parent=" + parent
            + ", isShare=" + isShare + ", isSync=" + isSync + ", isSharelink=" + isSharelink + ", isEncrypt="
            + isEncrypt + ']';
    }
    
}
