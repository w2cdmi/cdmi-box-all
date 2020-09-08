package com.huawei.sharedrive.uam.statistics.domain;

import java.util.Date;

public class StatisticsTempChart
{
    
    private byte[] chartImage;
    
    private Date createdAt;
    
    private String id;
    
    public byte[] getChartImage()
    {
        if (null != chartImage)
        {
            return chartImage.clone();
        }
        return new byte[]{};
    }
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setChartImage(byte[] chartImage)
    {
        if (null != chartImage)
        {
            this.chartImage = chartImage.clone();
        }
        else
        {
            this.chartImage = null;
        }
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (null != createdAt)
        {
            this.createdAt = (Date) createdAt.clone();
        }
        else
        {
            this.createdAt = null;
        }
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
}
