package com.huawei.sharedrive.uam.statistics;

import com.huawei.sharedrive.uam.openapi.domain.TimePoint;

public class StatisticsHistoryNode
{
    private TimePoint timePoint;
    
    public TimePoint getTimePoint()
    {
        return timePoint;
    }
    
    public void setTimePoint(TimePoint timePoint)
    {
        this.timePoint = timePoint;
    }
}
