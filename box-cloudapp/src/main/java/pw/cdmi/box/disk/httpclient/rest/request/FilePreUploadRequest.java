package pw.cdmi.box.disk.httpclient.rest.request;

public class FilePreUploadRequest
{
    
    private String name;
    
    private String parent;
    
    private long size;
    
    private String sha1;
    
    public FilePreUploadRequest()
    {
        
    }
    
    public FilePreUploadRequest(String parent, String name, long size, String sha1)
    {
        this.parent = parent;
        this.name = name;
        this.size = size;
        this.sha1 = sha1;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getParent()
    {
        return parent;
    }
    
    public void setParent(String parent)
    {
        this.parent = parent;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
}
