package com.huawei.sharedrive.uam.statistics.domain;

import java.text.ParseException;

import com.huawei.sharedrive.uam.openapi.domain.TimePoint;

public class TerminalHistoryNode
{
    
    private TimePoint timePoint;
    
    private int userCount;
    
    private int deviceType;
    
    public TimePoint getTimePoint()
    {
        return timePoint;
    }
    
    public int getUserCount()
    {
        return userCount;
    }
    
    public void setTimePoint(TimePoint timePoint)
    {
        this.timePoint = timePoint;
    }
    
    public void setUserCount(int userCount)
    {
        this.userCount = userCount;
    }
    
    public static TerminalHistoryNode convert(TerminalStatisticsDay itemStatistics, String unit)
        throws ParseException
    {
        if (null == itemStatistics)
        {
            return null;
        }
        TerminalHistoryNode tempHistoryNode = new TerminalHistoryNode();
        tempHistoryNode.setTimePoint(TimePoint.convert(itemStatistics.getDay(), unit));
        tempHistoryNode.setUserCount(itemStatistics.getUserCount());
        tempHistoryNode.setDeviceType(itemStatistics.getDeviceType());
        return tempHistoryNode;
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
}
