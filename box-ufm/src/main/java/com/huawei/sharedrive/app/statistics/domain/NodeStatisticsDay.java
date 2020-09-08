package com.huawei.sharedrive.app.statistics.domain;

public class NodeStatisticsDay
{
    
    private Long day;
    
    private String appId;
    
    private Integer regionId;
    
    private Long fileCount;
    
    private Long trashFileCount;
    
    private Long deletedFileCount;
    
    private Long spaceUsed;
    
    private Long trashSpaceUsed;
    
    private Long deletedSpaceUsed;
    
    private Long addedFileCount;
    
    private Long addedTrashFileCount;
    
    private Long addedDeletedFileCount;
    
    private Long addedSpaceUsed;
    
    private Long addedTrashSpaceUsed;
    
    private Long addedDeletedSpaceUsed;
    
    public Long getAddedDeletedFileCount()
    {
        return addedDeletedFileCount;
    }
    
    public Long getAddedDeletedSpaceUsed()
    {
        return addedDeletedSpaceUsed;
    }
    
    public Long getAddedFileCount()
    {
        return addedFileCount;
    }
    
    public Long getAddedSpaceUsed()
    {
        return addedSpaceUsed;
    }
    
    public Long getAddedTrashFileCount()
    {
        return addedTrashFileCount;
    }
    
    public Long getAddedTrashSpaceUsed()
    {
        return addedTrashSpaceUsed;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public Long getDay()
    {
        return day;
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
    
    public void setAddedDeletedFileCount(Long addedDeletedFileCount)
    {
        this.addedDeletedFileCount = addedDeletedFileCount;
    }
    
    public void setAddedDeletedSpaceUsed(Long addedDeletedSpaceUsed)
    {
        this.addedDeletedSpaceUsed = addedDeletedSpaceUsed;
    }
    
    public void setAddedFileCount(Long addedFileCount)
    {
        this.addedFileCount = addedFileCount;
    }
    
    public void setAddedSpaceUsed(Long addedSpaceUsed)
    {
        this.addedSpaceUsed = addedSpaceUsed;
    }
    
    public void setAddedTrashFileCount(Long addedTrashFileCount)
    {
        this.addedTrashFileCount = addedTrashFileCount;
    }
    
    public void setAddedTrashSpaceUsed(Long addedTrashSpaceUsed)
    {
        this.addedTrashSpaceUsed = addedTrashSpaceUsed;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setDay(Long day)
    {
        this.day = day;
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
    
    public void clearAddedData()
    {
        setAddedDeletedFileCount(0L);
        setAddedDeletedSpaceUsed(0L);
        setAddedSpaceUsed(0L);
        setAddedFileCount(0L);
        setAddedTrashFileCount(0L);
        setAddedTrashSpaceUsed(0L);
    }
    
    public void setAddedData(NodeStatisticsDay beforeData)
    {
        setAddedDeletedFileCount(getDeletedFileCount()
            - beforeData.getDeletedFileCount());
        setAddedDeletedSpaceUsed(getDeletedSpaceUsed()
            - beforeData.getDeletedSpaceUsed());
        setAddedFileCount(getFileCount() - beforeData.getFileCount());
        setAddedSpaceUsed(getSpaceUsed() - beforeData.getSpaceUsed());
        setAddedTrashFileCount(getTrashFileCount() - beforeData.getTrashFileCount());
        setAddedTrashSpaceUsed(getTrashSpaceUsed() - beforeData.getTrashSpaceUsed());
    }
}
