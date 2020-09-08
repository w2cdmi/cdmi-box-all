package com.huawei.sharedrive.app.openapi.domain.user;

import java.io.Serializable;

/**
 * 用户配置对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-3-31
 * @see
 * @since
 */
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
