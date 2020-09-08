package pw.cdmi.box.uam.statistics.domain;

import java.util.Date;

public class StatisticsTempChart
{
    
    private byte[] chartImage;
    
    private Date createdAt;
    
    private String id;
    
    public byte[] getChartImage()
    {
        return chartImage == null ? null : chartImage.clone();
    }
    
    public Date getCreatedAt()
    {
        return createdAt == null ? null : (Date) createdAt.clone();
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setChartImage(byte[] chartImage)
    {
        this.chartImage = (chartImage == null ? null : chartImage.clone());
    }
    
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = (createdAt == null ? null : (Date) createdAt.clone());
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
}
