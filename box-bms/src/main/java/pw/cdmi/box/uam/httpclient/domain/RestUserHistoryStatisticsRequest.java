package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;

public class RestUserHistoryStatisticsRequest implements Serializable
{
    
    
    private static final long serialVersionUID = -1279821842301173180L;
    
    private Long beginTime;
    
    private Long endTime;
    
    private String interval;
    
    private Integer regionId;
    
    private String appId;
    
    public Long getBeginTime()
    {
        return beginTime;
    }
    
    public void setBeginTime(Long beginTime)
    {
        this.beginTime = beginTime;
    }
    
    public Long getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(Long endTime)
    {
        this.endTime = endTime;
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
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
}
