package pw.cdmi.box.disk.system.domain.custom;

import java.io.Serializable;

public class CustomAttribute implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 5917459398822009070L;
    
    private String key;
    
    private String param;
    
    private String value;
    
    public String getKey()
    {
        return key;
    }
    
    public String getParam()
    {
        return param;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setKey(String key)
    {
        this.key = key;
    }
    
    public void setParam(String param)
    {
        this.param = param;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
}
