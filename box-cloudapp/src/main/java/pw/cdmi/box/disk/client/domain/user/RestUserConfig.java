package pw.cdmi.box.disk.client.domain.user;

import java.io.Serializable;

public class RestUserConfig implements Serializable
{

    private static final long serialVersionUID = 6563548929994405627L;

    private String name;
    
    private String value;

    public RestUserConfig()
    {
        
    }
    
    public RestUserConfig(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
    
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
    
}
