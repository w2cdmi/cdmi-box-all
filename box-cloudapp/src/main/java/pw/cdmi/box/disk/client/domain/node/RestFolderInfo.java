package pw.cdmi.box.disk.client.domain.node;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.disk.client.domain.common.RestBaseObject;

public class RestFolderInfo extends RestBaseObject
{
    
    private String description;
    
    private Boolean isShare;
    
    private Boolean isSharelink;
    
    private Boolean isSync;
    
    //文件夹是否为私密
    private Boolean isSecret;
    
    //如果私密文件，判断用户是否拥有浏览权限
    private Boolean isListAcl;
    
    private String extraType;

    public RestFolderInfo()
    {
        
    }
    
    public RestFolderInfo(INode inode)
    {
        this.setId(inode.getId());
        this.setType(INode.TYPE_FOLDER);
        this.setName(inode.getName());
        this.setDescription(inode.getDescription());
        this.setStatus(inode.getStatus());
        this.setCreatedAt(inode.getCreatedAt());
        this.setModifiedAt(inode.getModifiedAt());
        this.setOwnedBy(inode.getOwnedBy());
        this.setCreatedBy(inode.getCreatedBy());
        this.setModifiedBy(inode.getModifiedBy());
        this.setParent(inode.getParentId());
        this.setIsShare(inode.getShareStatus() == INode.SHARE_STATUS_SHARED);
        this.setIsSync(inode.getSyncStatus() == INode.SYNC_STATUS_SETTED);
        this.setIsSharelink(StringUtils.isNotBlank(inode.getLinkCode()));
        this.setIsEncrypt(StringUtils.isNotBlank(inode.getEncryptKey()));
        this.setIsSecret(inode.getIsSecret());
        this.setIsListAcl(inode.getIsListAcl());
        
        Long contentCreatedAt = inode.getContentCreatedAt() == null ? null : inode.getContentCreatedAt()
            .getTime();
        this.setContentCreatedAt(contentCreatedAt);
        
        Long contentModifiedAt = inode.getContentModifiedAt() == null ? null : inode.getContentModifiedAt()
            .getTime();
        this.setContentModifiedAt(contentModifiedAt);
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
    
    public Boolean getIsSync()
    {
        return isSync;
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
    
    public String getExtraType()
    {
        return extraType;
    }

    public void setExtraType(String extraType)
    {
        this.extraType = extraType;
    }

    public void transType()
    {
        if (StringUtils.equals(this.getExtraType(), INode.TYPE_BACKUP_COMPUTER_STR))
        {
            this.setType(INode.TYPE_BACKUP_COMPUTER);
        }
        else if (StringUtils.equals(this.getExtraType(), INode.TYPE_BACKUP_DISK_STR))
        {
            this.setType(INode.TYPE_BACKUP_DISK);
        }
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
