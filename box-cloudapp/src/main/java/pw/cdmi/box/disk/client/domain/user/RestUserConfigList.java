package pw.cdmi.box.disk.client.domain.user;

import java.util.List;

public class RestUserConfigList
{
    private List<RestUserConfig> configs;
    
    public RestUserConfigList()
    {
        
    }
    
    public RestUserConfigList(List<RestUserConfig> configs)
    {
        this.configs = configs;
    }

    public List<RestUserConfig> getConfigs()
    {
        return configs;
    }

    public void setConfigs(List<RestUserConfig> configs)
    {
        this.configs = configs;
    }

}
