package com.huawei.sharedrive.app.statistics.domain;

public class TempObjectStatisticsDay
{
    
    private long actualFileCount;

    private long actualSpaceUsed;

    private long day;

    private String dbName;

    private int regionId;

    private int resourceGroupId;

    public long getActualFileCount()
    {
        return actualFileCount;
    }

    public long getActualSpaceUsed()
    {
        return actualSpaceUsed;
    }

    public long getDay()
    {
        return day;
    }

    public String getDbName()
    {
        return dbName;
    }

    public int getRegionId()
    {
        return regionId;
    }

    public int getResourceGroupId()
    {
        return resourceGroupId;
    }

    public void setActualFileCount(long actualFileCount)
    {
        this.actualFileCount = actualFileCount;
    }
    
    public void setActualSpaceUsed(long actualSpaceUsed)
    {
        this.actualSpaceUsed = actualSpaceUsed;
    }

    public void setDay(long day)
    {
        this.day = day;
    }

    public void setDbName(String dbName)
    {
        this.dbName = dbName;
    }

    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }

    public void setResourceGroupId(int resourceGroupId)
    {
        this.resourceGroupId = resourceGroupId;
    }

    public ObjectStatisticsDay convertToObjectStatistics()
    {
        ObjectStatisticsDay objectStatistics = new ObjectStatisticsDay();
        objectStatistics.setActualFileCount(this.getActualFileCount());
        objectStatistics.setActualSpaceUsed(this.getActualSpaceUsed());
        objectStatistics.setDay(this.getDay());
        objectStatistics.setRegionId(this.getRegionId());
        return objectStatistics;
    }
}
