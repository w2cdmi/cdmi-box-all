package pw.cdmi.box.uam.statistics.domain;

public class FileTableNode
{
    private Long fileCount;
    
    private Long actualFileCount;
    
    private String date;
    
    public FileTableNode()
    {
        
    }
    
    public FileTableNode(long fileCount, long actualFileCount, String date)
    {
        this.fileCount = fileCount;
        this.actualFileCount = actualFileCount;
        this.date = date;
    }
    
    public Long getFileCount()
    {
        return fileCount;
    }
    
    public void setFileCount(Long fileCount)
    {
        this.fileCount = fileCount;
    }
    
    public Long getActualFileCount()
    {
        return actualFileCount;
    }
    
    public void setActualFileCount(Long actualFileCount)
    {
        this.actualFileCount = actualFileCount;
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
