package com.huawei.sharedrive.isystem.sysconfig.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.sysconfig.service.SystemConfigService;
import com.huawei.sharedrive.isystem.system.dao.SystemConfigDAO;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.SystemConfig;

@Service
public class SystemConfigServiceImpl implements SystemConfigService
{
    
    private static final String SYSTEM_CONFIG_CHANGE = "system_config_change";
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Override
    public SystemConfig getSystemConfig(String key)
    {
        return systemConfigDAO.get(key);
    }
    
    @Override
    public void setSystemConfig(String key, String value)
    {
        saveConfigToDb(key, value);
        configManager.setConfig(SYSTEM_CONFIG_CHANGE, key);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void saveConfigToDb(String key, String value)
    {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setId(key);
        systemConfig.setValue(value);
        systemConfigDAO.update(systemConfig);
    }
}
