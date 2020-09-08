package pw.cdmi.box.uam.statistics.domain;

import pw.cdmi.box.uam.httpclient.domain.TimePoint;

public class NodeHistoryStatisticsInfo
{
    
    private Long addedDeletedFileCount;
    
    private Long addedDeletedSpaceUsed;
    
    private Long addedFileCount;
    
    private Long addedSpaceUsed;
    
    private Long addedTrashFileCount;
    
    private Long addedTrashSpaceUsed;
    
    private Long deletedFileCount;
    
    private Long deletedSpaceUsed;
    
    private Long fileCount;
    
    private Long spaceUsed;
    
    private TimePoint timePoint;
    
    private Long trashFileCount;
    
    private Long trashSpaceUsed;
    
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
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public TimePoint getTimePoint()
    {
        return timePoint;
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
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public void setTimePoint(TimePoint timePoint)
    {
        this.timePoint = timePoint;
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
