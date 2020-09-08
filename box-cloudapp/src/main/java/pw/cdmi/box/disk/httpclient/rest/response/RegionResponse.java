package pw.cdmi.box.disk.httpclient.rest.response;

public class RegionResponse extends BaseResponse
{
    private String id;
    
    private String name;
    
    private String description;
    
    private boolean defaultRegion;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public boolean isDefaultRegion()
    {
        return defaultRegion;
    }
    
    public void setDefaultRegion(boolean defaultRegion)
    {
        this.defaultRegion = defaultRegion;
    }
    
}
