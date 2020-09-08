package com.huawei.sharedrive.uam.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.uam.system.service.SecurityService;
import com.huawei.sharedrive.uam.util.Constants;

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
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSecurityConfig(SecurityConfig securityConfig)
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
        AuthApp authApp = authAppService.getByAuthAppID(securityConfig.getAppId());
        if (authApp.getType() == Constants.DEFAULT_WEB_APP_TYPE)
        {
            configManager.setConfig(SecurityConfig.class.getSimpleName(), securityConfig);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSystemConfig(SystemConfig systemConfig)
    {
    	 if (systemConfigDAO.getByPriKey(systemConfig.getAppId(), systemConfig.getId()) == null)
         {
             systemConfigDAO.create(systemConfig);
         }
         else
         {
             systemConfigDAO.update(systemConfig);
         }
        AuthApp authApp = authAppService.getByAuthAppID(systemConfig.getAppId());
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(systemConfig.getAppId(), null, SecurityConfig.CONFIG_PREFIX);
        SecurityConfig securityConfig= SecurityConfig.buildPojo(itemList);
        if (authApp.getType() == Constants.DEFAULT_WEB_APP_TYPE)
        {
            configManager.setConfig(SecurityConfig.class.getSimpleName(), securityConfig);
        }
    }
    
    
}
