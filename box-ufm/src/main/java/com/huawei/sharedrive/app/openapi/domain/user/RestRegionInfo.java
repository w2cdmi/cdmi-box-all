package com.huawei.sharedrive.app.openapi.domain.user;

public class RestRegionInfo
{
    private boolean isDefaultRegionValue;
    
    private int regionId;
    
    private String regionName;
    
    public int getRegionId()
    {
        return regionId;
    }
    
    public String getRegionName()
    {
        return regionName;
    }
    
    public boolean isDefaultRegion()
    {
        return isDefaultRegionValue;
    }
    
    public void setDefaultRegion(boolean isDefaultRegion)
    {
        this.isDefaultRegionValue = isDefaultRegion;
    }
    
    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }
    
    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }
    
}
