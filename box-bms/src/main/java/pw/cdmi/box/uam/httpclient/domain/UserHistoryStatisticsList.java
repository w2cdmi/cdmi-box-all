package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;
import java.util.List;

public class UserHistoryStatisticsList implements Serializable
{
    
    private static final long serialVersionUID = 8699022125312707677L;
    
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
