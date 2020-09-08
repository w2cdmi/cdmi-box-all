package com.huawei.sharedrive.app.openapi.restv2.folder.v1vo;

import java.util.Date;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.utils.Constants;

public class RestFolderInfoV1
{
    
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
    
    private Date modifiedAt;
    
    private Long modifiedBy;
    
    private String name;
    
    // 节点所有者ID
    private Long ownedBy;
    
    // 由ownedBy取代
    @Deprecated
    private Long ownerBy;
    
    private Long parent;
    
    
    private Long size;
    
    private byte status;
    
    private byte type;
    
    public RestFolderInfoV1(RestFolderInfo folderV2)
    {
        this.setId(folderV2.getId());
        this.setType(INode.TYPE_FOLDER);
        this.setName(folderV2.getName());
        this.setStatus(folderV2.getStatus());
        
        // 解决问题单DTS2014021802512
        long createAtTime = folderV2.getCreatedAt().getTime() / 1000 * 1000;
        this.setCreatedAt(new Date(createAtTime));
        long modifiedAtTime = folderV2.getModifiedAt().getTime() / 1000 * 1000;
        this.setModifiedAt(new Date(modifiedAtTime));
        
        this.setOwnedBy(folderV2.getOwnedBy());
        // 兼容老的客户端
        if (Constants.IS_FILED_NAME_COMPATIBLE)
        {
            this.setOwnerBy(folderV2.getOwnedBy());
        }
        this.setCreatedBy(folderV2.getCreatedBy());
        this.setModifiedBy(folderV2.getModifiedBy());
        this.setParent(folderV2.getParent());
        this.setIsShare(folderV2.getIsShare());
        this.setIsSync(folderV2.getIsSync());
        this.setIsSharelink(folderV2.getIsSharelink());
        this.setIsEncrypt(folderV2.getIsEncrypt());
        this.setContentCreatedAt(folderV2.getContentCreatedAt());
        this.setContentModifiedAt(folderV2.getContentModifiedAt());
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
    
    public Long getSize()
    {
        return size;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public byte getType()
    {
        return type;
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
    
    public void setSize(Long size)
    {
        this.size = size;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
}
