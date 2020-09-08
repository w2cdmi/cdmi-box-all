package com.huawei.sharedrive.uam.openapi.domain;

public class RestUserCurrentStatisticsRequest
{
    
    private String groupBy;
    
    private Integer regionId;
    
    private String appId;
    
    public String getGroupBy()
    {
        return groupBy;
    }
    
    public void setGroupBy(String groupBy)
    {
        this.groupBy = groupBy;
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
