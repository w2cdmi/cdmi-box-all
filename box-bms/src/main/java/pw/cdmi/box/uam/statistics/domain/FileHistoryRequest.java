package pw.cdmi.box.uam.statistics.domain;

import java.io.Serializable;

public class FileHistoryRequest implements Serializable
{
    private static final long serialVersionUID = 7936418818581869886L;
    
    private long beginTime;
    
    private long endTime;
    
    private String timeType;
    
    public long getBeginTime()
    {
        return beginTime;
    }
    
    public void setBeginTime(long beginTime)
    {
        this.beginTime = beginTime;
    }
    
    public long getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }
    
    public String getTimeType()
    {
        return timeType;
    }
    
    public void setTimeType(String timeType)
    {
        this.timeType = timeType;
    }
    
}
