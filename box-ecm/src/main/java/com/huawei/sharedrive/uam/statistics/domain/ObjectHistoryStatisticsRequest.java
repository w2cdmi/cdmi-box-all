package com.huawei.sharedrive.uam.statistics.domain;

public class ObjectHistoryStatisticsRequest
{
    private Long beginTime;
    
    private Long endTime;
    
    private String interval;
    
    private Integer regionId;
    
    public ObjectHistoryStatisticsRequest()
    {
        
    }
    
    public ObjectHistoryStatisticsRequest(ObjectHistoryRequest historyRequest)
    {
        beginTime = historyRequest.getBeginTime() != null ? historyRequest.getBeginTime().getTime() : null;
        endTime = historyRequest.getEndTime() != null ? historyRequest.getEndTime().getTime() : null;
        interval = historyRequest.getInterval();
        regionId = historyRequest.getRegionId();
    }
    
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
    
}
