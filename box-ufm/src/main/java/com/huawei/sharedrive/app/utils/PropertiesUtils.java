/**
 * 
 */
package com.huawei.sharedrive.app.utils;

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
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
    
    private static Properties prop = null;
    
    private static Properties bridgeProp = null;
    
    private static Properties hwitProp = null;
    
    private static final String RESOURCE_NAME = "application.properties";
    
    private static final String RESOURCE_BRIDGE_NAME = "bridge.properties";
    
    private static final String RESOURCE_HWIT_NAME = "hwit.properties";
    
    public enum BundleName
    {
        APPLICATION, BRIDGE,HWIT;
    }
    
    private PropertiesUtils()
    {
        
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
     * 获取配置参数
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperty(String key, String defaultValue, BundleName bundleName)
    {
        if(bundleName == BundleName.BRIDGE)
        {
            if (bridgeProp == null)
            {
                loadProperties();
            }
            return bridgeProp.getProperty(key, defaultValue);
        }
        if(bundleName == BundleName.HWIT)
        {
            if (hwitProp == null)
            {
                loadProperties();
            }
            return hwitProp.getProperty(key, defaultValue);
        }
        return prop.getProperty(key, defaultValue);
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
     * 加载配置文件内容
     */
    private static synchronized void loadProperties()
    {
        if (prop == null)
        {
            try
            {
                prop = PropertiesLoaderUtils.loadAllProperties(RESOURCE_NAME);
            }
            catch (IOException e)
            {
                logger.error("Fail in load application properties", e);
            }
        }
        if (bridgeProp == null)
        {
            try
            {
                bridgeProp = PropertiesLoaderUtils.loadAllProperties(RESOURCE_BRIDGE_NAME);
            }
            catch (IOException e)
            {
                logger.error("Fail in load properties bridge properties", e);
            }
        }
        if (hwitProp == null)
        {
            try
            {
                hwitProp = PropertiesLoaderUtils.loadAllProperties(RESOURCE_HWIT_NAME);
            }
            catch (IOException e)
            {
                logger.error("Fail in load hwit properties", e);
            }
        }
        
        
    }
}
