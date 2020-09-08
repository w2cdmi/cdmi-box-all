package com.huawei.sharedrive.app.statistics.domain;

import java.util.Calendar;

public class ObjectStatisticsDay
{
    
    private Long day;
    
    private Integer regionId;
    
    private Long fileCount;
    
    private Long actualFileCount;
    
    private Long spaceUsed;
    
    private Long actualSpaceUsed;
    
    private Long addedFileCount;
    
    private Long addedActualFileCount;
    
    private Long addedSpaceUsed;
    
    private Long addedActualSpaceUsed;
    
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
    
    public Long getDay()
    {
        return day;
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
    
    public void setDay(Long day)
    {
        this.day = day;
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
    
    public void clearAddedData()
    {
        this.setAddedActualFileCount(0L);
        this.setAddedActualSpaceUsed(0L);
        this.setAddedFileCount(0L);
        this.setAddedSpaceUsed(0L);
    }
    
    public void setAddedData(ObjectStatisticsDay beforeData)
    {
        this.setAddedActualFileCount(this.getActualFileCount() - beforeData.getActualFileCount());
        this.setAddedActualSpaceUsed(this.getActualSpaceUsed() - beforeData.getActualSpaceUsed());
        this.setAddedFileCount(this.getFileCount() - beforeData.getFileCount());
        this.setAddedSpaceUsed(this.getSpaceUsed() - beforeData.getSpaceUsed());
    }
    
    public void setAddedData2(ObjectStatisticsDay beforeData)
    {
        this.setAddedActualFileCount(this.getAddedActualFileCount() + beforeData.getAddedActualFileCount());
        this.setAddedActualSpaceUsed(this.getAddedActualSpaceUsed() + beforeData.getAddedActualSpaceUsed());
        this.setAddedFileCount(this.getAddedFileCount() + beforeData.getAddedFileCount());
        this.setAddedSpaceUsed(this.getAddedSpaceUsed() + beforeData.getAddedSpaceUsed());
    }
    
    public boolean withSameWeek(ObjectStatisticsDay beforeData)
    {
        long thisDay = day;
        long nextDay = beforeData.getDay();
        long year = thisDay / 10000;
        long nextYear = beforeData.getDay() / 10000;
        if (year != nextYear)
        {
            return false;
        }
        Calendar thisDate = getCalendar((int) thisDay);
        Calendar nextDate = getCalendar((int) nextDay);
        if (thisDate.get(Calendar.WEEK_OF_YEAR) != nextDate.get(Calendar.WEEK_OF_YEAR))
        {
            return false;
        }
        if (thisDate.get(Calendar.DAY_OF_WEEK) < nextDate.get(Calendar.DAY_OF_WEEK))
        {
            return true;
        }
        return false;
    }
    
    /**
     * 判断该天是否同年同月靠前的天数
     * 
     * @param input
     * @return
     */
    public boolean withSameDay(ObjectStatisticsDay input)
    {
        long thisDay = day;
        long nextDay = input.getDay();
        long year = thisDay / 10000;
        long nextYear = nextDay / 10000;
        if (year != nextYear)
        {
            return false;
        }
        long month = (thisDay % 10000) / 100;
        long nextMonth = (nextDay % 10000) / 100;
        if (month != nextMonth)
        {
            return false;
        }
        if (thisDay % 100 < nextDay % 100)
        {
            return true;
        }
        return false;
    }
    
    /**
     * 判断该天是否同年同一季度靠前的天数
     * 
     * @param input
     * @return
     */
    public boolean withSameSeason(ObjectStatisticsDay input)
    {
        long thisDay = day;
        long nextDay = input.getDay();
        long year = thisDay / 10000;
        long nextYear = nextDay / 10000;
        if (year != nextYear)
        {
            return false;
        }
        Calendar thisDate = getCalendar((int) thisDay);
        Calendar nextDate = getCalendar((int) nextDay);
        
        // Calendar.MONTH 为 0~11 通过，除以3划分为4个季度，（0,1,2）（3,4,5）（6,7,8）（9,10,11）
        int season1 = thisDate.get(Calendar.MONTH) / 3;
        int season2 = nextDate.get(Calendar.MONTH) / 3;
        if (season1 != season2)
        {
            return false;
        }
        if (thisDate.get(Calendar.DAY_OF_YEAR) < nextDate.get(Calendar.DAY_OF_YEAR))
        {
            return true;
        }
        if (thisDate.get(Calendar.DAY_OF_YEAR) < nextDate.get(Calendar.DAY_OF_YEAR))
        {
            return true;
        }
        return false;
    }
    
    /**
     * 判断该天是否同年同一季度靠前的天数
     * 
     * @param input
     * @return
     */
    public boolean withSameYear(ObjectStatisticsDay input)
    {
        long thisDay = day;
        long nextDay = input.getDay();
        long year = thisDay / 10000;
        long nextYear = nextDay / 10000;
        if (year != nextYear)
        {
            return false;
        }
        if (thisDay % 10000 < nextDay % 10000)
        {
            return true;
        }
        return false;
    }
    
   private Calendar getCalendar(int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, day / 10000);
        calendar.set(Calendar.MONTH, day % 10000 / 100 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day % 100);
        return calendar;
    }
}
