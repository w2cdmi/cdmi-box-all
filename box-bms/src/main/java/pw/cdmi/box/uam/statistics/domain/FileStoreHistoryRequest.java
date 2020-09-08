package pw.cdmi.box.uam.statistics.domain;

import java.io.Serializable;
import java.util.Date;

public class FileStoreHistoryRequest implements Serializable
{
    private static final long serialVersionUID = 2675582047958122535L;
    
    private String appId;
    
    private Date beginTime;
    
    private Date endTime;
    
    private String interval;
    
    private Integer regionId;
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
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
    
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
    
}
