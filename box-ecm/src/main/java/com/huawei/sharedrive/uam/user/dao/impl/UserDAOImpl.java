package com.huawei.sharedrive.uam.user.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.openapi.domain.UserOrder;
import com.huawei.sharedrive.uam.user.dao.UserDAO;
import com.huawei.sharedrive.uam.user.domain.User;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.SqlUtils;

@Service("UserDAO")
@SuppressWarnings({"deprecation", "unchecked"})
public class UserDAOImpl extends CacheableSqlMapClientDAO implements UserDAO
{
    @Override
    public long getMaxUserId()
    {
        Object maxUserId = sqlMapClientTemplate.queryForObject("User.getMaxUserId");
        return maxUserId == null ? 0L : (long) maxUserId;
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
    
    @Override
    public User getUserByLoginNameAppId(String loginName, String appId)
    {
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("loginName", SqlUtils.stringToSqlEqualFields(loginName));
        map.put("appId", appId);
        return (User) sqlMapClientTemplate.queryForObject("User.getUserByLoginNameAppId", map);
    }
    
    @Override
    public User getUserByObjectSidAppId(String objectSid, String appId)
    {
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("objectSid", objectSid);
        map.put("appId", appId);
        
        return (User) sqlMapClientTemplate.queryForObject("User.getUserByObjectSidAppId", map);
    }
    
    @Override
    public List<User> getFilterdOrderList(User filter, List<UserOrder> orderList, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (CollectionUtils.isNotEmpty(orderList))
        {
            map.put("orderBy", getOrderByStr(orderList));
        }
        map.put("filter", filter);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("User.getFilterdOrderList", map);
    }
    
    @Override
    public List<User> getFilterd(User filter, Order order, Limit limit)
    {
        if (order == null)
        {
            order = new Order();
            order.setDesc(true);
            order.setField("createdAt");
        }
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("User.getFilterd", map);
    }
    
    @Override
    public int getFilterdCount(User filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("User.getFilterdCount", map);
    }
    
    @Override
    public void delete(Long id)
    {
        sqlMapClientTemplate.delete("User.delete", id);
        String key = User.CACHE_KEY_PREFIX_ID + id.longValue();
        deleteCacheAfterCommit(key);
        String cloudappKey = User.CACHE_KEY_CLOUD_PREFIX_ID + id.longValue();
        deleteCacheAfterCommit(cloudappKey);
    }
    
    @Override
    public void create(User user)
    {
        sqlMapClientTemplate.insert("User.insert", user);
    }
    
    @Override
    public void update(User user)
    {
        sqlMapClientTemplate.update("User.update", user);
        String key = User.CACHE_KEY_PREFIX_ID + user.getId();
        deleteCacheAfterCommit(key);
        String cloudappKey = User.CACHE_KEY_CLOUD_PREFIX_ID + user.getId();
        deleteCacheAfterCommit(cloudappKey);
    }
    
    @Override
    public long getNextAvailableUserId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "userId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        long id = (Long) map.get("returnid");
        return id;
    }
    
    @Override
    public List<User> getAll()
    {
        return sqlMapClientTemplate.queryForList("User.getAll");
    }
    
    @Override
    public List<User> getUsersByAppId(String appId)
    {
        Map<String, String> map = new HashMap<String, String>(1);
        map.put("appId", appId);
        return sqlMapClientTemplate.queryForList("User.getUsersAppId", map);
    }
    
    @Override
    public void updateStatus(long id, String status)
    {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.updateStatus", user);
        String key = User.CACHE_KEY_PREFIX_ID + id;
        deleteCacheAfterCommit(key);
        String cloudappKey = User.CACHE_KEY_CLOUD_PREFIX_ID + id;
        deleteCacheAfterCommit(cloudappKey);
    }
    
    @Override
    public void updateRegionID(long id, int regionID)
    {
        User user = new User();
        user.setId(id);
        user.setRegionId(regionID);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.updateRegion", user);
        String key = User.CACHE_KEY_PREFIX_ID + id;
        deleteCacheAfterCommit(key);
        String cloudappKey = User.CACHE_KEY_CLOUD_PREFIX_ID + id;
        deleteCacheAfterCommit(cloudappKey);
    }
    
    @Override
    public void updateValidateKey(long id, String validateKey)
    {
        User user = new User();
        user.setId(id);
        user.setValidateKey(validateKey);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.updateValidateKey", user);
        String key = User.CACHE_KEY_PREFIX_ID + id;
        deleteCacheAfterCommit(key);
        String cloudappKey = User.CACHE_KEY_CLOUD_PREFIX_ID + id;
        deleteCacheAfterCommit(cloudappKey);
    }
    
    @Override
    public void updateTeamSpace(long id, byte teamSpaceFlag, int teamSpaceMaxNum)
    {
        User user = new User();
        user.setId(id);
        user.setTeamSpaceFlag(teamSpaceFlag);
        user.setTeamSpaceMaxNum(teamSpaceMaxNum);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.updateTeamSpace", user);
        String key = User.CACHE_KEY_PREFIX_ID + id;
        deleteCacheAfterCommit(key);
        String cloudappKey = User.CACHE_KEY_CLOUD_PREFIX_ID + id;
        deleteCacheAfterCommit(cloudappKey);
    }
    
    @Override
    public void sacleUser(long id, long spaceQuota)
    {
        User user = new User();
        user.setId(id);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.sacle", user);
        String key = User.CACHE_KEY_PREFIX_ID + id;
        deleteCacheAfterCommit(key);
        String cloudappKey = User.CACHE_KEY_CLOUD_PREFIX_ID + id;
        deleteCacheAfterCommit(cloudappKey);
    }
    
    @Override
    public void updatePassword(long id, HashPassword hashPassword)
    {
        User user = new User();
        user.setId(id);
        user.setPassword(hashPassword.getHashPassword());
        user.setIterations(hashPassword.getIterations());
        user.setSalt(hashPassword.getSalt());
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.updatePassword", user);
        String key = User.CACHE_KEY_PREFIX_ID + id;
        deleteCacheAfterCommit(key);
        String cloudappKey = User.CACHE_KEY_CLOUD_PREFIX_ID + id;
        deleteCacheAfterCommit(cloudappKey);
    }
    
    @Override
    public User getUserByCloudUserId(Long onwerId)
    {
        return (User) sqlMapClientTemplate.queryForObject("User.getUserByCloudUserId", onwerId);
    }
    
    @Override
    public void addUserList(List<User> userlist)
    {
        sqlMapClientTemplate.insert("User.insert", userlist);
    }
    
    @Override
    public void updateuserlist(List<User> userbyupdatelist)
    {
        sqlMapClientTemplate.update("User.updateusermap", userbyupdatelist);
    }
    
    @Override
    public void deleteUserByid(Long id)
    {
        sqlMapClientTemplate.delete("User.delete", id);
    }
    
    @Override
    public void updateUserByid(User user)
    {
        sqlMapClientTemplate.update("User.updateUsers", user);
        
    }
    
    @Override
    public Long findUserById()
    {
        return (Long) sqlMapClientTemplate.queryForObject("User.getUserid");
    }
    
    private String getOrderByStr(List<UserOrder> orderList)
    {
        StringBuffer orderBy = new StringBuffer();
        String field;
        for (UserOrder order : orderList)
        {
            field = order.getField();
            orderBy.append(field).append(' ').append(order.getDirection()).append(',');
        }
        orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
        return orderBy.toString();
    }
    
    @Override
    public List<User> listUser(User filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("getFilterd", map);
    }
    
    @Override
    public List<User> listUserByAppid(String appId, int offset, int length)
    {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("appId", appId);
        map.put("offset", offset);
        map.put("length", length);
        return sqlMapClientTemplate.queryForList("User.getUsersAppIdAndLimit", map);
    }
    
    @Override
    public List<String> getFilterdId(User filter)
    {
        return sqlMapClientTemplate.queryForList("User.getFilterdId", filter);
    }
    
}
