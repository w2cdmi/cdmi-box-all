package com.huawei.sharedrive.app.user.dao.impl;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.spacestatistics.domain.AccountStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;
import com.huawei.sharedrive.app.user.dao.UserReverseDAO;
import com.huawei.sharedrive.app.user.domain.User;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.utils.HashTool;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userReverseDAO")
@SuppressWarnings("deprecation")
public class UserReverseDAOImpl extends CacheableSqlMapClientDAO implements UserReverseDAO
{
    
    /**
     * 获取accountId 的分表
     * 
     * @param user
     * @return
     */
    private int getTableSuffixByAccount(User user)
    {
        long accountId = user.getAccountId();
        return getTableSuffix(String.valueOf(accountId));
    }
    
    private int getTableSuffix(String ownerId)
    {
        int table = (int) (HashTool.apply(ownerId) % UserDAOImplV2.TABLE_COUNT);
        return table;
    }
    
    @Override
    public void create(User user)
    {
        user.setTableSuffix(getTableSuffixByAccount(user));
        sqlMapClientTemplate.insert("UserReverse.insertToAccount", user);
    }
    
    @Override
    public void delete(long accountId, long id)
    {
        User user = new User();
        user.setAccountId(accountId);
        user.setId(id);
        
        user.setTableSuffix(getTableSuffixByAccount(user));
        sqlMapClientTemplate.delete("UserReverse.deleteToAccount", user);
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<User> getFilterd(User filter, OrderV1 order, Limit limit)
    {
        filter.setTableSuffix(getTableSuffixByAccount(filter));
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("UserReverse.getFilterd", map);
    }
    
    @Override
    public int getFilterdCount(User filter)
    {
        if (filter == null)
        {
            return getAllTableCount();
        }
        filter.setTableSuffix(getTableSuffixByAccount(filter));
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("UserReverse.getFilterdCount", map);
    }
    
    private int getAllTableCount()
    {
        int sum = 0;
        for (int i = 0; i < UserDAOImplV2.TABLE_COUNT; i++)
        {
            sum += (Integer) sqlMapClientTemplate.queryForObject("UserReverse.getTableCount",
                String.valueOf(i));
        }
        return sum;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<User> getOrderedUserList(User filter, List<Order> orderList, long offset, int limit)
    {
        filter.setTableSuffix(getTableSuffixByAccount(filter));
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("offset", offset);
        map.put("limit", limit);
        if (CollectionUtils.isNotEmpty(orderList))
        {
            map.put("orderBy", getOrderByStr(orderList));
        }
        
        return sqlMapClientTemplate.queryForList("UserReverse.getOrderedUser", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<User> getUsedCapacity(Map<String, Object> map)
    {
        User filter = (User) map.get("filter");
        filter.setTableSuffix(getTableSuffixByAccount(filter));
        map.put("filter", filter);
        return sqlMapClientTemplate.queryForList("UserReverse.getUsedCapacity", map);
    }
    
    @Override
    public long countSpaceTotal(Map<String, Object> map)
    {
        User filter = (User) map.get("filter");
        filter.setTableSuffix(getTableSuffixByAccount(filter));
        map.put("filter", filter);
        return (Long) sqlMapClientTemplate.queryForObject("UserReverse.countSpaceTotal", map);
    }
    
    @Override
    public int getUserCountByAccountId(long accountId)
    {
        User user = new User();
        user.setAccountId(accountId);
        user.setTableSuffix(getTableSuffixByAccount(user));
        return (int) sqlMapClientTemplate.queryForObject("UserReverse.getUserCountByAccountId", user);
    }
    
    @Override
    public void updateLastLoginTime(long accountId, long id, Date lastLoginAt)
    {
        User user = new User();
        user.setAccountId(accountId);
        user.setId(id);
        user.setLastLoginAt(lastLoginAt);
        
        user.setTableSuffix(getTableSuffixByAccount(user));
        sqlMapClientTemplate.update("UserReverse.updateLastLoginAtToAccount", user);
        
    }
    
    @Override
    public void updateRegionID(long accountId, long id, int regionID)
    {
        User user = new User();
        user.setAccountId(accountId);
        user.setId(id);
        user.setRegionId(regionID);
        user.setModifiedAt(new Date());
        
        user.setTableSuffix(getTableSuffixByAccount(user));
        sqlMapClientTemplate.update("UserReverse.updateRegionToAccount", user);
        
    }
    
    @Override
    public int updateStatisticInfo(long accountId, long id, Long spaceUsed, Long fileCount)
    {
        User user = new User();
        user.setAccountId(accountId);
        user.setId(id);
        user.setSpaceUsed(spaceUsed);
        user.setFileCount(fileCount);
        user.setLastStatisticsTime(new Date());
        
        user.setTableSuffix(getTableSuffixByAccount(user));
        int result = sqlMapClientTemplate.update("UserReverse.updateStatisticInfoToAccount", user);
        
        return result;
    }
    
    @Override
    public int updateStatisticInfoAndRefreshCach(long accountId, long id, Long spaceUsed, Long fileCount)
    {
        User user = new User();
        user.setAccountId(accountId);
        user.setId(id);
        user.setSpaceUsed(spaceUsed);
        user.setFileCount(fileCount);
        user.setLastStatisticsTime(new Date());
        
        user.setTableSuffix(getTableSuffixByAccount(user));
        int result = sqlMapClientTemplate.update("UserReverse.updateStatisticInfoToAccount", user);
        
        if(isCacheSupported()){
        	String key = UserStatisticsInfo.CACHE_KEY_PRIFIX_USERSINFO + id;
	        UserStatisticsInfo userStatisticsInfo = (UserStatisticsInfo)getCacheClient().getCache(key);
	        if (userStatisticsInfo != null) {
	        	userStatisticsInfo.setFileCount(fileCount);
	        	userStatisticsInfo.setSpaceUsed(spaceUsed);
		        getCacheClient().setCache(key, userStatisticsInfo);
			}
        }
        return result;
    }
    
    @Override
    public int updateSecurityId(long accountId, long id, Integer securityId)
    {
        if (securityId == null)
        {
            return 0;
        }
        User user = new User();
        user.setAccountId(accountId);
        user.setId(id);
        user.setSecurityId(securityId);
        
        user.setTableSuffix(getTableSuffixByAccount(user));
        int result = sqlMapClientTemplate.update("UserReverse.updateSecurityIdToAccount", user);
        
        return result;
    }
    
    @Override
    public int countActiveUserByAccountId(long accountId)
    {
        User user = new User();
        user.setAccountId(accountId);
        user.setType(User.USER_TYPE_USER);
        user.setTableSuffix(getTableSuffixByAccount(user));
        return (int) sqlMapClientTemplate.queryForObject("UserReverse.countActiveUserByAccountId", user);
    }
    
    @Override
    public int countActiveTeamspaceByAccountId(long accountId)
    {
        User user = new User();
        user.setAccountId(accountId);
        user.setType(User.USER_TYPE_TEAMSPACE);
        user.setTableSuffix(getTableSuffixByAccount(user));
        return (int) sqlMapClientTemplate.queryForObject("UserReverse.countActiveUserByAccountId", user);
    }
    
    @Override
    public void updateStatus(long accountId, long id, String status)
    {
        User user = new User();
        user.setAccountId(accountId);
        user.setId(id);
        user.setStatus(status);
        user.setModifiedAt(new Date());
        
        user.setTableSuffix(getTableSuffixByAccount(user));
        sqlMapClientTemplate.update("UserReverse.updateStatusToAccount", user);
        
    }
    
    private String getOrderByStr(List<Order> orderList)
    {
        StringBuffer orderBy = new StringBuffer();
        String field = null;
        for (Order order : orderList)
        {
            field = order.getField();
            
            // 解决中文名称排序问题
            if ("name".equalsIgnoreCase(field))
            {
                field = "convert(name using gb2312)";
            }
            else if ("loginName".equalsIgnoreCase(field))
            {
                field = "convert(loginName using gb2312)";
            }
            orderBy.append(field).append(' ').append(order.getDirection()).append(',');
        }
        orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
        return orderBy.toString();
    }
    
    @Override
    public User getUserByLoginNameAccountId(String loginName, long accountId)
    {
        User user = new User();
        user.setAccountId(accountId);
        user.setLoginName(loginName);
        
        user.setTableSuffix(getTableSuffixByAccount(user));
        return (User) sqlMapClientTemplate.queryForObject("UserReverse.getUserByLoginNameAccountId", user);
    }
    
    @Override
    public User getOneUserOrderByACS(long accountId, long id)
    {
        // 按account去查询user.
        User user = new User();
        user.setAccountId(accountId);
        user.setId(id);
        user.setTableSuffix(getTableSuffixByAccount(user));
        return (User) sqlMapClientTemplate.queryForObject("UserReverse.getOneUserOrderByACS", user);
    }
    
    @Override
    public void update(User user)
    {
        user.setTableSuffix(getTableSuffixByAccount(user));
        sqlMapClientTemplate.update("UserReverse.updateToAccount", user);
    }
    
    @Override
    public AccountStatisticsInfo getAccountInfoById(long accountId)
    {
        User user = new User();
        user.setAccountId(accountId);
        user.setTableSuffix(getTableSuffixByAccount(user));
        Object accountStatisticsInfo = sqlMapClientTemplate.queryForObject("UserReverse.getAccountInfoById",
            user);
        if (null == accountStatisticsInfo)
        {
            return null;
        }
        AccountStatisticsInfo accountInfo = (AccountStatisticsInfo) accountStatisticsInfo;
        
        if (null == accountInfo.getAccountId())
        {
            return null;
        }
        if (null == accountInfo.getCurrentFiles())
        {
            accountInfo.setCurrentFiles(0L);
        }
        if (null == accountInfo.getCurrentSpace())
        {
            accountInfo.setCurrentSpace(0L);
        }
        return accountInfo;
    }
    
    @Override
    public User getBycloudUserId(long accountId, long cloudUserId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tableSuffix", getTableSuffix(accountId + ""));
        paramMap.put("id", cloudUserId);
        paramMap.put("accountId", accountId);
        
        return (User)sqlMapClientTemplate.queryForObject("UserReverse.getUserById", paramMap);
    }
    
    @Override
    public void updateBaseInfo(User user){
        user.setTableSuffix(getTableSuffixByAccount(user));
        sqlMapClientTemplate.update("UserReverse.updateBaseInfo", user);
    }

    @Override
    public List<User> getByAccountIdAndSpaceQuota(long accountId, long spaceQuota) {
        Map<String, Object> map = new HashMap<String, Object>(3);

        map.put("accountId", accountId);
        map.put("spaceQuota", spaceQuota);
        map.put("tableSuffix", getTableSuffix(String.valueOf(accountId)));

        return sqlMapClientTemplate.queryForList("UserReverse.getByAccountIdAndSpaceQuota", map);
    }

    @Override
    public void compareAndSwapSpaceQuotaByAccountId(long accountId, long oldValue, long newValue) {
        Map<String, Object> map = new HashMap<String, Object>(3);

        map.put("accountId", accountId);
        map.put("oldValue", oldValue);
        map.put("newValue", newValue);
        map.put("tableSuffix", getTableSuffix(String.valueOf(accountId)));

        sqlMapClientTemplate.update("UserReverse.compareAndSwapSpaceQuotaByAccountId", map);
    }

    @Override
    public void updateSpaceQuotaByAccountIdAndUserIds(long accountId, List<Long> userIds, long spaceQuota) {
        Map<String, Object> map = new HashMap<String, Object>(3);

        map.put("accountId", accountId);
        map.put("userIds", userIds);
        map.put("spaceQuota", spaceQuota);
        map.put("tableSuffix", getTableSuffix(String.valueOf(accountId)));

        sqlMapClientTemplate.update("UserReverse.updateSpaceQuotaByAccountIdAndUserIds", map);
    }
}
