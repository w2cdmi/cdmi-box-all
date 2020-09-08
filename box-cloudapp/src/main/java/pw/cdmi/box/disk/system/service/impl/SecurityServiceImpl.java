package pw.cdmi.box.disk.system.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.system.dao.SystemConfigDAO;
import pw.cdmi.box.disk.system.service.SecurityService;
import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.common.domain.SystemConfig;

@Component
public class SecurityServiceImpl implements SecurityService, ConfigListener
{
    private static Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);
    
    private static SecurityConfig localCache;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private AuthAppService authAppService;
    
    private static final String DEFAULT_HTTP_PORT = "80";
    
    private static final String DEFAULT_HTTPS_PORT = "443";
    
    private static final String HTTP_PORT = "8080";
    
    private static final String HTTPS_PORT = "8443";
    
    private static void initSecurityConfig(SecurityConfig value)
    {
        synchronized (SecurityServiceImpl.class)
        {
            localCache = value;
        }
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(SecurityConfig.class.getSimpleName()))
        {
            logger.info("Reload SecurityConfig By Cluster Notify.");
            SecurityServiceImpl.initSecurityConfig((SecurityConfig) value);
        }
    }
    
    @Override
    public SecurityConfig getSecurityConfig()
    {
        if (localCache == null)
        {
            List<SystemConfig> itemList = systemConfigDAO.getByPrefix(authAppService.getCurrentAppId(),
                null,
                SecurityConfig.CONFIG_PREFIX);
            SecurityServiceImpl.initSecurityConfig(SecurityConfig.buildPojo(itemList));
        }
        return localCache;
    }
    
    @Override
    public String changePort(String currentPort, String protocolType)
    {
        String defaultHttpPort = PropertiesUtils.getProperty("defaultHttpPort");
        if (StringUtils.isBlank(defaultHttpPort))
        {
            defaultHttpPort = DEFAULT_HTTP_PORT;
        }
        String defaultHttpsPort = PropertiesUtils.getProperty("defaultHttpsPort");
        if (StringUtils.isBlank(defaultHttpsPort))
        {
            defaultHttpsPort = DEFAULT_HTTPS_PORT;
        }
        String httpPort = PropertiesUtils.getProperty("httpPort");
        if (StringUtils.isBlank(httpPort))
        {
            httpPort = HTTP_PORT;
        }
        String httpsPort = PropertiesUtils.getProperty("httpsPort");
        if (StringUtils.isBlank(httpsPort))
        {
            httpsPort = HTTPS_PORT;
        }
        if (StringUtils.isBlank(currentPort) || currentPort.equals(defaultHttpsPort)
            || currentPort.equals(defaultHttpPort))
        {
            return "";
        }
        if ("http".equalsIgnoreCase(protocolType))
        {
            if (currentPort.equals(httpsPort))
            {
                return ':' + httpPort;
            }
            return ':' + currentPort;
        }
        if ("https".equalsIgnoreCase(protocolType))
        {
            if (currentPort.equals(httpPort))
            {
                return ':' + httpsPort;
            }
            return ':' + currentPort;
        }
        return currentPort;
    }
}
