package com.huawei.sharedrive.app.statistics.domain;

public class SysConcStatisticsDay
{
    private int day;
    
    private int maxUpload;
    
    private int maxDownload;
    
    public int getMaxUpload()
    {
        return maxUpload;
    }
    
    public void setMaxUpload(int maxUpload)
    {
        this.maxUpload = maxUpload;
    }
    
    public int getMaxDownload()
    {
        return maxDownload;
    }
    
    public void setMaxDownload(int maxDownload)
    {
        this.maxDownload = maxDownload;
    }
    
    public int getDay()
    {
        return day;
    }
    
    public void setDay(int day)
    {
        this.day = day;
    }
    
}
