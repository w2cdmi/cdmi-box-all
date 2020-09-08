package pw.cdmi.box.disk.share.domain.mail;

import java.io.Serializable;

public class RequestAttribute implements Serializable
{
    
    private static final long serialVersionUID = -5096621017018478389L;
    
    private String name;
    
    private String value;
    
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
