package com.huawei.sharedrive.app.openapi.restv2.folder.v1vo;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.utils.Constants;

import pw.cdmi.common.domain.Terminal;

public class RestFileInfoV1
{
    
    private static final int MD5_LENGTH = 32;
    
    private static final int SHA1_LENGTH = 40;
    
    private Long contentCreatedAt;
    
    private Long contentModifiedAt;
    
    private Date createdAt;
    
    private Long createdBy;
    
    private String description;
    
    private Long id;
    
    private Boolean isEncrypt;
    
    private Boolean isShare;
    
    private Boolean isSharelink;
    
    private Boolean isSync;
    
    // 安全标示
    @JsonInclude(Include.NON_NULL)
    private Integer kiaStatus;
    
    private String md5;
    
    private String mender;
    
    private String menderName;
    
    private Date modifiedAt;
    
    private Long modifiedBy;
    
    private String name;
    
    // 文件ObjectID, 适用于V2版本
    private String objectId;
    
    // 节点所有者ID
    private Long ownedBy;
    
    // 由ownedBy取代
    @Deprecated
    private Long ownerBy;
    
    private Long parent;
    
    private boolean previewable;
    
    private String sha1;
    
    private Long size;
    
    private byte status;
    
    private String thumbnailUrl;
    
    private List<ThumbnailUrl> thumbnailUrlList;
    
    private byte type;
    
    // 文件objectID, 适用于V1版本
    private String version;
    
    // 文件历史版本总数
    private int versions;
    
    public RestFileInfoV1()
    {
        
    }
    
    public RestFileInfoV1(RestFileInfo fileV2, int deviceType)
    {
        this.setId(fileV2.getId());
        this.setType(INode.TYPE_FILE);
        this.setName(fileV2.getName());
        this.setDescription(fileV2.getDescription());
        this.setSize(fileV2.getSize());
        
        // 文件ObjectID(V1版本使用), V2版本version字段修改为objectId, 此处为V1,V2版本共用RestFileInfo对象做的兼容处理
      /*  if (fileV2.getVersion() != null)
        {
            this.setVersion(fileV2.getObjectId());
        }
        else
        {
            // 文件ObjectID(V2版本使用)
            this.setObjectId(fileV2.getObjectId());
            // 文件版本总数, V2版本新增(含当前版本)
        }*/
        this.setVersion(fileV2.getObjectId());
        this.setObjectId(fileV2.getObjectId());
        this.setVersions(fileV2.getVersions());
        this.setStatus(fileV2.getStatus());
        
        if (StringUtils.isNotBlank(fileV2.getSha1()))
        {
            // 兼容移动客户端, 同时返回sha1字段和MD5字段
            if (Constants.IS_DIGEST_NAME_COMPATIBLE
                && (Terminal.CLIENT_TYPE_ANDROID == deviceType || Terminal.CLIENT_TYPE_IOS == deviceType))
            {
                this.setMd5(fileV2.getSha1());
                this.setSha1(fileV2.getSha1());
            }
            else
            {
                if (fileV2.getSha1().length() == MD5_LENGTH)
                {
                    this.setMd5(fileV2.getSha1());
                }
                else if (fileV2.getSha1().length() == SHA1_LENGTH)
                {
                    this.setSha1(fileV2.getSha1());
                }
            }
        }
        
        long createAtTime = fileV2.getCreatedAt().getTime() / 1000 * 1000;
        
        this.setCreatedAt(new Date(createAtTime));
        
        long modifiedAtTime = fileV2.getModifiedAt().getTime() / 1000 * 1000;
        
        this.setModifiedAt(new Date(modifiedAtTime));
        
        this.setOwnedBy(fileV2.getOwnedBy());
        if (Constants.IS_FILED_NAME_COMPATIBLE)
        {
            this.setOwnerBy(fileV2.getOwnedBy());
        }
        
        this.setCreatedBy(fileV2.getCreatedBy());
        this.setModifiedBy(fileV2.getModifiedBy());
        this.setParent(fileV2.getParent());
        this.setIsSharelink(fileV2.getIsSharelink());
        this.setIsEncrypt(fileV2.getIsEncrypt());
        this.setThumbnailUrl(fileV2.getThumbnailUrl());
        if(null == this.getThumbnailUrl())
        {
            if(CollectionUtils.isNotEmpty(fileV2.getThumbnailUrlList()))
            {
                this.setThumbnailUrl(fileV2.getThumbnailUrlList().get(0).getThumbnailUrl());
            }
        }
        this.setThumbnailUrlList(fileV2.getThumbnailUrlList());
        this.setIsShare(fileV2.getIsShare());
        this.setIsSync(fileV2.getIsSync());
        
        this.setContentCreatedAt(fileV2.getContentCreatedAt());
        this.setContentModifiedAt(fileV2.getContentModifiedAt());
        this.setPreviewable(fileV2.isPreviewable());
        this.setKiaStatus(fileV2.getKiaStatus());
    }
    
    public Long getContentCreatedAt()
    {
        return contentCreatedAt;
    }
    
    public Long getContentModifiedAt()
    {
        return contentModifiedAt;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
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
    
    public Integer getKiaStatus()
    {
        return kiaStatus;
    }
    
    public String getMd5()
    {
        return md5;
    }

    public String getMender()
    {
        return mender;
    }

    public String getMenderName()
    {
        return menderName;
    }

    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
public Long getModifiedBy()
{
    return modifiedBy;
}
    
    public String getName()
    {
        return name;
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public Long getOwnedBy()
    {
        return ownedBy;
    }
    
    @Deprecated
    public Long getOwnerBy()
    {
        return ownerBy;
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
    
    public List<ThumbnailUrl> getThumbnailUrlList()
    {
        return thumbnailUrlList;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public int getVersions()
    {
        return versions;
    }
    
    public boolean isPreviewable()
    {
        return previewable;
    }
    
    public void setContentCreatedAt(Long contentCreatedAt)
    {
        this.contentCreatedAt = contentCreatedAt;
    }
    
    public void setContentModifiedAt(Long contentModifiedAt)
    {
        this.contentModifiedAt = contentModifiedAt;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
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
    
    public void setKiaStatus(Integer kiaStatus)
    {
        this.kiaStatus = kiaStatus;
    }
    
    public void setMd5(String md5)
    {
        this.md5 = md5;
    }
    
    public void setMender(String mender)
    {
        this.mender = mender;
    }
    
    public void setMenderName(String menderName)
    {
        this.menderName = menderName;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt == null)
        {
            this.modifiedAt = null;
        }
        else
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
    }
    
    public void setModifiedBy(Long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    @Deprecated
    public void setOwnerBy(Long ownerBy)
    {
        this.ownerBy = ownerBy;
    }
    
    public void setParent(Long parent)
    {
        this.parent = parent;
    }
    
    public void setPreviewable(boolean previewable)
    {
        this.previewable = previewable;
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
    
    public void setThumbnailUrlList(List<ThumbnailUrl> thumbnailUrlList)
    {
        this.thumbnailUrlList = thumbnailUrlList;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public void setVersions(int versions)
    {
        this.versions = versions;
    }
}
