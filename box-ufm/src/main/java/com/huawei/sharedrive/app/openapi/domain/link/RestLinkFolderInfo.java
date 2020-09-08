package com.huawei.sharedrive.app.openapi.domain.link;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.openapi.domain.node.RestBaseObject;
import com.huawei.sharedrive.app.utils.Constants;

public class RestLinkFolderInfo extends RestBaseObject
{
    
    private String description;
    
    private Boolean isShare;
    
    private Boolean isSharelink;
    
    private Boolean isSync;
    
    private int linkCount;
    
    private String extraType;
    
    public RestLinkFolderInfo()
    {
        
    }
    
    @SuppressWarnings("deprecation")
    public RestLinkFolderInfo(INode node)
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
        else
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
        
        Long contentCreatedAt = node.getContentCreatedAt() == null ? null : node.getContentCreatedAt()
            .getTime();
        this.setContentCreatedAt(contentCreatedAt);
        
        Long contentModifiedAt = node.getContentModifiedAt() == null ? null : node.getContentModifiedAt()
            .getTime();
        this.setContentModifiedAt(contentModifiedAt);
        
        this.setLinkCount(node.getLinkCount());
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
    
    public int getLinkCount()
    {
        return linkCount;
    }
    
    public void setLinkCount(int linkCount)
    {
        this.linkCount = linkCount;
    }
    
    public String getExtraType()
    {
        return extraType;
    }
    
    public void setExtraType(String extraType)
    {
        this.extraType = extraType;
    }
    
}
