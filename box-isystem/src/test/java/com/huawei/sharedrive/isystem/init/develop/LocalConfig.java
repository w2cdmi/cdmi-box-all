package com.huawei.sharedrive.isystem.init.develop;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

public class LocalConfig
{
    
    private static final String DEV_RESOURCE_NAME = "localDevelopConfig.properties";
    
    
    private static volatile Properties prop = null;
    
    public static String getDevResourcePath() throws IOException
    {
        return getDevProjectPath() + "src/main/resource/";
    }
    
    public static String getMySQLIp() throws IOException
    {
        loadProperties();
        return prop.getProperty("local.dbip");
    }
    
    public static String getMemcachedIp() throws IOException
    {
        loadProperties();
        return prop.getProperty("local.memcached.ip");
    }
    
    public static String getZkIp() throws IOException
    {
        loadProperties();
        return prop.getProperty("local.zookeeper.ip");
    }
    
    public static String getLocalIp() throws IOException
    {
        loadProperties();
        return prop.getProperty("local.ip");
    }
    
    public static String getThriftServerSslEnable() throws IOException
    {
        loadProperties();
        return prop.getProperty("local.thrift.server.ssl.use");
    }
    
    
    private static String getDevProjectPath() throws IOException
    {
        loadProperties();
        String path = prop.getProperty("local.projectpath");
        if(path.endsWith("/"))
        {
            return path;
        }
        return path + "/";
    }
    
    /**
     * 加载配置文件内容
     * @throws IOException 
     */
    private static synchronized void loadProperties() throws IOException
    {
        if (prop != null)
        {
            return;
        }
        prop = PropertiesLoaderUtils.loadAllProperties(DEV_RESOURCE_NAME);
    }
    
}
