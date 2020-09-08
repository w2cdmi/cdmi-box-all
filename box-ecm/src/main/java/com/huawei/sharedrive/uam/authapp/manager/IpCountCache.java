package com.huawei.sharedrive.uam.authapp.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class IpCountCache
{
    private IpCountCache()
    {
    }
    
    private static final Map<String, Integer> IP_COUNT_CACHE = new ConcurrentHashMap<String, Integer>(1000);
    
    public static Integer getCountFromCach(String appId)
    {
        synchronized (IpCountCache.class)
        {
            return IP_COUNT_CACHE.get(appId);
        }
    }
    
    public static void removeFromIpCountCach(String appId)
    {
        synchronized (IpCountCache.class)
        {
            IP_COUNT_CACHE.remove(appId);
        }
    }
    
    public static void putIntoIpCountCach(String appId, Integer list)
    {
        synchronized (IpCountCache.class)
        {
            IP_COUNT_CACHE.put(appId, list);
        }
    }
    
}
