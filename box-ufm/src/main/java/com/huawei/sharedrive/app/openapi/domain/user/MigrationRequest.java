package com.huawei.sharedrive.app.openapi.domain.user;

/**
 * 数据迁移请求
 * 
 * @author c00287749
 * 
 */
public class MigrationRequest
{
    private int destRegionId;
    
    private long ownerId;
    
    public int getDestRegionId()
    {
        return destRegionId;
    }
    
    public long getOwnerId()
    {
        return ownerId;
    }
    
    public void setDestRegionId(int destRegionId)
    {
        this.destRegionId = destRegionId;
    }
    
    public void setOwnerId(long ownerId)
    {
        this.ownerId = ownerId;
    }
    
}
