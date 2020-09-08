package com.huawei.sharedrive.app.openapi.domain.account;

import java.util.List;


public class RestAccountConfigList {
    private List<RestAccountConfig> attributes;
    
    public RestAccountConfigList()
    {
        
    }
    
    public RestAccountConfigList(List<RestAccountConfig> attributes)
    {
        this.attributes = attributes;
    }
    
    public List<RestAccountConfig> getAttributes()
    {
        return attributes;
    }
    
    public void setAttributes(List<RestAccountConfig> attributes)
    {
        this.attributes = attributes;
    }
}
