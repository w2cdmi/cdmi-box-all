package com.huawei.sharedrive.app.openapi.domain.statistics.concurrence;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.openapi.domain.statistics.TimePoint;

public class ConcStatisticsRequest
{
    private Long beginTime;

    private Long endTime;

    private String interval;

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
        if(TimePoint.INTERVAL_SEANSON.equals(this.getInterval()))
        {
            return;
        }
        
        if(TimePoint.INTERVAL_WEEK.equals(this.getInterval()))
        {
            return;
        }
        if(TimePoint.INTERVAL_MONTH.equals(this.getInterval()))
        {
            return;
        }
        
        throw new InvalidParamException("error interval: " + this.getInterval());
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
    
}
