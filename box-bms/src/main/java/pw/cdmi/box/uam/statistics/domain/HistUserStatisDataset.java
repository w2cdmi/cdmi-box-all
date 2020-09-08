package pw.cdmi.box.uam.statistics.domain;

import java.io.Serializable;
import java.util.List;

import org.jfree.data.category.DefaultCategoryDataset;

import pw.cdmi.box.uam.httpclient.domain.UserHistoryStatisticsInfo;

public class HistUserStatisDataset implements Serializable
{
    
    private static final long serialVersionUID = -4634595971236346287L;
    
    private List<UserHistoryStatisticsInfo> data;
    
    private DefaultCategoryDataset defaultCategoryDataset;
    
    private long max;
    
    private long min;
    
    public List<UserHistoryStatisticsInfo> getData()
    {
        return data;
    }
    
    public DefaultCategoryDataset getDefaultCategoryDataset()
    {
        return defaultCategoryDataset;
    }
    
    public long getMax()
    {
        return max;
    }
    
    public long getMin()
    {
        return min;
    }
    
    public void setData(List<UserHistoryStatisticsInfo> data)
    {
        this.data = data;
    }
    
    public void setDefaultCategoryDataset(DefaultCategoryDataset defaultCategoryDataset)
    {
        this.defaultCategoryDataset = defaultCategoryDataset;
    }
    
    public void setMax(long max)
    {
        this.max = max;
    }
    
    public void setMin(long min)
    {
        this.min = min;
    }
    
}
