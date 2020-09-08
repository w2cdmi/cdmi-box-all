package pw.cdmi.box.disk.client.domain.user;

import java.util.List;


public class RestAccountConfigList {

    private List<RestUserConfig> configs;
    
    public RestAccountConfigList()
    {
        
    }
    
    public RestAccountConfigList(List<RestUserConfig> configs)
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
