package com.huawei.sharedrive.app.openapi.domain.statistics;

import java.text.ParseException;

import com.huawei.sharedrive.app.statistics.domain.NodeStatisticsDay;

public class NodeHistoryStatisticsInfo
{
   
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static NodeHistoryStatisticsInfo convert(NodeStatisticsDay nodeStatistics, String timeUnit)
        throws ParseException
    {
        if (null == nodeStatistics)
        {
            return null;
        }
        NodeHistoryStatisticsInfo info = new NodeHistoryStatisticsInfo();
        info.setTimePoint(TimePoint.convert(nodeStatistics.getDay(), timeUnit));
        info.setAddedDeletedFileCount(nodeStatistics.getAddedDeletedFileCount());
        info.setAddedDeletedSpaceUsed(nodeStatistics.getAddedDeletedSpaceUsed());
        info.setAddedFileCount(nodeStatistics.getAddedFileCount());
        info.setAddedSpaceUsed(nodeStatistics.getAddedSpaceUsed());
        info.setAddedTrashFileCount(nodeStatistics.getAddedTrashFileCount());
        info.setAddedTrashSpaceUsed(nodeStatistics.getAddedTrashSpaceUsed());
        info.setDeletedFileCount(nodeStatistics.getDeletedFileCount());
        info.setDeletedSpaceUsed(nodeStatistics.getDeletedSpaceUsed());
        info.setFileCount(nodeStatistics.getFileCount());
        info.setSpaceUsed(nodeStatistics.getSpaceUsed());
        info.setTrashFileCount(nodeStatistics.getTrashFileCount());
        info.setTrashSpaceUsed(nodeStatistics.getTrashSpaceUsed());
        return info;
    }
    
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
    
    public void clearAddedData()
    {
        setAddedDeletedFileCount(0L);
        setAddedDeletedSpaceUsed(0L);
        setAddedTrashFileCount(0L);
        setAddedTrashSpaceUsed(0L);
        setAddedFileCount(0L);
        setAddedSpaceUsed(0L);
    }
    
    public void resetSizeMb()
    {
        setAddedDeletedSpaceUsed(SizeUtils.getMbSize(getAddedDeletedSpaceUsed()));
        setAddedTrashSpaceUsed(SizeUtils.getMbSize(getAddedTrashSpaceUsed()));
        setAddedSpaceUsed(SizeUtils.getMbSize(getAddedSpaceUsed()));
        setDeletedSpaceUsed(SizeUtils.getMbSize(getDeletedSpaceUsed()));
        setTrashSpaceUsed(SizeUtils.getMbSize(getTrashSpaceUsed()));
        setSpaceUsed(SizeUtils.getMbSize(getSpaceUsed()));
        
    }
    
}
