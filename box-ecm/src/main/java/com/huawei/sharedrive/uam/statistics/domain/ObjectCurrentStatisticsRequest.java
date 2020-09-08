package com.huawei.sharedrive.uam.statistics.domain;

public class ObjectCurrentStatisticsRequest
{
    private Integer regionId;
    
    public ObjectCurrentStatisticsRequest()
    {
        
    }
    
    public ObjectCurrentStatisticsRequest(Integer regionId)
    {
        this.regionId = regionId;
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
