package com.huawei.sharedrive.uam.openapi.domain;


public class UserCurrentStatisticsInfo
{
    
    private String appId;
    
    private Integer regionId;
    
    private String regionName;
    
    private Long userCount;
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
    public String getRegionName()
    {
        return regionName;
    }
    
    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }
    
    public Long getUserCount()
    {
        return userCount;
    }
    
    public void setUserCount(Long userCount)
    {
        this.userCount = userCount;
    }
    
}
