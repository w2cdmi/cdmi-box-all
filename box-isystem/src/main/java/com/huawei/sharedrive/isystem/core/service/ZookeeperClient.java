/**
 * 
 */
package com.huawei.sharedrive.isystem.core.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.huawei.sharedrive.isystem.util.PropertiesUtils;

/**
 * @author s00108907
 * 
 */
public final class ZookeeperClient
{
    private static volatile CuratorFramework zkClient;
    
    private ZookeeperClient()
    {
        
    }
    
    /**
     * 获取Zookeeper连接client对象
     * 
     * @return
     */
    public static CuratorFramework getClient()
    {
        if (zkClient == null)
        {
            loadClient();
        }
        return zkClient;
    }
    
    /**
     * 初始化加载Zookeeper连接client对象
     */
    private static synchronized void loadClient()
    {
        if (zkClient == null)
        {
            String connectionString = PropertiesUtils.getProperty("zookeeper.server");
            if (connectionString != null)
            {
                int baseSleepTimeMs = Integer.parseInt(PropertiesUtils.getProperty("zookeeper.retryPolicy.baseSleepTimeMs",
                    "1000"));
                int maxRetries = Integer.parseInt(PropertiesUtils.getProperty("zookeeper.retryPolicy.maxRetries",
                    "3"));
                int connectionTimeoutMs = Integer.parseInt(PropertiesUtils.getProperty("zookeeper.connectionTimeoutMs",
                    "15000"));
                int sessionTimeoutMs = Integer.parseInt(PropertiesUtils.getProperty("zookeeper.sessionTimeoutMs",
                    "60000"));
                
                ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
                CuratorFramework newClient = CuratorFrameworkFactory.builder()
                    .connectString(connectionString)
                    .retryPolicy(retryPolicy)
                    .connectionTimeoutMs(connectionTimeoutMs)
                    .sessionTimeoutMs(sessionTimeoutMs)
                    .build();
                newClient.start();
                zkClient = newClient;
            }
        }
    }
}
