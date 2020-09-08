package com.huawei.sharedrive.app.user.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.utils.BusinessConstants;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.core.utils.HashTool;

@Service("userDAOV2")
@SuppressWarnings("deprecation")
public class UserDAOImplV2 extends CacheableSqlMapClientDAO implements UserDAOV2 {
	public static final int TABLE_COUNT = 100;

    private int getTableSuffix(String ownerId) {
		int table = (int) (HashTool.apply(ownerId) % TABLE_COUNT);
		return table;
	}

	/**
	 * 通过用户获取分表
	 * 
	 * @param user
	 * @return
	 */
    private int getTableSuffixByUser(User user) {
		long userId = user.getId();
		return getTableSuffix(String.valueOf(userId));
	}

	@Override
    public void create(User user) {
		user.setTableSuffix(getTableSuffixByUser(user));
		sqlMapClientTemplate.insert("User.insert", user);
	}

	@Override
    public void delete(long accountId, long id) {
		User user = new User();
		user.setAccountId(accountId);
		user.setId(id);

		user.setTableSuffix(getTableSuffixByUser(user));
		sqlMapClientTemplate.delete("User.delete", user);

		String key = User.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
	}

	@Override
    public User get(long id) {
        if (!isCacheSupported()) {
			String key = User.CACHE_KEY_PREFIX_ID + id;
			User user = (User) getCacheClient().getCache(key);
			if (user != null && user.getStatus() != null) {
				return user;
			}
			user = new User();
			user.setId(id);
			user.setTableSuffix(getTableSuffixByUser(user));
			user = (User) sqlMapClientTemplate.queryForObject("User.get", user);
            if (user == null) {
				return null;
			}
			getCacheClient().setCache(key, user);
			return user;
		}
		User user = new User();
		user.setId(id);
		user.setTableSuffix(getTableSuffixByUser(user));
		return (User) sqlMapClientTemplate.queryForObject("User.get", user);
	}

	/**
	 * 获取user表最大ID值
	 * 
	 * @return
	 */
	@Override
    public long getMaxUserId() {
		User user = new User();
		long max = 1L;
		long temp = 0L;
		Object maxUserId = null;
        for (int i = 0; i < TABLE_COUNT; i++) {
			user.setTableSuffix(i);

			maxUserId = sqlMapClientTemplate.queryForObject("User.getMaxUserId", user);
			temp = (maxUserId == null) ? 0L : (long) maxUserId;

            if (temp > max) {
				max = temp;
			}
		}

		return max;
	}

	// @Override
	// public long getNextAvailableUserId()
	// {
	// Map<String, Object> map = new HashMap<String, Object>(2);
	// map.put("param", "userId");
	// sqlMapClientTemplate.queryForObject("getNextId", map);
	// long id = (Long) map.get("returnid");
	// return id;
	// }

	@Override
    public int getMaxVersions(long userId) {
		User user = new User();
		user.setId(userId);
		user.setTableSuffix(getTableSuffixByUser(user));
		Object maxVersions = sqlMapClientTemplate.queryForObject("User.getMaxVersions", user);
		return maxVersions == null ? -1 : (int) maxVersions;
	}

	@Override
	@Deprecated
    public User getUserByObjectSid(String objectSid, long accountId) {
		return (User) sqlMapClientTemplate.queryForObject("User.getUserByObjectSid", objectSid);
	}

	@SuppressWarnings("unchecked")
	@Override
    public List<User> getUserByStatus(String status) {
		User user = new User();
		user.setStatus(status);
		List<User> list = new ArrayList<User>(BusinessConstants.INITIAL_CAPACITIES);
		List<User> result = null;
        for (int i = 0; i < TABLE_COUNT; i++) {
			user.setTableSuffix(i);

			result = sqlMapClientTemplate.queryForList("User.getByStatus", user);
            if (CollectionUtils.isEmpty(result)) {
				continue;
			}
			list.addAll(result);
		}
		return list;
	}

	@Override
    public void update(User user) {
		user.setTableSuffix(getTableSuffixByUser(user));
		sqlMapClientTemplate.update("User.update", user);

		String key = User.CACHE_KEY_PREFIX_ID + user.getId();
		deleteCacheAfterCommit(key);
	}

	@Override
    public void updateLastLoginTime(long accountId, long id, Date lastLoginAt) {
		User user = new User();
		user.setAccountId(accountId);
		user.setId(id);
		user.setLastLoginAt(lastLoginAt);
		user.setTableSuffix(getTableSuffixByUser(user));
		sqlMapClientTemplate.update("User.updateLastLoginAt", user);

		String key = User.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
	}

	@Override
    public void updateRegionID(long accountId, long id, int regionID) {
		User user = new User();
		user.setAccountId(accountId);
		user.setId(id);
		user.setRegionId(regionID);
		user.setModifiedAt(new Date());

		user.setTableSuffix(getTableSuffixByUser(user));
		sqlMapClientTemplate.update("User.updateRegion", user);

		String key = User.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
	}

	@Override
    public int updateStatisticInfo(long accountId, long id, Long spaceUsed, Long fileCount) {
		User user = new User();
		user.setAccountId(accountId);
		user.setId(id);
		user.setSpaceUsed(spaceUsed);
		user.setFileCount(fileCount);
		user.setLastStatisticsTime(new Date());

		user.setTableSuffix(getTableSuffixByUser(user));
		int result = sqlMapClientTemplate.update("User.updateStatisticInfo", user);

		String key = User.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
		return result;
	}

	@Override
	public int updateStatisticInfoAndRefreshCache(User user,long accountId, long id, Long spaceUsed, Long fileCount) {
//		User user = new User();
		user.setAccountId(accountId);
		user.setId(id);
		user.setSpaceUsed(spaceUsed);
		user.setFileCount(fileCount);
		user.setLastStatisticsTime(new Date());

		user.setTableSuffix(getTableSuffixByUser(user));
		int result = sqlMapClientTemplate.update("User.updateStatisticInfo", user);

		if (isCacheSupported()) {
			String key = User.CACHE_KEY_PREFIX_ID + id;
			getCacheClient().setCache(key, user);
		}
		return result;
	}
    @Override
    public int updateSecurityId(long accountId, long id, Integer securityId) {
        if (securityId == null) {
			return 0;
		}
		User user = new User();
		user.setAccountId(accountId);
		user.setId(id);
		user.setSecurityId(securityId);

		user.setTableSuffix(getTableSuffixByUser(user));
		int result = sqlMapClientTemplate.update("User.updateSecurityId", user);

		String key = User.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
		return result;
	}

	@Override
    public void updateStatus(long accountId, long id, String status) {
		User user = new User();
		user.setAccountId(accountId);
		user.setId(id);
		user.setStatus(status);
		user.setModifiedAt(new Date());

		user.setTableSuffix(getTableSuffixByUser(user));
		sqlMapClientTemplate.update("User.updateStatus", user);

		String key = User.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
	}

	@Override
	public void updateSpaceQuota(long userId, long spaceQuota) {
		User user = new User();
		user.setId(userId);
		user.setSpaceQuota(spaceQuota);
		user.setTableSuffix(getTableSuffix(String.valueOf(userId)));

		sqlMapClientTemplate.update("User.updateSpaceQuota", user);
	}
}