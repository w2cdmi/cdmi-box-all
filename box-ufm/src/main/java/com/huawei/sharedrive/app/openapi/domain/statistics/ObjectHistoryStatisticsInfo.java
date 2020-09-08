package com.huawei.sharedrive.app.openapi.domain.statistics;

import java.text.ParseException;

import com.huawei.sharedrive.app.statistics.domain.ObjectStatisticsDay;

public class ObjectHistoryStatisticsInfo
{
    
    public static ObjectHistoryStatisticsInfo convert(ObjectStatisticsDay objectStatistics, String timeUnit)
        throws ParseException
    {
        if (null == objectStatistics)
        {
            return null;
        }
        ObjectHistoryStatisticsInfo info = new ObjectHistoryStatisticsInfo();
        info.setTimePoint(TimePoint.convert(objectStatistics.getDay(), timeUnit));
        info.setAddedActualFileCount(objectStatistics.getAddedActualFileCount());
        info.setAddedActualSpaceUsed(objectStatistics.getAddedActualSpaceUsed());
        info.setAddedFileCount(objectStatistics.getAddedFileCount());
        info.setAddedSpaceUsed(objectStatistics.getAddedSpaceUsed());
        info.setFileCount(objectStatistics.getFileCount());
        info.setSpaceUsed(objectStatistics.getSpaceUsed());
        info.setActualFileCount(objectStatistics.getActualFileCount());
        info.setActualSpaceUsed(objectStatistics.getActualSpaceUsed());
        return info;
    }
    
    private Long actualFileCount;
    
    private Long actualSpaceUsed;
    
    private Long addedActualFileCount;
    
    private Long addedActualSpaceUsed;
    
    private Long addedFileCount;
    
    private Long addedSpaceUsed;
    
    private Long fileCount;
    
    private Long spaceUsed;
    
    private TimePoint timePoint;
    
    public Long getActualFileCount()
    {
        return actualFileCount;
    }
    
    public Long getActualSpaceUsed()
    {
        return actualSpaceUsed;
    }
    
    public Long getAddedActualFileCount()
    {
        return addedActualFileCount;
    }
    
    public Long getAddedActualSpaceUsed()
    {
        return addedActualSpaceUsed;
    }
    
    public Long getAddedFileCount()
    {
        return addedFileCount;
    }
    
    public Long getAddedSpaceUsed()
    {
        return addedSpaceUsed;
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
    
    public void setActualFileCount(Long actualFileCount)
    {
        this.actualFileCount = actualFileCount;
    }
    
    public void setActualSpaceUsed(Long actualSpaceUsed)
    {
        this.actualSpaceUsed = actualSpaceUsed;
    }
    
    public void setAddedActualFileCount(Long addedActualFileCount)
    {
        this.addedActualFileCount = addedActualFileCount;
    }
    
    public void setAddedActualSpaceUsed(Long addedActualSpaceUsed)
    {
        this.addedActualSpaceUsed = addedActualSpaceUsed;
    }
    
    public void setAddedFileCount(Long addedFileCount)
    {
        this.addedFileCount = addedFileCount;
    }
    
    public void setAddedSpaceUsed(Long addedSpaceUsed)
    {
        this.addedSpaceUsed = addedSpaceUsed;
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
    
    public void clearAddedData()
    {
        this.setAddedActualFileCount(0L);
        this.setAddedActualSpaceUsed(0L);
        this.setAddedFileCount(0L);
        this.setAddedSpaceUsed(0L);
    }
    
    public void resetSizeMb()
    {
        this.setActualSpaceUsed(SizeUtils.getMbSize(this.getActualSpaceUsed()));
        this.setAddedActualSpaceUsed(SizeUtils.getMbSize(this.getAddedActualSpaceUsed()));
        this.setAddedSpaceUsed(SizeUtils.getMbSize(this.getAddedSpaceUsed()));
        this.setSpaceUsed(SizeUtils.getMbSize(this.getSpaceUsed()));
    }
}
