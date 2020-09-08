package com.huawei.sharedrive.app.openapi.domain.statistics;

import java.util.List;

public class UserCurrentStatisticsList
{
    private long totalCount;
    
    private List<UserCurrentStatisticsInfo> data;

    public long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }

    public List<UserCurrentStatisticsInfo> getData()
    {
        return data;
    }

    public void setData(List<UserCurrentStatisticsInfo> data)
    {
        this.data = data;
    }
    
    
}
