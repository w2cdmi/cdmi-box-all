package pw.cdmi.box.disk.share.domain;

public class RestFileInfoV2 extends RestBaseObjectV2
{
    private Long id;
    
    private byte type;
    
    private Long createdAt;
    
    private Long modifiedAt;
    
    private Long createdBy;
    
    private Long modifiedBy;
    
    private Long contentCreatedAt;
    
    private Long contentModifiedAt;
    
    private String description;
    
    private Boolean isEncrypt;
    
    private Boolean isShare;
    
    private Boolean isSharelink;
    
    private Boolean isSync;
    
    private String name;
    
    private Long ownedBy;
    
    private Long parent;
    
    private String sha1;
    
    private Long size;
    
    private byte status;
    
    private String thumbnailUrl;
    
    private String thumbnailBigURL;
    
    private String version;
    
    private boolean previewable;
    
    public Long getContentCreatedAt()
    {
        return contentCreatedAt;
    }
    
    public Long getContentModifiedAt()
    {
        return contentModifiedAt;
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
    
    public String getName()
    {
        return name;
    }
    
    public Long getParent()
    {
        return parent;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public Long getSize()
    {
        return size;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public void setContentCreatedAt(Long contentCreatedAt)
    {
        this.contentCreatedAt = contentCreatedAt;
    }
    
    public void setContentModifiedAt(Long contentModifiedAt)
    {
        this.contentModifiedAt = contentModifiedAt;
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
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setParent(Long parent)
    {
        this.parent = parent;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setSize(Long size)
    {
        this.size = size;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public Long getCreatedBy()
    {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public String getThumbnailBigURL()
    {
        return thumbnailBigURL;
    }
    
    public void setThumbnailBigURL(String thumbnailBigURL)
    {
        this.thumbnailBigURL = thumbnailBigURL;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public Long getCreatedAt()
    {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public Long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public void setModifiedBy(Long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public Long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public void setModifiedAt(Long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    public boolean isPreviewable()
    {
        return previewable;
    }
    
    public void setPreviewable(boolean previewable)
    {
        this.previewable = previewable;
    }
    
}
