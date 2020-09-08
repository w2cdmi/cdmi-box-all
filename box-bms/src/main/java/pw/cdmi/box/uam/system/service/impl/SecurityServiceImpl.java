package pw.cdmi.box.uam.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.system.dao.SystemConfigDAO;
import pw.cdmi.box.uam.system.service.SecurityService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.uam.domain.AuthApp;

@Component
public class SecurityServiceImpl implements SecurityService
{
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Override
    public SecurityConfig getSecurityConfig(String appId)
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(appId, null, SecurityConfig.CONFIG_PREFIX);
        return SecurityConfig.buildPojo(itemList);
    }
    
    @Override
    public void saveSecurityConfig(SecurityConfig securityConfig)
    {
        saveSecurityInTransaction(securityConfig);
        AuthApp authApp = authAppService.getByAuthAppID(securityConfig.getAppId());
        if (authApp.getType() == Constants.DEFAULT_WEB_APP_TYPE)
        {
            configManager.setConfig(SecurityConfig.class.getSimpleName(), securityConfig);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSecurityInTransaction(SecurityConfig securityConfig)
    {
        List<SystemConfig> itemList = securityConfig.toConfigItem();
        for (SystemConfig systemConfig : itemList)
        {
            if (systemConfigDAO.getByPriKey(securityConfig.getAppId(), systemConfig.getId()) == null)
            {
                systemConfigDAO.create(systemConfig);
            }
            else
            {
                systemConfigDAO.update(systemConfig);
            }
        }
    }
    
}
