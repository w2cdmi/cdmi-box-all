package com.huawei.sharedrive.bms.init.develop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class PropertiesReplaceTools
{
    
    private static final String APP_RES_NAME = "application.properties";
    
    private static final String JOB_RES_NAME = "job.properties";
    
    public static void writeProperteis(String resourceFileName, Properties prop) throws Exception
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(new File(LocalConfig.getDevResourcePath() + resourceFileName));
            prop.store(fos, "the primary key of article table");
        }
        finally
        {
            IOUtils.closeQuietly(fos);
        }
    }
    
    @Test
    public void testReplace() throws Exception
    {
        Properties prop = PropertiesLoaderUtils.loadAllProperties(APP_RES_NAME);
        replaceAppLocalConfig(prop);
        PropertiesReplaceTools.writeProperteis(APP_RES_NAME, prop);
        
        Properties jobProps = PropertiesLoaderUtils.loadAllProperties(JOB_RES_NAME);
        replaceJobLocalConfig(jobProps);
        PropertiesReplaceTools.writeProperteis(JOB_RES_NAME, jobProps);
    }

    private void replaceAppLocalConfig(Properties prop) throws IOException
    {
        String jdbcUrlValue = prop.getProperty("jdbc.uamdb.url");
        jdbcUrlValue = jdbcUrlValue.replaceAll("<sysdb_ip>", LocalConfig.getMySQLIp());
        prop.setProperty("jdbc.uamdb.url", jdbcUrlValue);
        
        String monitorUrlValue = prop.getProperty("jdbc.userlogdb.url");
        monitorUrlValue = monitorUrlValue.replaceAll("<userlogdb_ip>", LocalConfig.getMySQLIp());
        prop.setProperty("jdbc.userlogdb.url", monitorUrlValue);
        
        prop.setProperty("zookeeper.server", LocalConfig.getZkIp());
        prop.setProperty("cache.default.server.ips", LocalConfig.getMemcachedIp());
        prop.setProperty("alarm.support", "false");
        prop.setProperty("ufm.client.valid.server.cert", "false");
        
    }
    
    private void replaceJobLocalConfig(Properties prop) throws IOException
    {
        String jdbcUrlValue = prop.getProperty("cluster.org.quartz.dataSource.URL");
        jdbcUrlValue = jdbcUrlValue.replaceAll("<jobdb_ip>", LocalConfig.getMySQLIp());
        prop.setProperty("cluster.org.quartz.dataSource.URL", jdbcUrlValue);
    }
}
