package pw.cdmi.file.engine.core.thrift.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.thrift.exception.ThriftPoolException;

/**
 * 
 * @author s90006125
 * 
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ThriftClientPool extends ThriftClientPoolConfig
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ThriftClientPool.class);
    
    private Map<Class<? extends ThriftClient>, GenericObjectPool<? extends ThriftClient>> poolMap = new HashMap<Class<? extends ThriftClient>, GenericObjectPool<? extends ThriftClient>>(
        1);
    
    private Map<Class<? extends ThriftClient>, ThriftClientFactory<? extends ThriftClient>> factoryMap = new HashMap<Class<? extends ThriftClient>, ThriftClientFactory<? extends ThriftClient>>(
        1);
    
    /** 连接池大小配置 */
    private GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
    
    /** 所支持的factory */
    private List<ThriftClientFactory<?>> supportFactorys = new ArrayList<ThriftClientFactory<?>>(1);
    
    private boolean useSSL;
    
    /**
     * 初始化连接池
     */
    public void initPool()
    {
        poolConfig.maxIdle = this.getMaxIdle();
        poolConfig.minIdle = this.getMinIdle();
        poolConfig.maxActive = this.getMaxActive();
        poolConfig.maxWait = this.getMaxWait();
        poolConfig.testOnBorrow = this.isTestOnBorrow();
        poolConfig.testOnReturn = this.isTestOnReturn();
        
        if (useSSL)
        {
            for (ThriftClientFactory<?> factory : supportFactorys)
            {
                factory.setPool(this);
                factoryMap.put(factory.supportType(), factory);
            }
        }
        else
        {
            GenericObjectPool pool = null;
            for (ThriftClientFactory<?> factory : supportFactorys)
            {
                factory.setPool(this);
                pool = new GenericObjectPool(factory, this.poolConfig);
                poolMap.put(factory.supportType(), pool);
                
            }
        }
        
    }
    
    public <T extends ThriftClient> T getThriftClient(Class<T> clazz)
    {
        try
        {
            if (useSSL)
            {
                ThriftClientFactory<? extends ThriftClient> factory = this.getFactory(clazz);
                return (T) factory.makeObject();
            }
            GenericObjectPool<? extends ThriftClient> pool = this.getPool(clazz);
            return (T) pool.borrowObject();
        }
        catch (Exception e)
        {
            String message = "Borrow ThriftClient Failed.";
            LOGGER.error(message, e);
            throw new ThriftPoolException(message, e);
        }
    }
    
    public <T extends ThriftClient> void returnObject(T client)
    {
        if (null == client || useSSL)
        {
            return;
        }
        
        GenericObjectPool pool = this.getPool(client.getClass());
        try
        {
            pool.returnObject(client);
        }
        catch (Exception e)
        {
            LOGGER.warn("Return ThriftClient Failed.", e);
        }
    }
    
    public void clear(Class<? extends ThriftClient> clazz)
    {
        if (useSSL)
        {
            return;
        }
        GenericObjectPool<? extends ThriftClient> pool = this.getPool(clazz);
        pool.clear();
    }
    
    public void close(Class<? extends ThriftClient> clazz)
    {
        GenericObjectPool<? extends ThriftClient> pool = this.getPool(clazz);
        try
        {
            pool.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException("fail to close thrift client pool.", e);
        }
    }
    
    /** 注册一个新的连接池 */
    public <T extends ThriftClient> void regiestNewPool(ThriftClientFactory<T> factory)
    {
        GenericObjectPool pool = new GenericObjectPool(factory, this.poolConfig);
        
        this.poolMap.put(factory.supportType(), pool);
        
        this.getSupportFactorys().add(factory);
    }
    
    public List<ThriftClientFactory<?>> getSupportFactorys()
    {
        return supportFactorys;
    }
    
    public void setSupportFactorys(List<ThriftClientFactory<?>> supportFactorys)
    {
        this.supportFactorys = supportFactorys;
    }
    
    public GenericObjectPool.Config getPoolConfig()
    {
        return poolConfig;
    }
    
    public void setPoolConfig(GenericObjectPool.Config poolConfig)
    {
        this.poolConfig = poolConfig;
    }
    
    public void destroy()
    {
        for (GenericObjectPool pool : poolMap.values())
        {
            tryClose(pool);
        }
    }
    
    private void tryClose(GenericObjectPool pool)
    {
        try
        {
            pool.close();
        }
        catch (Exception e)
        {
            LOGGER.warn("Destory ThriftClientPool Failed.", e);
        }
    }
    
    public void setUseSSL(boolean useSSL)
    {
        this.useSSL = useSSL;
    }
    
    private GenericObjectPool<? extends ThriftClient> getPool(Class<? extends ThriftClient> clazz)
    {
        GenericObjectPool<? extends ThriftClient> pool = this.poolMap.get(clazz);
        
        if (null == pool)
        {
            throw new NoSuchElementException("UnSupport ThriftClient [ " + clazz.getCanonicalName() + " ]");
        }
        
        return pool;
    }
    
    private ThriftClientFactory<? extends ThriftClient> getFactory(Class<? extends ThriftClient> clazz)
    {
        ThriftClientFactory<? extends ThriftClient> factory = this.factoryMap.get(clazz);
        if (null == factory)
        {
            throw new NoSuchElementException("UnSupport ThriftClient [ " + clazz.getCanonicalName() + " ]");
        }
        
        return factory;
    }
}
