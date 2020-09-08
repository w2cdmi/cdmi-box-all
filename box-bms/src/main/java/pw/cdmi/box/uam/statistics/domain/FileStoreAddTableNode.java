package pw.cdmi.box.uam.statistics.domain;

public class FileStoreAddTableNode
{
    private long spaceUsed;
    
    private long actualSpaceUsed;
    
    private String date;
    
    public FileStoreAddTableNode()
    {
        
    }
    
    public FileStoreAddTableNode(long spaceUsed, long actualSpaceUsed, String date)
    {
        this.spaceUsed = spaceUsed;
        this.actualSpaceUsed = actualSpaceUsed;
        this.date = date;
    }
    
    public long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public void setSpaceUsed(long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public long getActualSpaceUsed()
    {
        return actualSpaceUsed;
    }
    
    public void setActualSpaceUsed(long actualSpaceUsed)
    {
        this.actualSpaceUsed = actualSpaceUsed;
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
