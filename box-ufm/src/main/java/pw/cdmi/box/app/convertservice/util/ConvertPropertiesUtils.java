/**
 * 
 */
package pw.cdmi.box.app.convertservice.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * @author s00108907
 * 
 */
public final class ConvertPropertiesUtils
{
    private static Logger logger = LoggerFactory.getLogger(ConvertPropertiesUtils.class);
    
    private static Properties prop = null;
    
    private static Properties convertProp = null;
    
    private static final String RESOURCE_NAME = "application.properties";
    
    private static final String RESOURCE_CONVERT_NAME = "convert.properties";
    
    public enum BundleName
    {
        APPLICATION, CONVERT;
    }
    
    private ConvertPropertiesUtils()
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
        if(null == bundleName || bundleName == BundleName.APPLICATION)
        {
            return prop.getProperty(key, defaultValue);
        }
        if (convertProp == null)
        {
            loadProperties();
        }
        return convertProp.getProperty(key, defaultValue);
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
                logger.error("Fail in load properties", e);
            }
        }
        if (convertProp == null)
        {
            try
            {
            	convertProp = PropertiesLoaderUtils.loadAllProperties(RESOURCE_CONVERT_NAME);
            }
            catch (IOException e)
            {
                logger.error("Fail in load properties convertProp", e);
            }
        }
        
    }
}
