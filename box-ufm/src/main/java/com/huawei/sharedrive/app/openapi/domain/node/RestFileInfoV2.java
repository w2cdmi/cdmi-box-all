package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.files.domain.INode;

/**
 * 
 * @author l90005448
 * 
 */

public class RestFileInfoV2 extends RestBaseObjectV2
{
    // MD5摘要字符串长度
    private static final int MD5_LENGTH = 32;
    
    // Sha1摘要字符串长度
    private static final int SHA1_LENGTH = 40;
    
    private Long contentCreatedAt;
    
    private Long contentModifiedAt;
    
    private Long createdAt;
    
    private Long createdBy;
    
    private String description;
    
    private Long id;
    
    private Boolean isEncrypt;
    
    private Boolean isShare;
    
    private Boolean isSharelink;
    
    private Boolean isSync;
    
    private String md5;
    
    private Long modifiedAt;
    
    private Long modifiedBy;
    
    private String name;
    
    private Long ownedBy;
    
    private Long parent;
    
    private String sha1;
    
    private Long size;
    
    private byte status;
    
    private String thumbnailBigURL;
    
    private String thumbnailUrl;
    
    private byte type;
    
    private String version;
    
    private boolean previewable;
    
    private List<ThumbnailUrl> thumbnailUrlList;
    
    
    
    public RestFileInfoV2(INode node)
    {
        this.setId(node.getId());
        this.setType(INode.TYPE_FILE);
        this.setName(node.getName());
        this.setDescription(String.valueOf(node.getDescription()));
        this.setSize(node.getSize());
        
        // 版本设置为对象ID
        this.setVersion(String.valueOf(node.getObjectId()));
        this.setStatus(node.getStatus());
        this.setCreatedAt(node.getCreatedAt().getTime());
        this.setModifiedAt(node.getModifiedAt().getTime());
        this.setOwnedBy(node.getOwnedBy());
        this.setCreatedBy(node.getCreatedBy());
        this.setModifiedBy(node.getModifiedBy());
        this.setParent(node.getParentId());
        this.setIsShare(node.getShareStatus() == INode.SHARE_STATUS_SHARED);
        this.setIsSync(node.getSyncStatus() == INode.SYNC_STATUS_SETTED);
        this.setIsSharelink(StringUtils.isNotBlank(node.getLinkCode()));
        this.setIsEncrypt(StringUtils.isNotBlank(node.getEncryptKey()));
        this.setThumbnailUrl(node.getThumbnailUrl());
        this.setThumbnailUrlList(node.getThumbnailUrlList());
        
        if (StringUtils.isNotBlank(node.getSha1()))
        {
            if (node.getSha1().length() == MD5_LENGTH)
            {
                this.setMd5(node.getSha1());
            }
            else if (node.getSha1().length() == SHA1_LENGTH)
            {
                this.setSha1(node.getSha1());
            }
        }
        
        Long contentCreatedTime = node.getContentCreatedAt() == null ? null : node.getContentCreatedAt()
            .getTime();
        this.setContentCreatedAt(contentCreatedTime);
        
        Long contentModifiedTime = node.getContentModifiedAt() == null ? null : node.getContentModifiedAt()
            .getTime();
        this.setContentModifiedAt(contentModifiedTime);
        this.setPreviewable(node.isPreviewable());
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
    
    public Long getCreatedBy()
    {
        return createdBy;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public Long getId()
    {
        return id;
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
    
    public String getMd5()
    {
        return md5;
    }
    
    public Long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public Long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Long getOwnedBy()
    {
        return ownedBy;
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
    
    public String getThumbnailBigURL()
    {
        return thumbnailBigURL;
    }
    
    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }
    
    public byte getType()
    {
        return type;
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
    
    public void setCreatedAt(Long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public void setCreatedBy(Long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setId(Long id)
    {
        this.id = id;
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
    
    public void setMd5(String md5)
    {
        this.md5 = md5;
    }
    
    public void setModifiedAt(Long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    public void setModifiedBy(Long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public void setName(String name)
    {
        this.name = name;
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
    
    public void setThumbnailBigURL(String thumbnailBigURL)
    {
        this.thumbnailBigURL = thumbnailBigURL;
    }
    
    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public boolean isPreviewable()
    {
        return previewable;
    }
    
    public void setPreviewable(boolean previewable)
    {
        this.previewable = previewable;
    }

	public List<ThumbnailUrl> getThumbnailUrlList() {
		return thumbnailUrlList;
	}

	public void setThumbnailUrlList(List<ThumbnailUrl> thumbnailUrlList) {
		this.thumbnailUrlList = thumbnailUrlList;
	}
    
}
