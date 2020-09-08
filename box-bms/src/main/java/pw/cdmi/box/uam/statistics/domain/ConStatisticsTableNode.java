package pw.cdmi.box.uam.statistics.domain;

public class ConStatisticsTableNode
{
    private int maxUpload;
    
    private int maxDownload;
    
    private String date;
    
    public ConStatisticsTableNode()
    {
        
    }
    
    public ConStatisticsTableNode(int upload, int download, String date)
    {
        maxUpload = upload;
        maxDownload = download;
        this.date = date;
    }
    
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
    
    public String getDate()
    {
        return date;
    }
    
    public void setDate(String date)
    {
        this.date = date;
    }
    
}
