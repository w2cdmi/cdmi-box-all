package com.huawei.sharedrive.app.openapi.domain.user;

import java.util.List;

public class RestUserConfigList
{
    private List<RestUserConfig> attributes;
    
    public RestUserConfigList()
    {
        
    }
    
    public RestUserConfigList(List<RestUserConfig> attributes)
    {
        this.attributes = attributes;
    }
    
    public List<RestUserConfig> getAttributes()
    {
        return attributes;
    }
    
    public void setAttributes(List<RestUserConfig> attributes)
    {
        this.attributes = attributes;
    }
    
}
