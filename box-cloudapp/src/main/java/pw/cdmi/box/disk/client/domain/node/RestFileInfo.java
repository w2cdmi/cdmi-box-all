package pw.cdmi.box.disk.client.domain.node;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.disk.client.domain.common.RestBaseObject;
import pw.cdmi.box.disk.filelabel.dto.BaseFileLabelInfo;

public class RestFileInfo extends RestBaseObject
{
    
    private String description;
    
    private Boolean isShare;
    
    private Boolean isSharelink;
    
    private Boolean isVirus;
    
    private Boolean isSync;
    
    private String objectId;
    
    private String sha1;
    
    private String thumbnailUrl;
    
    private List<ThumbnailUrl> thumbnailUrlList;
    
    private String version;
    
    private int versions;
    
    private boolean previewable;

    /** 文件标签属性 */
    private List<BaseFileLabelInfo> fileLabelList;
    
    private byte secretLevel;
    
    //文件夹是否为私密
    private Boolean isSecret;
    
    //如果私密文件，判断用户是否拥有浏览权限
    private Boolean isListAcl;
    
    public RestFileInfo()
    {
        
    }
    
    public RestFileInfo(INode node)
    {
        this.setId(node.getId());
        this.setType(INode.TYPE_FILE);
        this.setName(node.getName());
        this.setDescription(node.getDescription());
        this.setSize(node.getSize());
        
        if (node.getVersion() != null)
        {
            this.setVersion(node.getObjectId());
        }
        else
        {
            this.setObjectId(node.getObjectId());
            this.setVersions(node.getVersions());
        }
        
        this.setStatus(node.getStatus());
        this.setSha1(node.getSha1());
        this.setCreatedAt(node.getCreatedAt());
        this.setModifiedAt(node.getModifiedAt());
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
        this.setFileLabelList(node.getFileLabels());
        this.setIsSecret(node.getIsSecret());
        this.setIsListAcl(node.getIsListAcl());
        
        Long contentCreatedTime = node.getContentCreatedAt() == null ? null : node.getContentCreatedAt()
            .getTime();
        this.setContentCreatedAt(contentCreatedTime);
        
        Long contentModifiedTime = node.getContentModifiedAt() == null ? null : node.getContentModifiedAt()
            .getTime();
        this.setContentModifiedAt(contentModifiedTime);
        
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public Boolean getIsShare()
    {
        return isShare;
    }
    
    public Boolean getIsSharelink()
    {
        return isSharelink;
    }
    
    public Boolean getIsVirus() {
		return isVirus;
	}

	public void setIsVirus(Boolean isVirus) {
		this.isVirus = isVirus;
	}
    
    public Boolean getIsSync()
    {
        return isSync;
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }
    
    public List<ThumbnailUrl> getThumbnailUrlList()
    {
        return thumbnailUrlList;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public int getVersions()
    {
        return versions;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
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
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public void setThumbnailUrlList(List<ThumbnailUrl> thumbnailUrlList)
    {
        this.thumbnailUrlList = thumbnailUrlList;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public void setVersions(int versions)
    {
        this.versions = versions;
    }
    
    public boolean isPreviewable()
    {
        return previewable;
    }
    
    public void setPreviewable(boolean previewable)
    {
        this.previewable = previewable;
    }

    public List<BaseFileLabelInfo> getFileLabelList() {
        return fileLabelList;
    }

    public void setFileLabelList(List<BaseFileLabelInfo> fileLabelList) {
        this.fileLabelList = fileLabelList;
    }

	public byte getSecretLevel() {
		return secretLevel;
	}

	public void setSecretLevel(byte secretLevel) {
		this.secretLevel = secretLevel;
	}

	public Boolean getIsSecret() {
		return isSecret;
	}

	public void setIsSecret(Boolean isSecret) {
		this.isSecret = isSecret;
	}

	public Boolean getIsListAcl() {
		return isListAcl;
	}

	public void setIsListAcl(Boolean isListAcl) {
		this.isListAcl = isListAcl;
	}
    
	
}
