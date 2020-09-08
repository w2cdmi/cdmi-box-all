package com.huawei.sharedrive.uam.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public final class PropertiesUtils
{
    private static final String RESOURCE_NAME = "application.properties";
    
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
    
    private static Properties prop = null;
    
    private PropertiesUtils()
    {
        
    }
    
    /**
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperty(String key, String defaultValue)
    {
        if (prop == null)
        {
            loadProperties();
        }
        return prop.getProperty(key, defaultValue);
    }
    
    /**
     * 
     * @param key
     * @return
     */
    public static String getProperty(String key)
    {
        if (prop == null)
        {
            loadProperties();
        }
        return prop.getProperty(key);
    }
    
    /**
     * 
     * @param key
     * @return
     */
    public static String getServiceUrl()
    {
        String serviceURL = getProperty("serviceURL");
        if (serviceURL != null)
        {
            if (!"/".equals(serviceURL.substring(serviceURL.length() - 1)))
            {
                return serviceURL + '/';
            }
        }
        return serviceURL;
    }
    
    private static synchronized void loadProperties()
    {
        if (prop != null)
        {
            return;
        }
        try
        {
            prop = PropertiesLoaderUtils.loadAllProperties(RESOURCE_NAME);
        }
        catch (IOException e)
        {
            logger.error("Fail in load properties", e);
        }
    }
}
