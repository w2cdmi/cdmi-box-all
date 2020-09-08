package com.huawei.sharedrive.app.openapi.domain.statistics.concurrence;

import java.text.ParseException;

import com.huawei.sharedrive.app.openapi.domain.statistics.TimePoint;
import com.huawei.sharedrive.app.statistics.domain.SysConcStatisticsDay;


public class ConcStatisticsInfo
{
    private int maxDownload;

    private int maxUpload;

    private TimePoint timePoint;

    public int getMaxDownload()
    {
        return maxDownload;
    }

    public int getMaxUpload()
    {
        return maxUpload;
    }

    public TimePoint getTimePoint()
    {
        return timePoint;
    }

    public void setMaxDownload(int maxDownload)
    {
        this.maxDownload = maxDownload;
    }
    
    public void setMaxUpload(int maxUpload)
    {
        this.maxUpload = maxUpload;
    }
    
    public void setTimePoint(TimePoint timePoint)
    {
        this.timePoint = timePoint;
    }

    public static ConcStatisticsInfo convert(SysConcStatisticsDay nodeStatistics, String timeUnit) throws ParseException
    {
        if(null == nodeStatistics)
        {
            return null;
        }
        ConcStatisticsInfo concStatistics = new ConcStatisticsInfo();
        concStatistics.setTimePoint(TimePoint.convert(nodeStatistics.getDay(), timeUnit));
        concStatistics.setMaxUpload(nodeStatistics.getMaxUpload());
        concStatistics.setMaxDownload(nodeStatistics.getMaxDownload());
        return concStatistics;
    }

    
}
