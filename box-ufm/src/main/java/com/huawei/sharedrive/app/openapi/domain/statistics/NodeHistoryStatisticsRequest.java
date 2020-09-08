package com.huawei.sharedrive.app.openapi.domain.statistics;

import com.huawei.sharedrive.app.exception.InvalidParamException;


/**
 * 获取文件历史数据统计请求
 * 
 * @author l90003768
 *
 */
public class NodeHistoryStatisticsRequest
{
 
    private String appId;

    private Long beginTime;

    private Long endTime;

    private String interval;

    private Integer regionId;

    public String getAppId()
    {
        return appId;
    }

    public Long getBeginTime()
    {
        return beginTime;
    }

    public Long getEndTime()
    {
        return endTime;
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
    
    public void setBeginTime(Long beginTime)
    {
        this.beginTime = beginTime;
    }
    
    public void setEndTime(Long endTime)
    {
        this.endTime = endTime;
    }
    
    public void setInterval(String interval)
    {
        this.interval = interval;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
    public void checkInterval()
    {
        if(null == this.getInterval())
        {
            this.setInterval(TimePoint.INTERVAL_DAY);
            return;
        }
        
        if(TimePoint.INTERVAL_DAY.equals(this.getInterval()))
        {
            return;
        }
        if(TimePoint.INTERVAL_YEAR.equals(this.getInterval()))
        {
            return;
        }
        if(TimePoint.INTERVAL_MONTH.equals(this.getInterval()))
        {
            return;
        }
        
        if(TimePoint.INTERVAL_WEEK.equals(this.getInterval()))
        {
            return;
        }
        if(TimePoint.INTERVAL_SEANSON.equals(this.getInterval()))
        {
            return;
        }
        
        throw new InvalidParamException("error interval: " + this.getInterval());
    }
    

    
}
