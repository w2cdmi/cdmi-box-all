package pw.cdmi.box.disk.client.domain.message;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import pw.cdmi.box.disk.utils.BusinessConstants;

public class MessageResponse extends BasicMessage
{
    
    private Map<String, Object> params;
    
    private String providerName;
    
    private String providerUsername;
    
    private String status;
    
    private String type;
    
    public void addParam(String name, Object value)
    {
        if (params == null)
        {
            params = new HashMap<String, Object>(BusinessConstants.INITIAL_CAPACITIES);
        }
        params.put(name, value);
    }
    
    public Object getParam(String name)
    {
        if (params == null)
        {
            return null;
        }
        return params.get(name);
    }
    
    public Map<String, Object> getParams()
    {
        return params;
    }
    
    public String getProviderName()
    {
        return providerName;
    }
    
    public String getProviderUsername()
    {
        return providerUsername;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }
    
    
    public void setProviderName(String providerName)
    {
        this.providerName = providerName;
    }
    
    public void setProviderUsername(String providerUsername)
    {
        this.providerUsername = providerUsername;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
    
}
