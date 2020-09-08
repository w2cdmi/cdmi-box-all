package com.huawei.sharedrive.uam.security.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.security.domain.SecurityConstants;

import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.config.service.ConfigListener;

@Component("securityMartixCacheManager")
public class SecurityMartixCacheManager implements ConfigListener
{
    private static Logger logger = LoggerFactory.getLogger(DefaultMatrixContext.class);
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    private static final Map<Object, Object> RESOURCES = new HashMap<Object, Object>(10);
    
    /**
     * Returns the ThreadLocal Map. This Map is used internally to bind objects to the
     * current thread by storing each object under a unique key.
     * 
     * @return the map of bound resources
     */
    public static Map<Object, Object> getResources()
    {
        return RESOURCES != null ? new HashMap<Object, Object>(10) : null;
    }
    
    /**
     * Allows a caller to explicitly set the entire resource map. This operation
     * overwrites everything that existed previously in the ThreadContext - if you need to
     * retain what was on the thread prior to calling this method, call the
     * {@link #getResources()} method, which will give you the existing state.
     * 
     * @param newResources the resources to replace the existing {@link #getResources()
     *            resources}.
     * @since 1.0
     */
    public static void setResources(Map<Object, Object> newResources)
    {
        if (CollectionUtils.isEmpty(newResources))
        {
            return;
        }
        RESOURCES.clear();
        RESOURCES.putAll(newResources);
    }
    
    /**
     * Returns the value bound in the {@code ThreadContext} under the specified
     * {@code key}, or {@code null} if there is no value for that {@code key}.
     * 
     * @param key the map key to use to lookup the value
     * @return the value bound in the {@code ThreadContext} under the specified
     *         {@code key}, or {@code null} if there is no value for that {@code key}.
     * @since 1.0
     */
    private static Object getValue(Object key)
    {
        return RESOURCES.get(key);
    }
    
    /**
     * Returns the object for the specified <code>key</code> that is bound to the current
     * thread.
     * 
     * @param key the key that identifies the value to return
     * @return the object keyed by <code>key</code> or <code>null</code> if no value
     *         exists for the specified <code>key</code>
     */
    public static Object get(Object key)
    {
        if (logger.isTraceEnabled())
        {
            String msg = "get() - in thread [" + Thread.currentThread().getName() + "]";
            logger.trace(msg);
        }
        
        Object value = getValue(key);
        if ((value != null) && logger.isTraceEnabled())
        {
            String msg = "Retrieved value of type [" + value.getClass().getName() + "] for key [" + key
                + "] " + "bound to thread [" + Thread.currentThread().getName() + "]";
            logger.trace(msg);
        }
        return value;
    }
    
    /**
     * Binds <tt>value</tt> for the given <code>key</code> to the current thread.
     * <p/>
     * <p>
     * A <tt>null</tt> <tt>value</tt> has the same effect as if <tt>remove</tt> was called
     * for the given <tt>key</tt>, i.e.:
     * <p/>
     * 
     * <pre>
     * if (value == null)
     * {
     *     remove(key);
     * }
     * </pre>
     * 
     * @param key The key with which to identify the <code>value</code>.
     * @param value The value to bind to the thread.
     * @throws IllegalArgumentException if the <code>key</code> argument is <tt>null</tt>.
     */
    public static void put(Object key, Object value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("key cannot be null");
        }
        
        if (value == null)
        {
            remove(key);
            return;
        }
        
        RESOURCES.put(key, value);
        
        if (logger.isTraceEnabled())
        {
            String msg = "Bound value of type [" + value.getClass().getName() + "] for key [" + key
                + "] to thread " + "[" + Thread.currentThread().getName() + "]";
            logger.trace(msg);
        }
    }
    
    /**
     * Unbinds the value for the given <code>key</code> from the current thread.
     * 
     * @param key The key identifying the value bound to the current thread.
     * @return the object unbound or <tt>null</tt> if there was nothing bound under the
     *         specified <tt>key</tt> name.
     */
    public static Object remove(Object key)
    {
        Object value = RESOURCES.remove(key);
        
        if ((value != null) && logger.isTraceEnabled())
        {
            String msg = "Removed value of type [" + value.getClass().getName() + "] for key [" + key + "]"
                + "from thread [" + Thread.currentThread().getName() + "]";
            logger.trace(msg);
        }
        
        return value;
    }
    
    /**
     * {@link ThreadLocal#remove Remove}s the underlying {@link ThreadLocal ThreadLocal}
     * from the thread.
     * <p/>
     * This method is meant to be the final 'clean up' operation that is called at the end
     * of thread execution to prevent thread corruption in pooled thread environments.
     * 
     * @since 1.0
     */
    public static void remove()
    {
        RESOURCES.clear();
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.startsWith(SecurityMatrixBuilder.class.getSimpleName()))
        {
            cacheClient.deleteCache(key);
            return;
        }
        boolean flag = false;
        flag = flag || key.equals(NetworkRegionServiceImpl.class.getSimpleName());
        flag = flag || key.equals(SecurityMatrixServiceImpl.class.getSimpleName());
        flag = flag || key.equals(SecurityPermissionServiceImpl.class.getSimpleName());
        flag = flag || key.equals(UserSpecialServiceImpl.class.getSimpleName());
        flag = flag || key.equals(ProxyNetworkServiceImpl.class.getSimpleName());
        
        if (flag)
        {
            String securityKeys = (String) cacheClient.getCache(SecurityConstants.SECURITY_MATRIX_CACHE);
            if (securityKeys == null)
            {
                return;
            }
            cacheClient.deleteCache(SecurityConstants.SECURITY_MATRIX_CACHE);
            String[] keys = securityKeys.split(";");
            for (String k : keys)
            {
                cacheClient.deleteCache(SecurityMatrixBuilder.class.getSimpleName() + "_" + k);
            }
        }
    }
}
