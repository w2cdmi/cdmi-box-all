/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.core.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.utils.BaseConvertUtils;

/**
 * 系统属性容器
 * 
 * @author s90006125
 * 
 */
public final class SystemConfigContainer
{
    private SystemConfigContainer()
    {
    }
    
    /** 保存所有系统属性 */
    private static final Map<String, SystemConfig> ALL_PROPERTIES = new HashMap<String, SystemConfig>(1);
    
    /** 保存所有系统属性解析后的值 */
    private static final Map<String, Object> ALL_PROPERTIES_VALUES = new HashMap<String, Object>(1);
    
    public static Boolean getBoolean(String key, Boolean defaultValue)
    {
        Object value = getFromCache(key);
        if (null != value && value instanceof Boolean)
        {
            return (Boolean) value;
        }
        
        value = BaseConvertUtils.toBoolean(getConfigValue(key), defaultValue);
        
        updateCacheValue(key, value);
        
        return (Boolean) value;
    }
    
    public synchronized static SystemConfig getConfig(String key)
    {
        return ALL_PROPERTIES.get(key);
    }
    
    public static String getConfigValue(String key)
    {
        SystemConfig property = getConfig(key);
        if (null == property)
        {
            return null;
        }
        
        return property.getValue();
    }
    
    public static String[] getConfigValues(String key)
    {
        return getConfigValues(key, ";");
    }
    
    public static String[] getConfigValues(String key, String split)
    {
        String str = getConfigValue(key);
        if (StringUtils.isBlank(str))
        {
            return new String[0];
        }
        
        return str.split(split);
    }
    
    public static Integer getInteger(String key, Integer defaultValue)
    {
        Object value = getFromCache(key);
        if (null != value && value instanceof Integer)
        {
            return (Integer) value;
        }
        
        value = BaseConvertUtils.toInt(getConfigValue(key), defaultValue);
        
        updateCacheValue(key, value);
        
        return (Integer) value;
    }
    
    public static Long getLong(String key, Long defaultValue)
    {
        Object value = getFromCache(key);
        if (null != value && value instanceof Long)
        {
            return (Long) value;
        }
        
        value = BaseConvertUtils.toLong(getConfigValue(key), defaultValue);
        
        updateCacheValue(key, value);
        
        return (Long) value;
    }
    
    public static String getString(String key, String defaultValue)
    {
        String str = getConfigValue(key);
        if (StringUtils.isBlank(str))
        {
            return defaultValue;
        }
        
        return str;
    }
    
    /**
     * 注册新的属性
     * 
     * @param property
     */
    public synchronized static void regiestConfig(SystemConfig... configs)
    {
        if (null == configs)
        {
            return;
        }
        
        for (SystemConfig property : configs)
        {
            ALL_PROPERTIES.put(property.getKey(), property);
            ALL_PROPERTIES_VALUES.remove(property.getKey());
        }
    }
    
    /**
     * 注册新的属性
     * 
     * @param property
     */
    public synchronized static void regiestConfig(List<SystemConfig> configsList)
    {
        if (null == configsList)
        {
            return;
        }
        
        for (SystemConfig property : configsList)
        {
            ALL_PROPERTIES.put(property.getKey(), property);
            ALL_PROPERTIES_VALUES.remove(property.getKey());
        }
    }
    
    private synchronized static Object getFromCache(String key)
    {
        return ALL_PROPERTIES_VALUES.get(key);
    }
    
    private static synchronized Object updateCacheValue(String key, Object value)
    {
        return ALL_PROPERTIES_VALUES.put(key, value);
    }
}
