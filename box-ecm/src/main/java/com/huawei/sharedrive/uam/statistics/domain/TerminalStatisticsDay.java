package com.huawei.sharedrive.uam.statistics.domain;

public class TerminalStatisticsDay
{
    private int day;
    
    private int deviceType;
    
    private String clientVersion;
    
    private int userCount;
    
    public String getClientVersion()
    {
        return clientVersion;
    }
    
    public int getDay()
    {
        return day;
    }
    
    public int getDeviceType()
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
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public void setUserCount(int userCount)
    {
        this.userCount = userCount;
    }
    
}
