package com.huawei.sharedrive.app.dataserver.service.tool;

import java.util.List;

public class RegionSpare
{
    private Integer mainRegion;

    private String mainRegionName;

    private List<RegionSpareRelation> spareList;

    public Integer getMainRegion()
    {
        return mainRegion;
    }

    public String getMainRegionName()
    {
        return mainRegionName;
    }

    public List<RegionSpareRelation> getSpareList()
    {
        return spareList;
    }

    public void setMainRegion(Integer mainRegion)
    {
        this.mainRegion = mainRegion;
    }
    
    public void setMainRegionName(String mainRegionName)
    {
        this.mainRegionName = mainRegionName;
    }
    
    public void setSpareList(List<RegionSpareRelation> spareList)
    {
        this.spareList = spareList;
    }
    
}
