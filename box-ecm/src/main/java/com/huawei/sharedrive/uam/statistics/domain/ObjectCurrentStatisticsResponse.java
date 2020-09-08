package com.huawei.sharedrive.uam.statistics.domain;

import java.util.List;

import com.huawei.sharedrive.uam.openapi.domain.TimePoint;

public class ObjectCurrentStatisticsResponse
{
    private List<ObjectCurrentStatisticsInfo> data;
    
    private TimePoint timePoint;
    
    private int totalCount;
    
    public List<ObjectCurrentStatisticsInfo> getData()
    {
        return data;
    }
    
    public TimePoint getTimePoint()
    {
        return timePoint;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public void setData(List<ObjectCurrentStatisticsInfo> data)
    {
        this.data = data;
    }
    
    public void setTimePoint(TimePoint timePoint)
    {
        this.timePoint = timePoint;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
}
