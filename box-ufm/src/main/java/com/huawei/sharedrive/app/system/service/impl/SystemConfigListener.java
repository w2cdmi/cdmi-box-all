package com.huawei.sharedrive.app.system.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;

import pw.cdmi.common.config.service.ConfigListener;

@Component("systemConfigListener")
public class SystemConfigListener implements ConfigListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfigListener.class);
    
    public static final String SYSTEM_CONFIG_CHANGE = "system_config_change";
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (SYSTEM_CONFIG_CHANGE.equals(key))
        {
            LOGGER.info("System config changed. Key :" + value);
            systemConfigDAO.invalidateCache(value);
        }
    }
    
}
