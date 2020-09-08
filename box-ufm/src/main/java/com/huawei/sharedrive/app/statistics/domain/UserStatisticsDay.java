package com.huawei.sharedrive.app.statistics.domain;

public class UserStatisticsDay
{
    private int day;
    
    private String appId;
    
    private int regionId;
    
    private long userCount;
    
    private long addedUserCount;

    public int getDay()
    {
        return day;
    }

    public void setDay(int day)
    {
        this.day = day;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public int getRegionId()
    {
        return regionId;
    }

    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }

    public long getUserCount()
    {
        return userCount;
    }

    public void setUserCount(long userCount)
    {
        this.userCount = userCount;
    }

    public long getAddedUserCount()
    {
        return addedUserCount;
    }

    public void setAddedUserCount(long addedUserCount)
    {
        this.addedUserCount = addedUserCount;
    }
}
