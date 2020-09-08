package pw.cdmi.box.disk.client.domain.teamspace;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.exception.InvalidParamException;


public class SetTeamSpaceAttrRequest implements Serializable
{
    
    private static final long serialVersionUID = 1380569874891882767L;
    
    private String name;
    
    private String value;
    
    public SetTeamSpaceAttrRequest()
    {
        
    }
    
    public SetTeamSpaceAttrRequest(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
    
    public void checkParameter()
    {
        if (StringUtils.isBlank(name))
        {
            throw new InvalidParamException("Name can not be null");
        }
        if (StringUtils.isBlank(value))
        {
            throw new InvalidParamException("Value can not be null");
        }
        TeamSpaceAttributeEnum config = TeamSpaceAttributeEnum.getTeamSpaceConfig(name);
        if (config == null)
        {
            throw new InvalidParamException("Unsupport config " + name);
        }
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
