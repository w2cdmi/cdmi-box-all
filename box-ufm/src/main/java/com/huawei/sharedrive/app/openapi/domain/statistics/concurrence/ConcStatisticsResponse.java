package com.huawei.sharedrive.app.openapi.domain.statistics.concurrence;

import java.util.List;

public class ConcStatisticsResponse
{
    
    private List<ConcStatisticsInfo> data;
    
    private int totalCount;

    public List<ConcStatisticsInfo> getData()
    {
        return data;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }

    public void setData(List<ConcStatisticsInfo> data)
    {
        this.data = data;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
