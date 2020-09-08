package com.huawei.sharedrive.uam.openapi.domain.account;

import java.io.Serializable;

/**
 * 
 * @version CloudStor CSE Service OpenClient Subproject, 2015-9-17
 * @see
 * @since
 */
public class RestAccountConfig implements Serializable
{
    
    private static final long serialVersionUID = -426497327143656214L;
    
    private String name;
    
    private String value;
    
    public RestAccountConfig()
    {
        
    }
    
    public RestAccountConfig(String name, String value)
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
