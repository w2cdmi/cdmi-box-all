/**
 * 
 */
package com.huawei.sharedrive.isystem.user.shiro;

import org.apache.curator.framework.CuratorFramework;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.zk.ZookeeperServer;

/**
 * @author s00108907
 * 
 */
public class ZKShiroCacheManager implements CacheManager
{
    private static Logger logger = LoggerFactory.getLogger(ZKShiroCacheManager.class);
    
    private String shiroSessionCacheZKPath = "/ISYSTEM-SHIROSESSIONCACHE";
    
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework zkClient;
    
    public void init()
    {
        try
        {
            zkClient = zookeeperServer.getClient();
            Stat stat = zkClient.checkExists().forPath(shiroSessionCacheZKPath);
            if (stat == null)
            {
                zkClient.create().forPath(shiroSessionCacheZKPath);
            }
        }
        catch (Exception e)
        {
            logger.error("init ZKShiroCacheManager fail!", e);
        }
    }
    
    public void setZookeeperServer(ZookeeperServer zookeeperServer)
    {
        this.zookeeperServer = zookeeperServer;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.shiro.cache.CacheManager#getCache(java.lang.String)
     */
    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) throws CacheException
    {
        return new ZKShiroCache<K, V>(zkClient, shiroSessionCacheZKPath);
    }
    
    public void setShiroSessionCacheZKPath(String shiroSessionCacheZKPath)
    {
        this.shiroSessionCacheZKPath = shiroSessionCacheZKPath;
    }
}
