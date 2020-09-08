/**
 * 
 */
package com.huawei.sharedrive.app.system.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.system.service.SecurityService;

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
    private static Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);
    
    @Autowired
    private ConfigManager configManager;
    
    private SecurityConfig localCache;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    private static final String DEFAULT_HTTP_PORT = "80";
    
    private static final String DEFAULT_HTTPS_PORT = "443";
    
    private static final String HTTP_PORT = "8080";
    
    private static final String HTTPS_PORT = "8443";
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(SecurityConfig.class.getSimpleName()))
        {
            logger.info("Reload SecurityConfig By Cluster Notify.");
            localCache = (SecurityConfig) value;
        }
    }
    
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
    public String changePort(String currentPort, String protocolType)
    {
        if (StringUtils.isBlank(currentPort) || currentPort.equals(DEFAULT_HTTPS_PORT)
            || currentPort.equals(DEFAULT_HTTP_PORT))
        {
            return "";
        }
        if ("http".equalsIgnoreCase(protocolType))
        {
            if (currentPort.equals(HTTPS_PORT))
            {
                return ':' + HTTP_PORT;
            }
            return ':' + currentPort;
        }
        if ("https".equalsIgnoreCase(protocolType))
        {
            if (currentPort.equals(HTTP_PORT))
            {
                return ':' + HTTPS_PORT;
            }
            return ':' + currentPort;
        }
        return currentPort;
    }
}
