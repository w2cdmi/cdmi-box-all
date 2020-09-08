package com.huawei.sharedrive.init.develop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class PropertiesReplaceTools
{
    
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
                if(StringUtils.trimToEmpty(tempLine).startsWith("jdbc.userdb.driverClassName"))
                {
                    String userdbBase = "jdbc.userdb_1.url=jdbc\\:mysql://<userdbIp>\\:3306/userdb_0?useUnicode\\=true&characterEncoding\\=UTF-8&autoReconnect\\=true&socketTimeout\\=1800000";
                    String userDbUrl = userdbBase.replaceAll("<userdbIp>", LocalConfig.getMySQLIp());
                    sb.append(userDbUrl);
                    sb.append(System.lineSeparator());
                    sb.append(tempLine);
                }
                else if(StringUtils.trimToEmpty(tempLine).startsWith("jdbc.sysdb.url"))
                {
                    String value = tempLine.replace("<sysdb_ip>", LocalConfig.getMySQLIp()).toString();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("jdbc.logdb.url"))
                {
                    String value = tempLine.replace("<logdb_ip>", LocalConfig.getMySQLIp()).toString();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("uam.client.valid.server.cert"))
                {
                    String value = "uam.client.valid.server.cert=false";
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("uam.client.require.client.cert"))
                {
                    String value = "uam.client.require.client.cert=false";
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("thrift.dataserver.report.addr"))
                {
                    String value = "thrift.dataserver.report.addr=" + LocalConfig.getLocalIp();
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
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("self.privateAddr"))
                {
                    String value = "self.privateAddr=" + LocalConfig.getLocalIp();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("self.manageAddr"))
                {
                    String value = "self.manageAddr=" + LocalConfig.getLocalIp();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("self.serviceAddr"))
                {
                    String value = "self.serviceAddr=" + LocalConfig.getLocalIp();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("system.environment.privategw"))
                {
                    String value = "system.environment.privategw=" + LocalConfig.getLocalIp();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("system.environment.publicgw"))
                {
                    String value = "system.environment.publicgw=" + LocalConfig.getLocalIp();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("system.network.check.address"))
                {
                    String value = "system.network.check.address=" + LocalConfig.getLocalIp();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("cache.default.max.connections"))
                {
                    String value = "cache.default.max.connections=2";
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("alarm.ism.ip"))
                {
                    String value = "alarm.ism.ip=" + LocalConfig.getLocalIp();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("alarm.support"))
                {
                    String value = "alarm.support=false";
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("cache.default.server.ips"))
                {
                    String value = "cache.default.server.ips=" + LocalConfig.getMemcachedIp();
                    sb.append(value);
                }
                else  if(StringUtils.trimToEmpty(tempLine).startsWith("activemq.broker.url"))
                {
                    String value = "activemq.broker.url=" + LocalConfig.getActiveMQ();
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
