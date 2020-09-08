/*
 * Copyright Huawei Technologies Co.,Ltd. 2013-2014. All rights reserved.
 */
package com.huawei.sharedrive.app.share.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;

/**
 * 共享关系表
 * 
 * @author l90003768
 * 
 */
public class INodeShare 
{

    
    /**
     * 共享资源位于回收站状态
     */
    public static final byte STATUS_IN_RECYCLE = 1;
    
    /**
     * 正常状态
     */
    public static final byte STATUS_NORMAL = 0;
    
    /**
     * 文件类型
     */
    public static final byte TYPE_FILE = 1;
    
    /**
     * 文件夹类型
     */
    public static final byte TYPE_FOLDER = 0;

    public static final String SHARE_TYPE_LINK = "link";
    
    /** 创建时间 */
    @JsonIgnore
    private Date createdAt;
    
    /** 创建者 */
    private long createdBy;
    
    /** 共享项目ID */
    private long iNodeId;
    
    /** 修改时间 */
    private Date modifiedAt;
    
    /** 最后修改者 */
    private Long modifiedBy;
    
    /** 文件夹或文件名称 */
    private String name;
    
    /** 共享资源拥有者ID */
    private long ownerId;
    
    /**
     * 共享Owner登录名称，即工号
     */
    private String ownerLoginName;
    
    /** 共享资源拥有者名称 */
    private String ownerName;
    
    /** 角色名称 */
    private String roleName;
    
    /**
     * 部門信息
     */
    private String sharedUserDescrip;
    
    private String sharedUserEmail;
    
    /** 被共享者ID */
    private Long sharedUserId;
    
    /**
     * 被共享人登录名称，即工号
     */
    private String sharedUserLoginName;
    
    /** 被共享者名称 */
    private String sharedUserName;
    
    /** 被共享者类型 */
    private byte sharedUserType;
    
    /**
     * 文件大小
     */
    private long size;
    
    private String extraType;
    
    /** 资源节点状态 */
    private byte status;
    
    @JsonIgnore
    private int tableSuffix;
    
    /**
     * 缩略图地址
     */
    private List<ThumbnailUrl> thumbnailUrlList;
    
    
    private Boolean previewable;
    
    /**
     * 资源类型
     */
    private byte type;

    /*分享类型*/
    private String shareType;
    
    /*分享类型*/
    private String linkCode;

    /*转发人*/
    private Long forwardId;

//    private Byte originalType;

    /*引用记录的iNodeId*/
//    private Long originalNodeId;
//
//    /*引用记录的ownerId*/
//    private Long originalOwnerId;

    /**
     * 获取创建时间
     * 
     * @return
     */
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    /**
     * 获取创建者
     * 
     * @return
     */
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    /**
     * 获取最后修改时间
     * 
     * @return
     */
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    /**
     * 获取最后修改人
     * 
     * @return
     */
    public Long getModifiedBy()
    {
        return modifiedBy;
    }
    
    /**
     * 获取资源节点名称
     * 
     * @return
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * 获取拥有者ID
     * 
     * @return
     */
    public long getOwnerId()
    {
        return ownerId;
    }
    
    public String getOwnerLoginName()
    {
        return ownerLoginName;
    }
    
    /**
     * 获取用户名
     * 
     * @return
     */
    public String getOwnerName()
    {
        return ownerName;
    }
    
    /**
     * 获取角色名称
     * 
     * @return
     */
    public String getRoleName()
    {
        return roleName;
    }
    
    public String getSharedUserEmail()
    {
        return sharedUserEmail;
    }
    
    /**
     * 获取被共享者ID
     * 
     * @return
     */
    public Long getSharedUserId()
    {
        return sharedUserId;
    }
    
    public String getSharedUserLoginName()
    {
        return sharedUserLoginName;
    }
    
    /**
     * 获取被共享者名称
     * 
     * @return
     */
    public String getSharedUserName()
    {
        return sharedUserName;
    }
    
    public long getSize()
    {
        return size;
    }
    
    /**
     * 获取节点状态
     * 
     * @return
     */
    public byte getStatus()
    {
        return status;
    }
    
    /**
     * 获取分表后缀
     * 
     * @return
     */
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    /**
     * 获取资源节点类型
     * 
     * @return
     */
    public byte getType()
    {
        return type;
    }
    
    /**
     * 设置创建时间
     * 
     * @param createdAt
     */
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
    
    /**
     * 设置创建者
     * 
     * @param createdBy
     */
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    /**
     * 设置最后修改时间
     * 
     * @param modifiedAt
     */
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
    
    /**
     * 设置最后修改者
     * 
     * @param modifiedBy
     */
    public void setModifiedBy(Long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    /**
     * 设置资源名称
     * 
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * 设置共享文件夹拥有者
     * 
     * @param ownerId
     */
    public void setOwnerId(long ownerId)
    {
        this.ownerId = ownerId;
    }
    
    public void setOwnerLoginName(String ownerLoginName)
    {
        this.ownerLoginName = ownerLoginName;
    }
    
    /**
     * 设置资源拥有者名称
     * 
     * @param ownerName
     */
    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }
    
    /**
     * 设置角色名称
     * 
     * @param roleName
     */
    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }
    
    public void setSharedUserEmail(String sharedUserEmail)
    {
        this.sharedUserEmail = sharedUserEmail;
    }
    
    /**
     * 设置被共享者
     * 
     * @param sharedUserId
     */
    public void setSharedUserId(Long sharedUserId)
    {
        this.sharedUserId = sharedUserId;
    }
    
    public void setSharedUserLoginName(String sharedUserLoginName)
    {
        this.sharedUserLoginName = sharedUserLoginName;
    }
    
    /**
     * 设置被共享者名称
     * 
     * @param sharedUserName 共享者名称
     */
    public void setSharedUserName(String sharedUserName)
    {
        this.sharedUserName = sharedUserName;
    }
    
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    /**
     * 设置节点状态
     * 
     * @param status
     */
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    /**
     * 设置分表后缀 <br/>
     * 正表根据ownerId分表，反表根据sharedUserId分表
     * 
     * @param tableSuffix
     */
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    /**
     * 设置资源类型
     * 
     * @param type
     */
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public String getSharedUserDescrip()
    {
        return sharedUserDescrip;
    }
    
    public void setSharedUserDescrip(String sharedUserDescrip)
    {
        this.sharedUserDescrip = sharedUserDescrip;
    }
    
    public List<ThumbnailUrl> getThumbnailUrlList()
    {
        return thumbnailUrlList;
    }
    
    public void setThumbnailUrlList(List<ThumbnailUrl> thumbnailUrlList)
    {
        this.thumbnailUrlList = thumbnailUrlList;
    }
    
    public Boolean getPreviewable()
    {
        return previewable;
    }
    
    public void setPreviewable(Boolean previewable)
    {
        this.previewable = previewable;
    }
    
    public String getExtraType()
    {
        return extraType;
    }

    public void setExtraType(String extraType)
    {
        this.extraType = extraType;
    }

    

//    public Long getOriginalNodeId() {
//        return originalNodeId;
//    }
//
//    public void setOriginalNodeId(Long originalNodeId) {
//        this.originalNodeId = originalNodeId;
//    }
//
//    public Long getOriginalOwnerId() {
//        return originalOwnerId;
//    }
//
//    public void setOriginalOwnerId(Long originalOwnerId) {
//        this.originalOwnerId = originalOwnerId;
//    }

//	public Byte getOriginalType() {
//		return originalType;
//	}
//
//	public void setOriginalType(Byte originalType) {
//		this.originalType = originalType;
//	}


//
	public byte getSharedUserType() {
		return sharedUserType;
	}

	public void setSharedUserType(byte sharedUserType) {
		this.sharedUserType = sharedUserType;
	}

	public long getiNodeId() {
		return iNodeId;
	}

	public void setiNodeId(long iNodeId) {
		this.iNodeId = iNodeId;
	}

	public String getShareType() {
		return shareType;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}


    public Long getForwardId() {
        return forwardId;
    }

    public void setForwardId(Long forwardId) {
        this.forwardId = forwardId;
    }

	public String getLinkCode() {
		return linkCode;
	}

	public void setLinkCode(String linkCode) {
		this.linkCode = linkCode;
	}

    
}
