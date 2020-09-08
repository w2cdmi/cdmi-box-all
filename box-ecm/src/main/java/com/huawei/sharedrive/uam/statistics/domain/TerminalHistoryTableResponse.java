package com.huawei.sharedrive.uam.statistics.domain;

import com.huawei.sharedrive.uam.statistics.StatisticsHistoryNode;

public class TerminalHistoryTableResponse extends StatisticsHistoryNode
{
    private int androidUserCount;
    
    private String date;
    
    private int iosUserCount;
    
    private int pcUserCount;
    
    private int webUserCount;
    
    public int getAndroidUserCount()
    {
        return androidUserCount;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public int getIosUserCount()
    {
        return iosUserCount;
    }
    
    public int getPcUserCount()
    {
        return pcUserCount;
    }
    
    public int getWebUserCount()
    {
        return webUserCount;
    }
    
    public void setAndroidUserCount(int androidUserCount)
    {
        this.androidUserCount = androidUserCount;
    }
    
    public void setDate(String date)
    {
        this.date = date;
    }
    
    public void setIosUserCount(int iosUserCount)
    {
        this.iosUserCount = iosUserCount;
    }
    
    public void setPcUserCount(int pcUserCount)
    {
        this.pcUserCount = pcUserCount;
    }
    
    public void setWebUserCount(int webUserCount)
    {
        this.webUserCount = webUserCount;
    }
    
}
