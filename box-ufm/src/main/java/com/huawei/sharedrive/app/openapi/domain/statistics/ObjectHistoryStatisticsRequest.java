package com.huawei.sharedrive.app.openapi.domain.statistics;

import com.huawei.sharedrive.app.exception.InvalidParamException;


/**
 * 获取文件历史数据统计请求
 * 
 * @author l90003768
 *
 */
public class ObjectHistoryStatisticsRequest
{
 
    private Long beginTime;

    private Long endTime;

    private String interval;

    private Integer regionId;

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
            this.setInterval(INTERVAL_DAY);
            return;
        }
        
        if(INTERVAL_DAY.equals(this.getInterval()))
        {
            return;
        }
        if(INTERVAL_YEAR.equals(this.getInterval()))
        {
            return;
        }
        if(INTERVAL_MONTH.equals(this.getInterval()))
        {
            return;
        }
        
        if(INTERVAL_WEEK.equals(this.getInterval()))
        {
            return;
        }
        if(INTERVAL_SEANSON.equals(this.getInterval()))
        {
            return;
        }
        
        throw new InvalidParamException("error interval: " + this.getInterval());
    }
    
    public static final String INTERVAL_YEAR = "year";
    
    public static final String INTERVAL_MONTH = "month";
    
    public static final String INTERVAL_SEANSON = "season";
    
    public static final String INTERVAL_WEEK = "week";
    
    public static final String INTERVAL_DAY = "day";
    
}
