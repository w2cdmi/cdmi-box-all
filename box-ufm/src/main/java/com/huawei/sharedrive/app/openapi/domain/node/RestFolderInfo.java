package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.Constants;

public class RestFolderInfo extends RestBaseObject
{
    
    private String description;
    
    private Boolean isShare;
    
    private Boolean isSharelink;
    
    private Boolean isSync;
    
    private Boolean isVirus;
    
    private Boolean isShortcut;
    
    //是否私密文件
    private Boolean isSecret;
    
    //如果私密文件，判断用户是否拥有浏览权限
    private Boolean isListAcl;

	private List<RestBaseObject> path;
    
    private String extraType;

    public RestFolderInfo()
    {
        
    }
    
    @SuppressWarnings("deprecation")
    public RestFolderInfo(INode node)
    {
        this.setId(node.getId());
        if (node.getType() == INode.TYPE_BACKUP_COMPUTER)
        {
            this.setExtraType(INode.TYPE_BACKUP_COMPUTER_STR);
            this.setType(INode.TYPE_FOLDER);
        }
        else if (node.getType() == INode.TYPE_BACKUP_DISK)
        {
            this.setExtraType(INode.TYPE_BACKUP_DISK_STR);
            this.setType(INode.TYPE_FOLDER);
        }
        else if (node.getType() == INode.TYPE_BACKUP_EMAIL)
        {
            this.setExtraType(INode.TYPE_BACKUP_EMAIL_STR);
            this.setType(INode.TYPE_FOLDER);
        }
        else if (node.getType() == INode.TYPE_MIGRATION)
        {
            this.setExtraType(INode.TYPE_MIGRATION_STR);
            this.setType(INode.TYPE_MIGRATION);
        } else if (node.getType() == INode.TYPE_WECHAT)
        {
            this.setExtraType(INode.TYPE_WECHAT_STR);
            this.setType(INode.TYPE_WECHAT);
        } else if (node.getType() == INode.TYPE_INBOX)
        {
            this.setExtraType(INode.TYPE_WECHAT_STR);
            this.setType(INode.TYPE_INBOX);
            node.setName("来自:收件箱");
        }else

        {
            this.setType(INode.TYPE_FOLDER);
        }
        
        this.setName(node.getName());
        this.setDescription(node.getDescription());
        this.setStatus(node.getStatus());
        
        // 解决问题单DTS2014021802512
        long createAtTime = node.getCreatedAt().getTime() / 1000 * 1000;
        this.setCreatedAt(new Date(createAtTime));
        long modifiedAtTime = node.getModifiedAt().getTime() / 1000 * 1000;
        this.setModifiedAt(new Date(modifiedAtTime));
        
        this.setOwnedBy(node.getOwnedBy());
        // 兼容老的客户端
        if (Constants.IS_FILED_NAME_COMPATIBLE)
        {
            this.setOwnerBy(node.getOwnedBy());
        }
        this.setCreatedBy(node.getCreatedBy());
        this.setModifiedBy(node.getModifiedBy());
        this.setParent(node.getParentId());
        this.setIsShare(node.getShareStatus() == INode.SHARE_STATUS_SHARED);
        this.setIsSync(node.getSyncStatus() == INode.SYNC_STATUS_SETTED);
        this.setIsSharelink(StringUtils.isNotBlank(node.getLinkCode()));
        this.setIsEncrypt(StringUtils.isNotBlank(node.getEncryptKey()));
        this.setIsVirus(INode.getVirusStatusFromKIALabel(node.getKiaLabel()));
        this.setIsSecret(node.getIsSecret());
        this.setIsListAcl(node.getIsListAcl());
        
        Long contentCreatedAt = node.getContentCreatedAt() == null ? null : node.getContentCreatedAt()
            .getTime();
        this.setContentCreatedAt(contentCreatedAt);
        
        Long contentModifiedAt = node.getContentModifiedAt() == null ? null : node.getContentModifiedAt()
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
    
    public Boolean getIsVirus() {
		return isVirus;
	}

	public void setIsVirus(Boolean isVirus) {
		this.isVirus = isVirus;
	}
    
    public List<RestBaseObject> getPath()
    {
        return path;
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
    
    public void setPath(List<RestBaseObject> path)
    {
        this.path = path;
    }
    
    public String getExtraType()
    {
        return extraType;
    }
    
    public void setExtraType(String extraType)
    {
        this.extraType = extraType;
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

	public Boolean getIsShortcut() {
		return isShortcut;
	}

	public void setIsShortcut(Boolean isShortcut) {
		this.isShortcut = isShortcut;
	}

    
}
