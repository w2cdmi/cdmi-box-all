package pw.cdmi.box.disk.httpclient.rest.response;

public class FolderResponse extends INodeResponse
{
    private static final long serialVersionUID = -6799941849842958057L;
    
    private long contentCreatedAt;
    
    private long contentModifiedAt;
    
    private long createdAt;
    
    private String createdBy;
    
    private String description;
    
    private Boolean isEncrypt;
    
    private Boolean isShare;
    
    private Boolean isSharelink;
    
    private Boolean isSync;
    
    private long modifiedAt;
    
    private String modifiedBy;
    
    private String ownerBy;
    
    private String parent;
    
    private long size;
    
    private String status;
    
    private String version;
    
    public long getContentCreatedAt()
    {
        return contentCreatedAt;
    }
    
    public long getContentModifiedAt()
    {
        return contentModifiedAt;
    }
    
    public long getCreatedAt()
    {
        return createdAt;
    }
    
    public String getCreatedBy()
    {
        return createdBy;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public Boolean getIsEncrypt()
    {
        return isEncrypt;
    }
    
    public Boolean getIsShare()
    {
        return isShare;
    }
    
    public Boolean getIsSharelink()
    {
        return isSharelink;
    }
    
    public Boolean getIsSync()
    {
        return isSync;
    }
    
    public long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public String getModifiedBy()
    {
        return modifiedBy;
    }
    
    public String getOwnerBy()
    {
        return ownerBy;
    }
    
    public String getParent()
    {
        return parent;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public void setContentCreatedAt(long contentCreatedAt)
    {
        this.contentCreatedAt = contentCreatedAt;
    }
    
    public void setContentModifiedAt(long contentModifiedAt)
    {
        this.contentModifiedAt = contentModifiedAt;
    }
    
    public void setCreatedAt(long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setIsEncrypt(Boolean isEncrypt)
    {
        this.isEncrypt = isEncrypt;
    }
    
    public void setIsShare(Boolean isShare)
    {
        this.isShare = isShare;
    }
    
    public void setIsSharelink(Boolean isSharelink)
    {
        this.isSharelink = isSharelink;
    }
    
    public void setIsSync(Boolean isSync)
    {
        this.isSync = isSync;
    }
    
    public void setModifiedAt(long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    public void setModifiedBy(String modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public void setOwnerBy(String ownerBy)
    {
        this.ownerBy = ownerBy;
    }
    
    public void setParent(String parent)
    {
        this.parent = parent;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
}
