package com.huawei.sharedrive.uam.statistics.domain;

import java.util.Calendar;

import com.huawei.sharedrive.uam.openapi.domain.TimePoint;
import com.huawei.sharedrive.uam.statistics.StatisticsHistoryNode;

public class ThirdTerminalData extends StatisticsHistoryNode
{
    public static String getDate(TimePoint timePoint)
    {
        StringBuilder sb = new StringBuilder();
        if ("year".equalsIgnoreCase(timePoint.getUnit()))
        {
            sb.append(timePoint.getYear());
        }
        else if ("day".equalsIgnoreCase(timePoint.getUnit()))
        {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, timePoint.getYear());
            cal.set(Calendar.DAY_OF_YEAR, timePoint.getNumber());
            int month = cal.get(Calendar.MONTH) + 1;
            sb.append(timePoint.getYear())
                .append('-')
                .append(month)
                .append('-')
                .append(cal.get(Calendar.DAY_OF_MONTH));
        }
        else
        {
            sb.append(timePoint.getYear()).append('-').append(timePoint.getNumber());
        }
        
        return sb.toString();
    }
    
    private String clientVersion;
    
    private int count;
    
    private String dateStr;
    
    private int deviceType;
    
    public ThirdTerminalData(int count, int deviceType, String deviceName, TimePoint timePoint)
    {
        this.count = count;
        this.deviceType = deviceType;
        this.clientVersion = deviceName;
        this.setTimePoint(timePoint);
        this.dateStr = getDate(timePoint);
    }
    
    public ThirdTerminalData(int count, String clientVersion, TimePoint timePoint)
    {
        this.count = count;
        this.clientVersion = clientVersion;
        this.setTimePoint(timePoint);
        this.dateStr = getDate(timePoint);
    }
    
    public String getClientVersion()
    {
        return clientVersion;
    }
    
    public int getCount()
    {
        return count;
    }
    
    public String getDateStr()
    {
        return dateStr;
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public void setClientVersion(String clientVersion)
    {
        this.clientVersion = clientVersion;
    }
    
    public void setCount(int count)
    {
        this.count = count;
    }
    
    public void setDateStr(String dateStr)
    {
        this.dateStr = dateStr;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
}
