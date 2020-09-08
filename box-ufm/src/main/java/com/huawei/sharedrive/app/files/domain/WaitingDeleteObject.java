package com.huawei.sharedrive.app.files.domain;

import java.util.Date;

public class WaitingDeleteObject
{
    /** 创建时间 */
    private Date createdAt;
    
    /** 对象ID */
    private String objectId;
    
    /** 资源组ID */
    private int resourceGroupId;
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public int getResourceGroupId()
    {
        return resourceGroupId;
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
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public void setResourceGroupId(int resourceGroupId)
    {
        this.resourceGroupId = resourceGroupId;
    }
    
}
