package pw.cdmi.box.disk.client.domain.teamspace;

import java.io.Serializable;

public class TeamSpaceAttribute implements Serializable
{
    
    private static final long serialVersionUID = 4260306419764569121L;
    
    private String name;
    
    private String value;
    
    public TeamSpaceAttribute()
    {
        
    }
    
    public TeamSpaceAttribute(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
}
