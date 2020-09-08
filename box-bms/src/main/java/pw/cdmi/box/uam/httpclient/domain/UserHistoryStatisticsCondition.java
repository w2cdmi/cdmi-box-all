package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;
import java.util.Date;

public class UserHistoryStatisticsCondition implements Serializable
{
    
    private static final long serialVersionUID = -7552313019070286233L;
    
    private String appId;
    
    private String authAppId;
    
    private Date beginTime;
    
    private Date endTime;
    
    private String interval;
    
    private Integer regionId;
    
    public String getAppId()
    {
        return appId;
    }
    
    public String getAuthAppId()
    {
        return authAppId;
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
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setAuthAppId(String authAppId)
    {
        this.authAppId = authAppId;
    }
    
    public void setInterval(String interval)
    {
        this.interval = interval;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
}
