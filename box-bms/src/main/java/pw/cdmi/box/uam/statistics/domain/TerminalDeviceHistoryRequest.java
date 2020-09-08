package pw.cdmi.box.uam.statistics.domain;

import java.io.Serializable;
import java.util.Date;

public class TerminalDeviceHistoryRequest implements Serializable
{
    private static final long serialVersionUID = 2424703910361133601L;
    
    private Date beginTime;
    
    private Date endTime;
    
    private String interval;
    
    public Date getBeginTime()
    {
        return beginTime == null ? null : (Date) beginTime.clone();
    }
    
    public void setBeginTime(Date beginTime)
    {
        this.beginTime = (beginTime == null ? null : (Date) beginTime.clone());
    }
    
    public Date getEndTime()
    {
        return endTime == null ? null : (Date) endTime.clone();
    }
    
    public void setEndTime(Date endTime)
    {
        this.endTime = (endTime == null ? null : (Date) endTime.clone());
    }
    
    public String getInterval()
    {
        return interval;
    }
    
    public void setInterval(String interval)
    {
        this.interval = interval;
    }
    
}
