package pw.cdmi.box.disk.httpclient.rest.request;

public class FileSearchRequest
{
    private String ownerID;
    
    private String name;
    
    private Integer offset = 0;
    
    private Integer limit = 100;
    
    public String getOwnerID()
    {
        return ownerID;
    }
    
    public void setOwnerID(String ownerID)
    {
        this.ownerID = ownerID;
    }
    
    public Integer getOffset()
    {
        return offset;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
}
