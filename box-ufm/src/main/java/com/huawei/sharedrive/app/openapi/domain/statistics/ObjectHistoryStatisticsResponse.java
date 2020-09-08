package com.huawei.sharedrive.app.openapi.domain.statistics;

import java.util.List;

public class ObjectHistoryStatisticsResponse
{
    
    private List<ObjectHistoryStatisticsInfo> data;
    
    private int totalCount;

    public ObjectHistoryStatisticsResponse()
    {
        
    }
    
    public ObjectHistoryStatisticsResponse(List<ObjectHistoryStatisticsInfo> data)
    {
        this.data = data;
        this.totalCount = data != null ? data.size() : 0;
    }
    
    public List<ObjectHistoryStatisticsInfo> getData()
    {
        return data;
    }


    public int getTotalCount()
    {
        return totalCount;
    }

    public void setData(List<ObjectHistoryStatisticsInfo> data)
    {
        this.data = data;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
