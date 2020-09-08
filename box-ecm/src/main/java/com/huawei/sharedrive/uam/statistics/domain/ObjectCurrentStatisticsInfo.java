package com.huawei.sharedrive.uam.statistics.domain;

public class ObjectCurrentStatisticsInfo
{
    
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
