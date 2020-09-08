package com.huawei.sharedrive.app.share.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;

/**
 * 共享关系表
 * 
 * @author l90005448
 * 
 */
public class MyNodeShare implements Serializable
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
    
    /** 序列化号 */
    private static final long serialVersionUID = -7436565031071171271L;
    
    /** 修改时间 */
    private long modifiedAt;
    
    /** 最后修改者 */
    private long modifiedBy;
    
    /** 文件夹或文件名称 */
    private String name;
    
    
    /** 共享项目ID */
    private long nodeId;
    
    
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
     * 文件大小
     */
    private long size;
    
    
    /** 资源节点状态 */
    private byte status;
    
    @JsonIgnore
    private int tableSuffix;
    
    /**
     * 缩略图地址
     */
    private List<ThumbnailUrl> thumbnailUrlList;
    
    /**
     * 资源类型
     */
    private byte type;
    
    public long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public long getModifiedBy()
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
    
    public long getNodeId()
    {
        return nodeId;
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
    
    public List<ThumbnailUrl> getThumbnailUrlList()
    {
        return thumbnailUrlList;
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
    
    public void setModifiedAt(long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    
    public void setModifiedBy(long modifiedBy)
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
    
    public void setNodeId(long nodeId)
    {
        this.nodeId = nodeId;
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

    public void setThumbnailUrlList(List<ThumbnailUrl> thumbnailUrlList)
    {
        this.thumbnailUrlList = thumbnailUrlList;
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
    
}
