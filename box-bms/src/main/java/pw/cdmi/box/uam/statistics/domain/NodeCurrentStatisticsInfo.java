package pw.cdmi.box.uam.statistics.domain;

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
    
}
