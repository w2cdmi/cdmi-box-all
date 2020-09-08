package com.huawei.sharedrive.app.statistics.domain;


public class TempConcStatistics
{
    private int day;

    private String host;

    private int maxDownload;

    private int maxUpload;

    private int timeUnit;

    public int getDay()
    {
        return day;
    }

    public String getHost()
    {
        return host;
    }

    public int getMaxDownload()
    {
        return maxDownload;
    }

    public int getMaxUpload()
    {
        return maxUpload;
    }

    public int getTimeUnit()
    {
        return timeUnit;
    }

    public void setDay(int day)
    {
        this.day = day;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public void setMaxDownload(int maxDownload)
    {
        this.maxDownload = maxDownload;
    }

    public void setMaxUpload(int maxUpload)
    {
        this.maxUpload = maxUpload;
    }

    public void setTimeUnit(int timeUnit)
    {
        this.timeUnit = timeUnit;
    }
}
