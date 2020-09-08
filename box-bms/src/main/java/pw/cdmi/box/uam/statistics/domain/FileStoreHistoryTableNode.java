package pw.cdmi.box.uam.statistics.domain;

public class FileStoreHistoryTableNode
{
    private Long spaceUsed;
    
    private String date;
    
    public FileStoreHistoryTableNode()
    {
        
    }
    
    public FileStoreHistoryTableNode(long spaceCount, String date)
    {
        this.spaceUsed = spaceCount;
        this.date = date;
    }
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
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
