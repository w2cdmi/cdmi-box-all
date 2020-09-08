package pw.cdmi.box.uam.statistics.domain;

import java.util.Date;

public class ObjectHistoryRequest
{
    private Date beginTime;
    
    private Date endTime;
    
    private String interval;
    
    private Integer regionId;
    
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
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
}
