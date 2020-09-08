package com.huawei.sharedrive.app.openapi.domain.user;

import java.util.Date;

import com.huawei.sharedrive.app.mirror.datamigration.domain.UserDataMigrationTask;

/**
 * 数据迁移请求
 * 
 * @author c00287749
 * 
 */
public class MigrationResponse
{
    protected Date createdAt;
    
    protected int destRegionId;
    
    protected Date modifiedAt;
    
    protected long ownerId;
    
    protected int status;
    
    public MigrationResponse(UserDataMigrationTask task)
    {
        this.ownerId = task.getCloudUserId();
        this.destRegionId = task.getDestRegionId();
        this.status = task.getStatus();
        this.createdAt = task.getCreatedAt();
        this.modifiedAt = task.getModifiedAt();
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public int getDestRegionId()
    {
        return destRegionId;
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public long getOwnerId()
    {
        return ownerId;
    }
    
    public int getStatus()
    {
        return status;
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
    
    public void setDestRegionId(int destRegionId)
    {
        this.destRegionId = destRegionId;
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
    
    public void setOwnerId(long ownerId)
    {
        this.ownerId = ownerId;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
}
