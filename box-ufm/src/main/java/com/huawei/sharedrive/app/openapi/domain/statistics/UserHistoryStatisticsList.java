package com.huawei.sharedrive.app.openapi.domain.statistics;

import java.util.List;

public class UserHistoryStatisticsList
{
    private long totalCount;
    
    private List<UserHistoryStatisticsInfo> data;

    public long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }

    public List<UserHistoryStatisticsInfo> getData()
    {
        return data;
    }

    public void setData(List<UserHistoryStatisticsInfo> data)
    {
        this.data = data;
    }
    
    
}
