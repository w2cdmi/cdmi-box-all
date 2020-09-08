package pw.cdmi.box.uam.authapp.manager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pw.cdmi.box.uam.authapp.domain.AuthAppNetRegionIpCacheObj;

public final class IpListCache
{
    private IpListCache()
    {
    }
    
    public static List<AuthAppNetRegionIpCacheObj> getIpListFromCach(String appId)
    {
        return IP_CACHE.get(appId);
    }
    
    public static void removeFromIpListCach(String appId)
    {
        IP_CACHE.remove(appId);
    }
    
    public static void putIntoIpListCach(String appId, List<AuthAppNetRegionIpCacheObj> list)
    {
        IP_CACHE.put(appId, list);
    }
    
    private static final Map<String, List<AuthAppNetRegionIpCacheObj>> IP_CACHE = new ConcurrentHashMap<String, List<AuthAppNetRegionIpCacheObj>>(
        1000);
}
