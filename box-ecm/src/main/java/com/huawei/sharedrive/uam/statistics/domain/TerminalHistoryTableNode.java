package com.huawei.sharedrive.uam.statistics.domain;

public class TerminalHistoryTableNode
{
    private int deviceType;
    
    private String date;
    
    private int userCount;
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public void setDate(String date)
    {
        this.date = date;
    }
    
    public int getUserCount()
    {
        return userCount;
    }
    
    public void setUserCount(int userCount)
    {
        this.userCount = userCount;
    }
    
}
