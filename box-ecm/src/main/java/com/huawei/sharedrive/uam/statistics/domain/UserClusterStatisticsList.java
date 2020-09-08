package com.huawei.sharedrive.uam.statistics.domain;

import java.util.List;

public class UserClusterStatisticsList
{
    private long totalCount;
    
    private List<UserClusterStatisticsInfo> data;
    
    public long getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public List<UserClusterStatisticsInfo> getData()
    {
        return data;
    }
    
    public void setData(List<UserClusterStatisticsInfo> data)
    {
        this.data = data;
    }
    
}
