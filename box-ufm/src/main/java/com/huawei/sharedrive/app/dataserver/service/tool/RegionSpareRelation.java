package com.huawei.sharedrive.app.dataserver.service.tool;

public class RegionSpareRelation
{
    private Integer priority;

    private Integer spareRegion;
    
    private String spareRegionName;

    public Integer getPriority()
    {
        return priority;
    }

    public Integer getSpareRegion()
    {
        return spareRegion;
    }

    public String getSpareRegionName()
    {
        return spareRegionName;
    }

    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }
    
    public void setSpareRegion(Integer spareRegion)
    {
        this.spareRegion = spareRegion;
    }
    
    public void setSpareRegionName(String spareRegionName)
    {
        this.spareRegionName = spareRegionName;
    }
}
