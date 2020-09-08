/**
 * 
 */
package com.huawei.sharedrive.app.test.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

/**
 * @author q90003805
 * 
 */
public class ConfigManagerTest
{
    private static final String CONFIG_ROOT = "/config";
    
    @Test
    public void testchangeConfig() throws Exception
    {
        String path = CONFIG_ROOT + "/testConfig";
        String value = "testValue";
        CuratorFramework client = null;
        
        try
        {
            client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(60000)
                .retryPolicy(new RetryNTimes(3, 1000))
                .connectionTimeoutMs(15000)
                .build();
            client.start();
            byte[] data = value.getBytes();
            Stat stat = client.checkExists().forPath(path);
            if (stat == null)
            {
                client.create().forPath(path, data);
            }
            else
            {
                client.setData().forPath(path, data);
            }
        }
        finally
        {
            if (client != null)
            {
                client.close();
            }
        }
    }
}
