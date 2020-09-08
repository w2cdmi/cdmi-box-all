package pw.cdmi.box.uam.user.shiro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

public class ZKShiroCache<K, V> implements Cache<K, V>
{
    private static Logger logger = LoggerFactory.getLogger(ZKShiroCache.class);
    
    private CuratorFramework zkClient;
    
    private String shiroSessionCacheZKPath;
    
    public ZKShiroCache(CuratorFramework zkClient, String shiroSessionCacheZKPath)
    {
        this.zkClient = zkClient;
        this.shiroSessionCacheZKPath = shiroSessionCacheZKPath;
    }
    
    @Override
    public void clear() throws CacheException
    {
        try
        {
            zkClient.delete().forPath(shiroSessionCacheZKPath);
        }
        catch (Exception e)
        {
            logger.error("Clear failed", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public V get(K key) throws CacheException
    {
        String path = getPath(key);
        try
        {
            byte[] byteValue = zkClient.getData().forPath(path);
            if (byteValue.length > 0)
            {
                return (V) SerializationUtils.deserialize(byteValue);
            }
        }
        catch (Exception e)
        {
            logger.warn("cache not found");
        }
        return null;
    }
    
    @Override
    public V put(K key, V value) throws CacheException
    {
        V previous = get(key);
        String path = getPath(key);
        try
        {
            zkClient.create()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, SerializationUtils.serialize(value));
        }
        catch (Exception e)
        {
            logger.error("put error!", e);
        }
        return previous;
    }
    
    @Override
    public V remove(K key) throws CacheException
    {
        String path = getPath(key);
        V previous = get(key);
        try
        {
            zkClient.delete().forPath(path);
        }
        catch (Exception e)
        {
            logger.error("remove error!", e);
        }
        return previous;
    }
    
    @Override
    public int size()
    {
        try
        {
            return zkClient.getChildren().forPath(shiroSessionCacheZKPath).size();
        }
        catch (Exception e)
        {
            logger.error("get size error!", e);
        }
        return 0;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys()
    {
        try
        {
            List<String> cl = zkClient.getChildren().forPath(shiroSessionCacheZKPath);
            Set<String> keys = new HashSet<String>(16);
            keys.addAll(cl);
            return (Set<K>) keys;
        }
        catch (Exception e)
        {
            logger.error("get keys error!", e);
        }
        return Collections.emptySet();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values()
    {
        try
        {
            Collection<V> cs = new ArrayList<V>(10);
            List<String> cl = zkClient.getChildren().forPath(shiroSessionCacheZKPath);
            byte[] byteValue;
            for (String path : cl)
            {
                byteValue = zkClient.getData().forPath(path);
                cs.add((V) SerializationUtils.deserialize(byteValue));
            }
            return cs;
        }
        catch (Exception e)
        {
            logger.error("get values error!", e);
        }
        return Collections.emptyList();
    }
    
    /**
     * 
     * @param key
     * @return
     */
    private String getPath(K key)
    {
        return shiroSessionCacheZKPath + '/' + key;
    }
    
}
