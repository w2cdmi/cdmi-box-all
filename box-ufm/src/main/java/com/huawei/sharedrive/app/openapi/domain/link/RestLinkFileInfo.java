package com.huawei.sharedrive.app.openapi.domain.link;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.openapi.domain.node.RestBaseFileInfo;

/**
 * 
 * @author c00110381
 * 
 */
public class RestLinkFileInfo extends RestBaseFileInfo
{
    // MD5摘要字符串长度
    private static final int MD5_LENGTH = 32;
    
    // Sha1摘要字符串长度
    private static final int SHA1_LENGTH = 40;
    
    private int linkCount;
    
    public RestLinkFileInfo()
    {
        
    }
    
    public RestLinkFileInfo(INode node)
    {
        this.setId(node.getId());
        this.setType(INode.TYPE_FILE);
        this.setName(node.getName());
        this.setDescription(node.getDescription());
        this.setSize(node.getSize());
        
        // 文件ObjectID(V1版本使用), V2版本version字段修改为objectId, 此处为V1,V2版本共用RestFileInfo对象做的兼容处理
        if (node.getVersion() != null)
        {
            this.setVersion(node.getObjectId());
        }
        else
        {
            // 文件ObjectID(V2版本使用)
            this.setObjectId(node.getObjectId());
            // 文件版本总数, V2版本新增(含当前版本)
            this.setVersions(node.getVersions());
        }
        
        this.setStatus(node.getStatus());
        
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
        
        long createAtTime = node.getCreatedAt().getTime() / 1000 * 1000;
        this.setCreatedAt(new Date(createAtTime));
        long modifiedAtTime = node.getModifiedAt().getTime() / 1000 * 1000;
        this.setModifiedAt(new Date(modifiedAtTime));
        
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
        
        Long contentCreatedTime = node.getContentCreatedAt() == null ? null : node.getContentCreatedAt()
            .getTime();
        this.setContentCreatedAt(contentCreatedTime);
        
        Long contentModifiedTime = node.getContentModifiedAt() == null ? null : node.getContentModifiedAt()
            .getTime();
        this.setContentModifiedAt(contentModifiedTime);
        
        this.setLinkCount(node.getLinkCount());
        this.setPreviewable(node.isPreviewable());
    }
    
    public int getLinkCount()
    {
        return linkCount;
    }
    
    public void setLinkCount(int linkCount)
    {
        this.linkCount = linkCount;
    }
    
}
