package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;

public class RestRegionInfo implements Serializable
{
    private static final long serialVersionUID = 4859545404949615387L;
    
    private boolean defaultRegion;
    
    private String description;
    
    private int id;
    
    private String name;
    
    public boolean isDefaultRegion()
    {
        return defaultRegion;
    }
    
    public void setDefaultRegion(boolean defaultRegion)
    {
        this.defaultRegion = defaultRegion;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
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
}
