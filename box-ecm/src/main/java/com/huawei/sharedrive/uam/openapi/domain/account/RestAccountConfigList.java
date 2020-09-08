package com.huawei.sharedrive.uam.openapi.domain.account;

import java.util.List;

public class RestAccountConfigList
{
    private List<RestAccountConfig> configs;
    
    public RestAccountConfigList()
    {
        
    }
    
    public RestAccountConfigList(List<RestAccountConfig> configs)
    {
        this.configs = configs;
    }
    
    public List<RestAccountConfig> getConfigs()
    {
        return configs;
    }
    
    public void setConfigs(List<RestAccountConfig> configs)
    {
        this.configs = configs;
    }
}
