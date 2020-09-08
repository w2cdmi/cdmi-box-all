package com.huawei.sharedrive.app.openapi.domain.statistics;

public class UserHistoryStatisticsInfo
{
    private Long userCount;
    
    private Integer added;
    
    private TimePoint timePoint;

    public Long getUserCount()
    {
        return userCount;
    }

    public void setUserCount(Long userCount)
    {
        this.userCount = userCount;
    }

    public Integer getAdded()
    {
        return added;
    }

    public void setAdded(Integer added)
    {
        this.added = added;
    }

    public TimePoint getTimePoint()
    {
        return timePoint;
    }

    public void setTimePoint(TimePoint timePoint)
    {
        this.timePoint = timePoint;
    }
    
    
}
