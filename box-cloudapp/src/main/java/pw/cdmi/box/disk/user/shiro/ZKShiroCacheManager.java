package pw.cdmi.box.disk.user.shiro;

import org.apache.curator.framework.CuratorFramework;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.utils.ZookeeperUtil;
import pw.cdmi.core.zk.ZookeeperServer;

public class ZKShiroCacheManager implements CacheManager
{
    private static Logger logger = LoggerFactory.getLogger(ZKShiroCacheManager.class);
    
    private String shiroSessionCacheZKPath = "/SHIROSESSIONCACHE";
    
    private CuratorFramework zkClient;
    
    private ZookeeperServer zookeeperServer;
    
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
    
    public void init()
    {
        try
        {
            zkClient = zookeeperServer.getClient();
            ZookeeperUtil.safeCreateNode(zkClient, shiroSessionCacheZKPath, new byte[0]);
        }
        catch (Exception e)
        {
            logger.error("init ZKShiroCacheManager fail!", e);
        }
    }
    
    public void setShiroSessionCacheZKPath(String shiroSessionCacheZKPath)
    {
        this.shiroSessionCacheZKPath = shiroSessionCacheZKPath;
    }
    
    public void setZookeeperServer(ZookeeperServer zookeeperServer)
    {
        this.zookeeperServer = zookeeperServer;
    }
}
