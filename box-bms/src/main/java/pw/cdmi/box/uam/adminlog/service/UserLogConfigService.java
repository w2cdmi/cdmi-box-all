package pw.cdmi.box.uam.adminlog.service;

import pw.cdmi.common.domain.SystemConfig;

public interface UserLogConfigService
{
    
    void saveConfig(SystemConfig systemConfig);
    
    void updateConfig(SystemConfig systemConfig);
    
    SystemConfig queryConfig(String appId, String id);
    
}
