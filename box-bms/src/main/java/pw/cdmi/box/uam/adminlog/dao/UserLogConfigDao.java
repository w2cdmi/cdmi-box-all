package pw.cdmi.box.uam.adminlog.dao;

import pw.cdmi.common.domain.SystemConfig;

public interface UserLogConfigDao
{
    
    SystemConfig getByPriKey(String appId, String id);
    
    void saveConfig(SystemConfig systemConfig);
    
    void update(SystemConfig systemConfig);
}
