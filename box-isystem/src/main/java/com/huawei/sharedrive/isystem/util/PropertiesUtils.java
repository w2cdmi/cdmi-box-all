/**
 * 
 */
package com.huawei.sharedrive.isystem.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * @author s00108907
 * 
 */
public final class PropertiesUtils
{
    private static final String RESOURCE_NAME = "application.properties";
    
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
    
    private static volatile Properties prop = null;
    
    private PropertiesUtils()
    {
        
    }
    
    /**
     * 获取配置参数
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
     * 获取配置参数
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
     * 获取服务地址(地址后缀作兼容性处理)
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
    
    /**
     * 加载配置文件内容
     */
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
