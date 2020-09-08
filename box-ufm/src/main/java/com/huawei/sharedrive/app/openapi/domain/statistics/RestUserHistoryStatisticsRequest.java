package com.huawei.sharedrive.app.openapi.domain.statistics;

import java.util.Date;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public class RestUserHistoryStatisticsRequest
{
    private static final String[] INTERVAL_TYPE_ARRAY = {"day", "week", "month", "season", "year"};
    
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
    
    public void checkParameter() throws InvalidParamException
    {
        if (beginTime == null)
        {
            throw new InvalidParamException("beginTime is null");
        }
        if(endTime == null)
        {
            this.setEndTime(new Date().getTime());
        }
        if (endTime < beginTime)
        {
            throw new InvalidParamException("endTime is invalid:" + endTime + ",beginTime:" + beginTime);
        }
        if (null == interval)
        {
            this.interval = "day";
            return;
        }
        for (String tempInterval : INTERVAL_TYPE_ARRAY)
        {
            if (interval.equals(tempInterval))
            {
                return;
            }
        }
        throw new InvalidParamException("interval is invalid:" + interval);
    }
    
}
