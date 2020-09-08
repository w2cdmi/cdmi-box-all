package com.huawei.sharedrive.isystem.init.develop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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
        replaceApplicationProps();
        replaceJobProps();
    }
    
    private void replaceApplicationProps() throws Exception
    {
        StringBuilder sb = new StringBuilder();
        File appCxtFile = new File(LocalConfig.getDevResourcePath() + "application.properties");
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(appCxtFile));
            String tempLine;
            while((tempLine = reader.readLine()) != null)
            {
                if(StringUtils.trimToEmpty(tempLine).startsWith("jdbc.url"))
                {
                    String value = tempLine.replace("<sysdb_ip>", LocalConfig.getMySQLIp()).toString();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("jdbc.monitor.url"))
                {
                    String value = tempLine.replace("<monitordb_ip>", LocalConfig.getMySQLIp()).toString();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("jdbc.logdb.url"))
                {
                    String value = tempLine.replace("<logdb_ip>", LocalConfig.getMySQLIp()).toString();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("thrift.app.ip"))
                {
                    String value = "thrift.app.ip=" + LocalConfig.getLocalIp();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("thrift.use.ssl"))
                {
                    String value = "thrift.use.ssl=" + LocalConfig.getThriftServerSslEnable();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("zookeeper.server"))
                {
                    String value = "zookeeper.server=" + LocalConfig.getZkIp();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("cache.default.server.ips"))
                {
                    String value = "cache.default.server.ips=" + LocalConfig.getMemcachedIp();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("thrift.monitor.server.ip"))
                {
                    String value = "thrift.monitor.server.ip=" + LocalConfig.getLocalIp();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("thrift.monitor.use.ssl"))
                {
                    String value = "thrift.monitor.use.ssl=" + LocalConfig.getThriftServerSslEnable();
                    sb.append(value);
                }
                else
                {
                    sb.append(tempLine);
                }
                sb.append(System.lineSeparator());
            }
        }
        finally
        {
            IOUtils.closeQuietly(reader);
        }
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(appCxtFile);
            IOUtils.write(sb.toString(), out);
        }
        finally
        {
            IOUtils.closeQuietly(out);
        }
    }
    
    
    private void replaceJobProps() throws Exception
    {
        StringBuilder sb = new StringBuilder();
        File appCxtFile = new File(LocalConfig.getDevResourcePath() + "job.properties");
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(appCxtFile));
            String tempLine;
            while((tempLine = reader.readLine()) != null)
            {
                if(StringUtils.trimToEmpty(tempLine).startsWith("cluster.org.quartz.dataSource.URL"))
                {
                    String value = tempLine.replace("<jobdb_ip>", LocalConfig.getMySQLIp()).toString();
                    sb.append(value);
                }
                else
                {
                    sb.append(tempLine);
                }
                sb.append(System.lineSeparator());
            }
        }
        finally
        {
            IOUtils.closeQuietly(reader);
        }
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(appCxtFile);
            IOUtils.write(sb.toString(), out);
        }
        finally
        {
            IOUtils.closeQuietly(out);
        }
    }
    
    
}
