package pw.cdmi.box.disk.httpclient.rest.response;

public class FileInfoResponse extends INodeResponse
{
    private static final long serialVersionUID = 2555985093094644056L;
    
    private String parent;
    
    private String description;
    
    private long size;
    
    private String version;
    
    private String status;
    
    private String md5;
    
    private String etag;
    
    private String sha1;
    
    private long createdAt;
    
    private long modifiedAt;
    
    private String ownerBy;
    
    private String createdBy;
    
    private String modifiedBy;
    
    private long contentCreatedAt;
    
    private long contentModifiedAt;
    
    private boolean shared;
    
    private boolean synced;
    
    private boolean sharedlink;
    
    private boolean encrypted;
    
    private String thumbnailUrl;
    
    public String getParent()
    {
        return parent;
    }
    
    public void setParent(String parent)
    {
        this.parent = parent;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
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
    
    public String getMd5()
    {
        return md5;
    }
    
    public void setMd5(String md5)
    {
        this.md5 = md5;
    }
    
    public String getEtag()
    {
        return etag;
    }
    
    public void setEtag(String etag)
    {
        this.etag = etag;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
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
    
    public boolean isShare()
    {
        return shared;
    }
    
    public void setShare(boolean isShare)
    {
        this.shared = isShare;
    }
    
    public boolean isSync()
    {
        return synced;
    }
    
    public void setSync(boolean isSync)
    {
        this.synced = isSync;
    }
    
    public boolean isEncrypt()
    {
        return encrypted;
    }
    
    public void setEncrypt(boolean isEncrypt)
    {
        this.encrypted = isEncrypt;
    }
    
    public boolean isSharelink()
    {
        return sharedlink;
    }
    
    public void setSharelink(boolean isSharelink)
    {
        this.sharedlink = isSharelink;
    }
    
    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    @Override
    public String toString()
    {
        return "FileInfoResponse [id=" + this.getId() + ", parent=" + parent + ", type=" + this.getType()
            + ", name=" + this.getName() + ", description=" + description + ", size=" + size + ", version="
            + version + ", status=" + status + ", md5=" + md5 + ", etag=" + etag + ", sha1=" + sha1
            + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + ", ownerBy=" + ownerBy
            + ", createdBy=" + createdBy + ", modifiedBy=" + modifiedBy + ", contentCreatedAt="
            + contentCreatedAt + ", contentModifiedAt=" + contentModifiedAt + ", isShare=" + shared
            + ", isSync=" + synced + ", isSharelink=" + sharedlink + ", isEncrypt=" + encrypted + ']';
    }
    
}