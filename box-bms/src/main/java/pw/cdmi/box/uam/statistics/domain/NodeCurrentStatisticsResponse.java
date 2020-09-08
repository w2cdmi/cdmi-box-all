package pw.cdmi.box.uam.statistics.domain;

import java.util.List;

import pw.cdmi.box.uam.httpclient.domain.TimePoint;

public class NodeCurrentStatisticsResponse
{
    
    private List<NodeCurrentStatisticsInfo> data;
    
    private TimePoint timePoint;
    
    private int totalCount;
    
    public List<NodeCurrentStatisticsInfo> getData()
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
    
    public void setData(List<NodeCurrentStatisticsInfo> data)
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
