package pw.cdmi.box.uam.statistics.domain;

import java.util.List;

public class ObjectHistoryStatisticsResponse
{
    private List<ObjectHistoryStatisticsInfo> data;
    
    private int totalCount;
    
    public List<ObjectHistoryStatisticsInfo> getData()
    {
        return data;
    }
    
    public void setData(List<ObjectHistoryStatisticsInfo> data)
    {
        this.data = data;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
