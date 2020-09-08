package pw.cdmi.box.disk.client.domain.user;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.exception.InvalidParamException;


public class SetConfigRequest
{
    private static final int MAX_NAME_LENGTH = 64;
    
    private static final int MAX_VALUE_LENGTH = 255;
    
    private String name;
    
    private String value;
    
    public SetConfigRequest()
    {
        name = null;
        value = null;
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
    
    public void checkParameter()
    {
        if (StringUtils.isBlank(name) || name.length() > MAX_NAME_LENGTH)
        {
            throw new InvalidParamException("Invalid config name " + name);
        }
        if (StringUtils.isBlank(value) || value.length() > MAX_VALUE_LENGTH)
        {
            throw new InvalidParamException("Invalid config value " + value);
        }
        
    }
}
