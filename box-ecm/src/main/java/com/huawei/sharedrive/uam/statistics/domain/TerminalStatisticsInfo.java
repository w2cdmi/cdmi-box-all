package com.huawei.sharedrive.uam.statistics.domain;

import pw.cdmi.common.domain.Terminal;

public class TerminalStatisticsInfo
{
    private String clientVersion;
    
    private int day;
    
    private String deviceType;
    
    private int userCount;
    
    public TerminalStatisticsInfo()
    {
        
    }
    
    public TerminalStatisticsInfo(TerminalStatisticsDay statisticsDay)
    {
        clientVersion = statisticsDay.getClientVersion();
        day = statisticsDay.getDay();
        userCount = statisticsDay.getUserCount();
        this.resetDeviceType(statisticsDay);
        clientVersion = statisticsDay.getClientVersion();
    }
    
    public String getClientVersion()
    {
        return clientVersion;
    }
    
    public int getDay()
    {
        return day;
    }
    
    public String getDeviceType()
    {
        return deviceType;
    }
    
    public int getUserCount()
    {
        return userCount;
    }
    
    public void setClientVersion(String clientVersion)
    {
        this.clientVersion = clientVersion;
    }
    
    public void setDay(int day)
    {
        this.day = day;
    }
    
    public void setDeviceType(String deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public void setUserCount(int userCount)
    {
        this.userCount = userCount;
    }
    
    private void resetDeviceType(TerminalStatisticsDay statisticsDay)
    {
        switch (statisticsDay.getDeviceType())
        {
            case Terminal.CLIENT_TYPE_ANDROID:
                this.deviceType = Terminal.CLIENT_TYPE_ANDROID_STR;
                break;
            case Terminal.CLIENT_TYPE_IOS:
                this.deviceType = Terminal.CLIENT_TYPE_IOS_STR;
                break;
            case Terminal.CLIENT_TYPE_PC:
                this.deviceType = Terminal.CLIENT_TYPE_PC_STR;
                break;
            case Terminal.CLIENT_TYPE_WEB:
                this.deviceType = Terminal.CLIENT_TYPE_WEB_STR;
                break;
            default:
                this.deviceType = "others";
                break;
        }
    }
    
}
