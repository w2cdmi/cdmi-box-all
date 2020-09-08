package pw.cdmi.box.uam.adminlog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.adminlog.dao.UserLogConfigDao;
import pw.cdmi.box.uam.adminlog.service.UserLogConfigService;
import pw.cdmi.common.domain.SystemConfig;

@Service("userlogConfigService")
public class UserLogConfigServiceImpl implements UserLogConfigService
{
    @Autowired
    private UserLogConfigDao userLogConfigDao;
    
    @Override
    public void saveConfig(SystemConfig systemConfig)
    {
        userLogConfigDao.saveConfig(systemConfig);
    }
    
    @Override
    public void updateConfig(SystemConfig systemConfig)
    {
        userLogConfigDao.update(systemConfig);
    }
    
    @Override
    public SystemConfig queryConfig(String appId, String id)
    {
        return userLogConfigDao.getByPriKey(appId, id);
    }
    
}
