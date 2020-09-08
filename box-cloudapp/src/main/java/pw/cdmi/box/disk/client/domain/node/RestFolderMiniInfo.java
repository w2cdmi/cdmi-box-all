package pw.cdmi.box.disk.client.domain.node;

/**
 * FOLDER MINI OBJECT
 * 
 */

public class RestFolderMiniInfo
{
    protected String id;
    
    protected String name;
    
    protected String parent;
    
    protected String type;
    
    public String getId()
    {
        return id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getParent()
    {
        return parent;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setParent(String parent)
    {
        this.parent = parent;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
}
