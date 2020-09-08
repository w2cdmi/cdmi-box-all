package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;
import java.util.List;

public class UserCurrentStatisticsList implements Serializable
{
    
    private static final long serialVersionUID = 2099966953757836817L;
    
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
