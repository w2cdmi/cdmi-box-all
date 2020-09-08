package pw.cdmi.box.disk.httpclient.rest.request;

public class FolderCreateRequest
{
    private String name;
    
    private String parent;
    
    private long contentCreatedAt;
    
    private long contentModifiedAt;
    
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
    
    public long getContentCreatedAt()
    {
        return contentCreatedAt;
    }
    
    public void setContentCreatedAt(long contentCreatedAt)
    {
        this.contentCreatedAt = contentCreatedAt;
    }
    
    public long getContentModifiedAt()
    {
        return contentModifiedAt;
    }
    
    public void setContentModifiedAt(long contentModifiedAt)
    {
        this.contentModifiedAt = contentModifiedAt;
    }
    
}
