/**
 * 
 */
package com.huawei.sharedrive.isystem.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.isystem.system.service.SecurityService;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.common.domain.SystemConfig;

/**
 * @author s00108907
 * 
 */
@Component
public class SecurityServiceImpl implements SecurityService, ConfigListener
{
    
    private SecurityConfig localCache;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    @Override
    public SecurityConfig getSecurityConfig()
    {
        if (localCache == null)
        {
            List<SystemConfig> itemList = systemConfigDAO.getByPrefix(null, "securityConfig");
            localCache = SecurityConfig.buildPojo(itemList);
            if (localCache != null)
            {
                configManager.setConfig(SecurityConfig.class.getSimpleName(), localCache);
            }
        }
        return localCache;
    }
    
    @Override    
    public void saveSecurityConfig(SecurityConfig securityConfig)
    {
        saveSecurityConfigInNewTrans(securityConfig);
        configManager.setConfig(SecurityConfig.class.getSimpleName(), securityConfig);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSecurityConfigInNewTrans(SecurityConfig securityConfig)
    {
        List<SystemConfig> itemList = securityConfig.toConfigItem();
        for (SystemConfig systemConfig : itemList)
        {
            if (systemConfigDAO.get(systemConfig.getId()) == null)
            {
                systemConfigDAO.create(systemConfig);
            }
            else
            {
                systemConfigDAO.update(systemConfig);
            }
        }
        localCache = securityConfig;
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(SecurityConfig.class.getSimpleName()))
        {
            localCache = (SecurityConfig) value;
        }
    }
    
}
