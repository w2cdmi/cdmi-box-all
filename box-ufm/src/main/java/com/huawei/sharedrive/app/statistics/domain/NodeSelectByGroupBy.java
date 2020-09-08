package com.huawei.sharedrive.app.statistics.domain;

/**
 * 分组统计查询的结果bean
 * @author l90003768
 *
 */
public class NodeSelectByGroupBy
{
    private String dbName;
    
    private long fileCount;

    private long ownedBy;

    private int resourceGroupId;

    private long spaceUsed;

    private int status;

    public String getDbName()
    {
        return dbName;
    }

    public long getFileCount()
    {
        return fileCount;
    }

    public long getOwnedBy()
    {
        return ownedBy;
    }

    public int getResourceGroupId()
    {
        return resourceGroupId;
    }

    public long getSpaceUsed()
    {
        return spaceUsed;
    }

    public int getStatus()
    {
        return status;
    }
    
    public void setDbName(String dbName)
    {
        this.dbName = dbName;
    }
    
    public void setFileCount(long fileCount)
    {
        this.fileCount = fileCount;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setResourceGroupId(int resourceGroupId)
    {
        this.resourceGroupId = resourceGroupId;
    }

    public void setSpaceUsed(long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }
    
    
}
