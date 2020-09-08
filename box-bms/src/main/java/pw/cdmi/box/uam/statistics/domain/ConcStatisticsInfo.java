package pw.cdmi.box.uam.statistics.domain;

public class ConcStatisticsInfo extends BasicStatisticsBean
{
    
    private int maxDownload;
    
    private int maxUpload;
    
    public int getMaxDownload()
    {
        return maxDownload;
    }
    
    public int getMaxUpload()
    {
        return maxUpload;
    }
    
    public void setMaxDownload(int maxDownload)
    {
        this.maxDownload = maxDownload;
    }
    
    public void setMaxUpload(int maxUpload)
    {
        this.maxUpload = maxUpload;
    }
}
