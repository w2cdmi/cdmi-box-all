package com.huawei.sharedrive.app.acl.domain;

import java.util.Date;

import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.utils.BusinessConstants;

/**
 * 授权关系对象
 * 
 * @author c00110381
 * 
 */
public class INodeACL
{
    public static final String TYPE_USER = "user";
    
    public static final String TYPE_GROUP = "group";
    
    public static final String TYPE_DEPT = "department";
    
    public static final String TYPE_LINK = "link";
    
    public static final String TYPE_TEAM = "team";
    
    public static final String TYPE_SYSTEM = "system";
    
    public static final String TYPE_PUBLIC = "public";
    
	public static final String TYPE_SECRET = "secret";
    
    /** 数据迁移类型 */
    public static final String TYPE_MIGRATION = "migration";
    
    public final static String ID_PUBLIC = BusinessConstants.ID_TEAM_PUBLIC;
    
    public final static String ID_SECRET = "0" ;


    
    private long id;
    
    private long ownedBy;
    
    private String resourceRole;
    
    // 接口调用需要下面几个属性
    private User user;
    
    private int tableSuffix;
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    private long iNodeId;
    
    private long iNodePid;
    
    private String accessUserId;
    
    private String userType;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private long createdBy;
    
    private long modifiedBy;
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public long getiNodeId()
    {
        return iNodeId;
    }
    
    public void setiNodeId(long iNodeId)
    {
        this.iNodeId = iNodeId;
    }
    
    public String getAccessUserId()
    {
        return accessUserId;
    }
    
    public void setAccessUserId(String accessUserId)
    {
        this.accessUserId = accessUserId;
    }
    
    public String getUserType()
    {
        return userType;
    }
    
    public void setUserType(String userType)
    {
        this.userType = userType;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
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
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
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
    
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public void setModifiedBy(long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public String getResourceRole()
    {
        return resourceRole;
    }
    
    public void setResourceRole(String resourceRole)
    {
        this.resourceRole = resourceRole;
    }
    
    public long getiNodePid()
    {
        return iNodePid;
    }
    
    public void setiNodePid(long iNodePid)
    {
        this.iNodePid = iNodePid;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
    
}
