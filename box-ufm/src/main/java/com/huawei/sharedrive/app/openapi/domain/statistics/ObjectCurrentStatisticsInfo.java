package com.huawei.sharedrive.app.openapi.domain.statistics;

import com.huawei.sharedrive.app.dataserver.domain.Region;
import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.statistics.domain.ObjectStatisticsDay;

public class ObjectCurrentStatisticsInfo
{

    public static ObjectCurrentStatisticsInfo convertInto(ObjectStatisticsDay objectStatistics, RegionService regionService)
    {
        ObjectCurrentStatisticsInfo info = new ObjectCurrentStatisticsInfo();
        info.setRegionId(objectStatistics.getRegionId());
        if(null != objectStatistics.getRegionId() && objectStatistics.getRegionId() > 0)
        {
            Region region = regionService.getRegion(objectStatistics.getRegionId());
            if(null == region)
            {
                info.setRegionName("unknown");
            }
            else
            {
                info.setRegionName(region.getName());
            }
        }
        else
        {
            info.setRegionName("unknown");
        }
        info.setFileCount(objectStatistics.getFileCount());
        info.setSpaceUsed(SizeUtils.getMbSize(objectStatistics.getSpaceUsed()));
        info.setActualFileCount(objectStatistics.getActualFileCount());
        info.setActualSpaceUsed(SizeUtils.getMbSize(objectStatistics.getActualSpaceUsed()));
        return info;
    }


    private Long actualFileCount;

    private Long actualSpaceUsed;

    private Long fileCount;

    private Integer regionId;

    private String regionName;

    private Long spaceUsed;

    public Long getActualFileCount()
    {
        return actualFileCount;
    }

    public Long getActualSpaceUsed()
    {
        return actualSpaceUsed;
    }

    public Long getFileCount()
    {
        return fileCount;
    }

    public Integer getRegionId()
    {
        return regionId;
    }

    public String getRegionName()
    {
        return regionName;
    }

    public Long getSpaceUsed()
    {
        return spaceUsed;
    }

    public void setActualFileCount(Long actualFileCount)
    {
        this.actualFileCount = actualFileCount;
    }

    public void setActualSpaceUsed(Long actualSpaceUsed)
    {
        this.actualSpaceUsed = actualSpaceUsed;
    }

    public void setFileCount(Long fileCount)
    {
        this.fileCount = fileCount;
    }

    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }

    
    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
}
