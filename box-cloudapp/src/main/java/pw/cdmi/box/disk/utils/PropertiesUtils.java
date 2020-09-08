package pw.cdmi.box.disk.utils;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public final class PropertiesUtils
{
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
    
    private static Properties prop = null;
    
    private static final String RESOURCE_NAME = "application.properties";
    
    private PropertiesUtils()
    {
        
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
