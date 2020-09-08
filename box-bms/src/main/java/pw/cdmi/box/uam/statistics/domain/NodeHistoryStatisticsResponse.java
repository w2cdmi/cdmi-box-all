package pw.cdmi.box.uam.statistics.domain;

import java.util.List;

public class NodeHistoryStatisticsResponse
{
    
    private List<NodeHistoryStatisticsInfo> data;
    
    private int totalCount;
    
    public List<NodeHistoryStatisticsInfo> getData()
    {
        return data;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public void setData(List<NodeHistoryStatisticsInfo> data)
    {
        this.data = data;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
}
