package com.huawei.sharedrive.uam.authapp.manager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.huawei.sharedrive.uam.authapp.domain.AuthAppNetRegionIpCacheObj;

public final class IpListCache
{
    private IpListCache()
    {
    }
    
    public static List<AuthAppNetRegionIpCacheObj> getIpListFromCach(String appId)
    {
        synchronized (IpListCache.class)
        {
            return IP_CACHE.get(appId);
        }
    }
    
    public static void removeFromIpListCach(String appId)
    {
        synchronized (IpListCache.class)
        {
            IP_CACHE.remove(appId);
        }
    }
    
    public static void putIntoIpListCach(String appId, List<AuthAppNetRegionIpCacheObj> list)
    {
        synchronized (IpListCache.class)
        {
            IP_CACHE.put(appId, list);
        }
    }
    
    private static final Map<String, List<AuthAppNetRegionIpCacheObj>> IP_CACHE = new ConcurrentHashMap<String, List<AuthAppNetRegionIpCacheObj>>(
        1000);
}
