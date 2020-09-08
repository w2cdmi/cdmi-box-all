package com.huawei.sharedrive.app.openapi.domain.account;

public class RestAccountConfig {

    
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
