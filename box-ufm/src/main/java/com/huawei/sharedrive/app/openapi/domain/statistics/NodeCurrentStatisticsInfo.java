package com.huawei.sharedrive.app.openapi.domain.statistics;

import com.huawei.sharedrive.app.dataserver.domain.Region;
import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.statistics.domain.NodeStatisticsDay;

public class NodeCurrentStatisticsInfo
{
    private String appId;

    private Long deletedFileCount;

    private Long deletedSpaceUsed;

    private Long fileCount;

    private Integer regionId;

    private String regionName;

    private Long spaceUsed;

    private Long trashFileCount;

    private Long trashSpaceUsed;

    public String getAppId()
    {
        return appId;
    }

    public Long getDeletedFileCount()
    {
        return deletedFileCount;
    }

    public Long getDeletedSpaceUsed()
    {
        return deletedSpaceUsed;
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

    public Long getTrashFileCount()
    {
        return trashFileCount;
    }

    public Long getTrashSpaceUsed()
    {
        return trashSpaceUsed;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setDeletedFileCount(Long deletedFileCount)
    {
        this.deletedFileCount = deletedFileCount;
    }
    
    public void setDeletedSpaceUsed(Long deletedSpaceUsed)
    {
        this.deletedSpaceUsed = deletedSpaceUsed;
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
    
    public void setTrashFileCount(Long trashFileCount)
    {
        this.trashFileCount = trashFileCount;
    }
    
    public void setTrashSpaceUsed(Long trashSpaceUsed)
    {
        this.trashSpaceUsed = trashSpaceUsed;
    }
    
    public static NodeCurrentStatisticsInfo convertInto(NodeStatisticsDay nodeStatistics, RegionService regionService)
    {
        NodeCurrentStatisticsInfo info = new NodeCurrentStatisticsInfo();
        info.setAppId(nodeStatistics.getAppId());
        info.setRegionId(nodeStatistics.getRegionId());
        if(null == nodeStatistics.getRegionId())
        {
            info.setRegionId(null);
        }
        else
        {
            Region region = regionService.getRegion(nodeStatistics.getRegionId());
            if(null == region)
            {
                info.setRegionName("unknown");
            }
            else
            {
                info.setRegionName(region.getName());
            }
        }
        info.setDeletedFileCount(nodeStatistics.getDeletedFileCount());
        info.setDeletedSpaceUsed(SizeUtils.getMbSize(nodeStatistics.getDeletedSpaceUsed()));
        info.setFileCount(nodeStatistics.getFileCount());
        info.setSpaceUsed(SizeUtils.getMbSize(nodeStatistics.getSpaceUsed()));
        info.setTrashFileCount(nodeStatistics.getTrashFileCount());
        info.setTrashSpaceUsed(SizeUtils.getMbSize(nodeStatistics.getTrashSpaceUsed()));
        return info;
    }
}
