package com.huawei.sharedrive.uam.user.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.user.dao.AdminDAO;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.domain.AdminRole;
import com.huawei.sharedrive.uam.user.shiro.UsernamePasswordCaptchaToken;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.encrypt.HashPassword;

@Service
@SuppressWarnings("deprecation")
public class AdminDAOImpl extends CacheableSqlMapClientDAO implements AdminDAO {

	private static final String ADMIN_INFORMATION = "Admin_Information_";

	@Override
	public Admin get(Long id) {
		if (isCacheSupported()) {
			String key = Admin.CACHE_KEY_PREFIX_ID + id.longValue();
			Admin admin = (Admin) getCacheClient().getCache(key);
			if (admin != null) {
				return admin;
			}
			admin = (Admin) sqlMapClientTemplate.queryForObject("Admin.get", id);
			if (admin == null) {
				return null;
			}
			getCacheClient().setCache(key, admin);
			return admin;
		}
		return (Admin) sqlMapClientTemplate.queryForObject("Admin.get", id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Admin> getAdminByIds(Long[] ids) {
		if (ids == null || ids.length <= 0) {
			return new ArrayList<Admin>(1);
		}
		return sqlMapClientTemplate.queryForList("Admin.getAdminByIds", ids);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Admin> getAdminExcludeIds(Long[] ids) {
		if (ids == null || ids.length <= 0) {
			Admin filter = new Admin();
			filter.setType(Constants.ROLE_COMMON_ADMIN);
			return getFilterd(filter, null, null);
		}
		return sqlMapClientTemplate.queryForList("Admin.getAdminExcludeIds", ids);
	}

	@Override
	public Admin getByEmail(String email) {
		Admin admin = null;

		admin = (Admin) getCacheClient().getCache(ADMIN_INFORMATION + email);

		if (admin != null) {
			return admin;
		}
		try {
			admin = (Admin) sqlMapClientTemplate.queryForObject("Admin.getByEmail", email);
			if (admin == null) {
				admin = (Admin) sqlMapClientTemplate.queryForObject("Admin.getByLoginName", email);
			}
		} catch (Exception e) {
			admin = (Admin) sqlMapClientTemplate.queryForObject("Admin.getByLoginName", email);
		}
		if (admin == null) {
			return null;
		}
		getCacheClient().setCache(ADMIN_INFORMATION + email, admin);
		return admin;
	}

	@Override
	public Admin getByLoginName(String loginName) {
		Admin admin = null;

		admin = (Admin) getCacheClient().getCache(ADMIN_INFORMATION + loginName);

		if (admin != null) {
			return admin;
		}
		admin = (Admin) sqlMapClientTemplate.queryForObject("Admin.getByLoginName", loginName);
		if (admin == null) {
			return null;
		}
		getCacheClient().setCache(ADMIN_INFORMATION + loginName, admin);
		return admin;
	}

	@Override
	public Admin getByLoginNameWithoutCache(String loginName) {
		return (Admin) sqlMapClientTemplate.queryForObject("Admin.getByLoginName", loginName);
	}

	@Override
	public Admin getByLoginNameAndEnterpriseIdWithoutCache(String loginName, long enterpriseId) {
		Map<String, Object> map = new HashMap<>(2);
		map.put("loginName", loginName);
		map.put("enterpriseId", enterpriseId);

		return (Admin) sqlMapClientTemplate.queryForObject("Admin.getByLoginNameAndEnterpriseId", map);
	}

	@Override
	public Admin getByEmailWithoutCache(String email) {
		try {
			return (Admin) sqlMapClientTemplate.queryForObject("Admin.getByEmail", email);
		} catch (Exception e) {
			return (Admin) sqlMapClientTemplate.queryForObject("Admin.getByLoginName", email);
		}
	}

	@Override
	public Admin getAdminByObjectSID(String objectSID) {
		return (Admin) sqlMapClientTemplate.queryForObject("Admin.getByObjectSID", objectSID);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Admin> getFilterd(Admin filter, Order order, Limit limit) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", filter);
		map.put("order", order);
		map.put("limit", limit);
		return sqlMapClientTemplate.queryForList("Admin.getFilterd", map);
	}

	@Override
	public int getFilterdCount(Admin filter) {
		Map<String, Object> map = new HashMap<String, Object>(1);
		map.put("filter", filter);
		return (Integer) sqlMapClientTemplate.queryForObject("Admin.getFilterdCount", map);
	}

	@Override
	public void delete(Long id) {
		sqlMapClientTemplate.delete("Admin.delete", id);
		String key = Admin.CACHE_KEY_PREFIX_ID + id.longValue();
		deleteCacheAfterCommit(key);
	}

	@Override
	public void updateStatus(Byte status, Long id) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("status", status);
		map.put("id", id);
		map.put("modifiedAt", new Date());
		sqlMapClientTemplate.update("Admin.updateStatus", map);
		String key = Admin.CACHE_KEY_PREFIX_ID + id.longValue();
		deleteCacheAfterCommit(key);
	}

	@Override
	public void create(Admin admin) {
		sqlMapClientTemplate.insert("Admin.insert", admin);
	}

	@Override
	public void update(Admin admin) {
		admin.setModifiedAt(new Date());
		sqlMapClientTemplate.update("Admin.update", admin);
		String key = Admin.CACHE_KEY_PREFIX_ID + admin.getId();
		deleteCacheAfterCommit(key);
	}

	@Override
	public long getNextAvailableAdminId() {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("param", "adminId");
		sqlMapClientTemplate.queryForObject("getNextId", map);
		long id = (Long) map.get("returnid");
		return id;
	}

	@Override
	public void updatePassword(long id, HashPassword hashPassword) {
		Admin admin = new Admin();
		admin.setId(id);
		admin.setPassword(hashPassword.getHashPassword());
		admin.setIterations(hashPassword.getIterations());
		admin.setSalt(hashPassword.getSalt());
		admin.setModifiedAt(new Date());
		sqlMapClientTemplate.update("Admin.updatePassword", admin);
		String key = Admin.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
	}

	@Override
	public void updateValidKeyAndDynamicPwd(long id, String validateKey, String dynamicPwd) {
		Admin admin = new Admin();
		admin.setId(id);
		admin.setValidateKey(validateKey);
		admin.setDynamicPassword(dynamicPwd);
		admin.setModifiedAt(new Date());
		admin.setResetPasswordAt(new Date());
		sqlMapClientTemplate.update("Admin.updateValidKeyAndDynamicPwd", admin);
		String key = Admin.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
	}

	@Override
	public void updateEmail(long id, String email) {
		Admin admin = new Admin();
		admin.setId(id);
		admin.setEmail(email);
		admin.setModifiedAt(new Date());
		sqlMapClientTemplate.update("Admin.updateEmail", admin);
		String key = Admin.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
	}

	@Override
	public void updateLoginName(long id, String loginName) {
		Admin admin = new Admin();
		admin.setId(id);
		admin.setLoginName(loginName);
		admin.setModifiedAt(new Date());
		sqlMapClientTemplate.update("Admin.updateLoginName", admin);
		String key = Admin.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
	}

	@Override
	public void updateLastLoginTime(long id) {
		Admin admin = new Admin();
		admin.setId(id);
		admin.setLastLoginTime(new Date());
		sqlMapClientTemplate.update("Admin.updateLastLoginTime", admin);
		String key = Admin.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
	}

	@Override
	public void updateLastLoginIP(long id, AuthenticationToken authcToken) {
		Admin admin = new Admin();
		admin.setId(id);
		UsernamePasswordCaptchaToken token = (UsernamePasswordCaptchaToken) authcToken;
		admin.setLastLoginIP(token.getHost());
		sqlMapClientTemplate.update("Admin.updateLastLoginIP", admin);
		String key = Admin.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
	}

	@Override
	public void updateLastLoginInfo(long id, String loginIP) {
		Admin admin = new Admin();
		admin.setId(id);
		admin.setLastLoginTime(new Date());
		admin.setLastLoginIP(loginIP);
		sqlMapClientTemplate.update("Admin.updateLastLoginInfo", admin);
		String key = Admin.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
	}

	@Override
	public void updateRoles(long id, HashSet<AdminRole> roles) {
		Admin admin = new Admin();
		admin.setId(id);
		admin.setRoles(roles);
		admin.setModifiedAt(new Date());
		sqlMapClientTemplate.update("Admin.updateRoles", admin);
		String key = Admin.CACHE_KEY_PREFIX_ID + id;
		deleteCacheAfterCommit(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Admin> getByRole(AdminRole role) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", new Admin());
		List<Admin> list = sqlMapClientTemplate.queryForList("Admin.getFilterd", map);
		List<Admin> result = new LinkedList<Admin>();
		for (Admin admin : list) {
			if (admin.getRoles().contains(role)) {
				result.add(admin);
			}
		}
		return result;
	}

}
