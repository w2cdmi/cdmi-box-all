package com.huawei.sharedrive.app.statistics.domain;

/**
 * 临时的每天用户节点使用情况
 * @author l90003768
 *
 */
public class TempUserNodeStatistics
{
    private long accountId;
    
    private String appId;

    private long day;

    private String dbName;

    private long deletedFileCount;

    private long deletedSpaceUsed;

    private long fileCount;

    private long ownedBy;

    private int regionId;
    
    private int resourceGroupId;

    private long spaceUsed;

    private long trashFileCount;
    
    private long trashSpaceUsed;

    public NodeStatisticsDay convertIntoNodeStatisticsDay()
    {
        NodeStatisticsDay nodeStatistics = new NodeStatisticsDay();
        nodeStatistics.setDay(this.getDay());
        nodeStatistics.setAppId(this.getAppId());
        nodeStatistics.setRegionId(this.getRegionId());
        nodeStatistics.setFileCount(this.getFileCount());
        nodeStatistics.setTrashFileCount(this.getTrashFileCount());
        nodeStatistics.setDeletedFileCount(this.getDeletedFileCount());
        nodeStatistics.setSpaceUsed(this.getSpaceUsed());
        nodeStatistics.setTrashSpaceUsed(this.getTrashSpaceUsed());
        nodeStatistics.setDeletedSpaceUsed(this.getDeletedSpaceUsed());
        return nodeStatistics;
    }

    public long getAccountId()
    {
        return accountId;
    }

    public String getAppId()
    {
        return appId;
    }

    public long getDay()
    {
        return day;
    }

    public String getDbName()
    {
        return dbName;
    }

    public long getDeletedFileCount()
    {
        return deletedFileCount;
    }

    public long getDeletedSpaceUsed()
    {
        return deletedSpaceUsed;
    }

    public long getFileCount()
    {
        return fileCount;
    }

    public long getOwnedBy()
    {
        return ownedBy;
    }

    public int getRegionId()
    {
        return regionId;
    }

    public int getResourceGroupId()
    {
        return resourceGroupId;
    }

    public long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public long getTrashFileCount()
    {
        return trashFileCount;
    }
    
    public long getTrashSpaceUsed()
    {
        return trashSpaceUsed;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setDay(long day)
    {
        this.day = day;
    }
    
    public void setDbName(String dbName)
    {
        this.dbName = dbName;
    }
    
    public void setDeletedFileCount(long deletedFileCount)
    {
        this.deletedFileCount = deletedFileCount;
    }
    
    public void setDeletedSpaceUsed(long deletedSpaceUsed)
    {
        this.deletedSpaceUsed = deletedSpaceUsed;
    }

    public void setFileCount(long fileCount)
    {
        this.fileCount = fileCount;
    }

    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }

    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }

    public void setResourceGroupId(int resourceGroupId)
    {
        this.resourceGroupId = resourceGroupId;
    }
    
    public void setSpaceUsed(long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }

    public void setTrashFileCount(long trashFileCount)
    {
        this.trashFileCount = trashFileCount;
    }

    public void setTrashSpaceUsed(long trashSpaceUsed)
    {
        this.trashSpaceUsed = trashSpaceUsed;
    }
}
