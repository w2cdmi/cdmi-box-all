package com.huawei.sharedrive.app.openapi.domain.node;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityStatus;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityType;

public class SetObjectAttributeRequest implements Serializable
{
    
    private static final long serialVersionUID = -3635141321217801183L;
    
    private String name;
    
    private String value;
    
    public SetObjectAttributeRequest()
    {
        
    }
    
    public SetObjectAttributeRequest(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if (StringUtils.isBlank(name))
        {
            throw new InvalidParamException("Name can not be null");
        }
        if (StringUtils.isBlank(value))
        {
            throw new InvalidParamException("Value can not be null");
        }
        
        SecurityType type = SecurityType.getSecurityType(name);
        if (type == null)
        {
            throw new InvalidParamException("Invalid attribute " + name);
        }
        
        if (type == SecurityType.CONFIDENTIAL)
        {
            SecurityStatus securityStatus = SecurityStatus.getSecurityStatus(Integer.valueOf(value));
            SecurityStatus securityKsoftStatus = SecurityStatus.getKsoftSecurityStatus(Integer.valueOf(value));
            if (securityStatus == null && securityKsoftStatus == null)
            {
                throw new InvalidParamException("Invalid attibute value " + value);
            }
        }
        else 
        {
            throw new InvalidParamException("Invalid attibute " + name);
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
