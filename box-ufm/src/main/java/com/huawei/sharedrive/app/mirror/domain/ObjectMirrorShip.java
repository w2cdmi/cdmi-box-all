package com.huawei.sharedrive.app.mirror.domain;

import java.util.Date;

/**
 * 对象关系
 * @author c00287749
 *
 */
public class ObjectMirrorShip
{
    private String objectId;
    
    private long size;
    
    private int resourceGroupId;
    
    private String parentObjectId;
    
    private Date createdAt;
    
    private Date accessedAt;
    
    private int type;
    
    /** 分表信息 */
    private int tableSuffix;
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }

    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }

    public String getObjectId()
    {
        return objectId;
    }
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public int getResourceGroupId()
    {
        return resourceGroupId;
    }
    
    public void setResourceGroupId(int resourceGroupId)
    {
        this.resourceGroupId = resourceGroupId;
    }
    
    public String getParentObjectId()
    {
        return parentObjectId;
    }
    
    public void setParentObjectId(String parentObjectId)
    {
        this.parentObjectId = parentObjectId;
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
    
    public Date getAccessedAt()
    {
        if (accessedAt == null)
        {
            return null;
        }
        return (Date) accessedAt.clone();
    }
    
    public void setAccessedAt(Date accessedAt)
    {
        if (accessedAt == null)
        {
            this.accessedAt = null;
        }
        else
        {
            this.accessedAt = (Date) accessedAt.clone();
        }
    }
    
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
   
}
