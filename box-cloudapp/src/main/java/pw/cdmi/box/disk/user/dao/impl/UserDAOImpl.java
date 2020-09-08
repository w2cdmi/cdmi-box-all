package pw.cdmi.box.disk.user.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.user.dao.UserDAO;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.utils.SqlUtils;

@Service("UserDAO")
@SuppressWarnings("deprecation")
public class UserDAOImpl extends CacheableSqlMapClientDAO implements UserDAO
{
    
    private static Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);
    
    private LoadingCache<Long, User> localCache;
    
    @Autowired(required = false)
    @Qualifier("uamCacheClient")
    private CacheClient cacheClient;
    
    @Override
    public CacheClient getCacheClient()
    {
        return cacheClient;
    }
    
    @Override
    public void create(User user)
    {
        sqlMapClientTemplate.insert("User.insert", user);
    }
    
    @Override
    public void delete(Long id)
    {
        sqlMapClientTemplate.delete("User.delete", id);
    }
    
    @Override
    public User get(Long id)
    {
        if (isCacheSupported())
        {
            String key = User.CACHE_KEY_PREFIX_ID + id.longValue();
            User user = (User) getCacheClient().getCache(key);
            if (user != null)
            {
                return user;
            }
            user = (User) sqlMapClientTemplate.queryForObject("User.get", id);
            if (user == null)
            {
                return null;
            }
            getCacheClient().setCache(key, user);
            return user;
        }
        return (User) sqlMapClientTemplate.queryForObject("User.get", id);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<User> getFilterd(User filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("User.getFilterd", map);
    }
    
    @Override
    public int getFilterdCount(User filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("User.getFilterdCount", map);
    }
    
    @Override
    public long getMaxUserId()
    {
        Object maxUserId = sqlMapClientTemplate.queryForObject("User.getMaxUserId");
        return maxUserId == null ? 0L : (Long) maxUserId;
    }
    
    @Override
    public User getUserByLoginNameAppId(String loginName, String appId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("loginName", SqlUtils.stringToSqlEqualFields(loginName));
        map.put("appId", appId);
        return (User) sqlMapClientTemplate.queryForObject("User.getUserByLoginNameAppId", map);
    }
    
    @Override
    public User getUserByObjectSid(String objectSid)
    {
        return (User) sqlMapClientTemplate.queryForObject("User.getUserByObjectSid", objectSid);
    }
    
    @Override
    public void sacleUser(long id, long spaceQuota)
    {
        User user = new User();
        user.setId(id);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.sacle", user);
    }
    
    @Override
    public void update(User user)
    {
        sqlMapClientTemplate.update("User.update", user);
    }
    
    @Override
    public void updateLastLoginTime(long id, Date lastLoginAt)
    {
        User user = new User();
        user.setId(id);
        user.setLastLoginAt(lastLoginAt);
        sqlMapClientTemplate.update("User.updateLastLoginAt", user);
        
    }
    
    @Override
    public void updatePassword(long id, String newPsw)
    {
        User user = new User();
        user.setId(id);
        user.setPassword(newPsw);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.updatePassword", user);
    }
    
    @Override
    public void updateRegionID(long id, int regionID)
    {
        User user = new User();
        user.setId(id);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.updateRegion", user);
    }
    
    @Override
    public void updateStatus(long id, String status)
    {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.updateStatus", user);
    }
    
    @Override
    public User getUserByCloudUserId(long onwerId)
    {
        if (localCache == null)
        {
            localCache = CacheBuilder.newBuilder().build(new CacheLoader<Long, User>()
            {
                @Override
                public User load(Long id)
                {
                    return (User) sqlMapClientTemplate.queryForObject("User.getUserByCloudUserId", id);
                }
            });
        }
        
        try
        {
            return localCache.get(onwerId);
        }
        catch (ExecutionException e)
        {
            logger.error("Fail in get cache", e);
            return null;
        }
    }
}
