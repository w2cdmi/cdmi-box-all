package pw.cdmi.box.uam.statistics.domain;

import java.util.List;

public class HistUserStatisChart
{
    private List<String> urls;
    
    private long max;
    
    private long min;
    
    public List<String> getUrls()
    {
        return urls;
    }
    
    public void setUrls(List<String> urls)
    {
        this.urls = urls;
    }
    
    public long getMax()
    {
        return max;
    }
    
    public void setMax(long max)
    {
        this.max = max;
    }
    
    public long getMin()
    {
        return min;
    }
    
    public void setMin(long min)
    {
        this.min = min;
    }
    
}
