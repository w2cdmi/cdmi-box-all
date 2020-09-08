package com.huawei.sharedrive.uam.enterpriseuser.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.uam.organization.domain.Department;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import com.huawei.sharedrive.uam.enterpriseuser.dao.EnterpriseUserDao;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.dto.EnterpriseUserStatus;
import com.huawei.sharedrive.uam.idgenerate.service.EnterpriseUserGenerateService;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class EnterpriseUserDaoImpl extends CacheableSqlMapClientDAO implements EnterpriseUserDao
{

	public static final int TABLE_COUNT = 100;

	@Autowired
	private EnterpriseUserGenerateService enterpriseUserGenerateService;

	@Override
	public long getMaxUserId() {
		long maxUserId = 1L;
		long selMaxUserId;
		String tableSuffix;
		Object maxUserIdObject;
		for (int i = 0; i < TABLE_COUNT; i++) {
			tableSuffix = "_" + i;
			maxUserIdObject = sqlMapClientTemplate.queryForObject("EnterpriseUser.getMaxUserId", tableSuffix);
			selMaxUserId = maxUserIdObject == null ? 0L : (long) maxUserIdObject;
			if (maxUserId < selMaxUserId) {
				maxUserId = selMaxUserId;
			}
		}
		return maxUserId;
	}

	@Override
	public long create(EnterpriseUser enterpriseUser) {
		long userId = enterpriseUserGenerateService.getNextUserId();
		enterpriseUser.setId(userId);
		enterpriseUser.setTableSuffix(getTableSuffix(enterpriseUser.getEnterpriseId()));
		sqlMapClientTemplate.insert("EnterpriseUser.insert", enterpriseUser);
		return userId;
	}

	@Override
	public void update(EnterpriseUser enterpriseUser) {

		enterpriseUser.setTableSuffix(getTableSuffix(enterpriseUser.getEnterpriseId()));
		sqlMapClientTemplate.update("EnterpriseUser.update", enterpriseUser);
	}

	@Override
	public EnterpriseUser get(long userId, long enterpriseId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("userId", userId);
		map.put("enterpriseId", enterpriseId);
		EnterpriseUser enterpriseUser = (EnterpriseUser) sqlMapClientTemplate.queryForObject("EnterpriseUser.get", map);
		return enterpriseUser;
	}

	@Override
	public EnterpriseUser getUserInfo(long userId, long enterpriseId, String authType) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("userId", userId);
		map.put("enterpriseId", enterpriseId);
		map.put("authType", authType);
		EnterpriseUser enterpriseUser = (EnterpriseUser) sqlMapClientTemplate.queryForObject("EnterpriseUser.getUserInfo", map);
		return enterpriseUser;
	}

	@Override
	public EnterpriseUser getByObjectSid(String objectSid, long enterpriseId) {

		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("objectSid", objectSid);
		map.put("enterpriseId", enterpriseId);
		EnterpriseUser enterpriseUser = (EnterpriseUser) sqlMapClientTemplate.queryForObject("EnterpriseUser.getByObjectSid", map);
		return enterpriseUser;
	}

	@Override
	public boolean isExistEmailEnterpriseId(String email, String enterpriseId) {

		Map<String, Object> map = new HashMap<String, Object>(2);
		Object obj = sqlMapClientTemplate.queryForObject("EnterpriseUser.getEmailEnterpriseId", map);
		if (obj == null) {
			return false;
		}
		return true;
	}

	@Override
	public int getFilterdCount(String filter, Long authServerId, Long enterpriseId, Long departmentId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("filter", filter);
		map.put("authServerId", authServerId);
		map.put("enterpriseId", enterpriseId);
		map.put("departmentId", departmentId);
		return (Integer) sqlMapClientTemplate.queryForObject("EnterpriseUser.getFilterdCount", map);
	}

	@Override
	public List<EnterpriseUser> getFilterd(String filter, Long authServerId, Long departmentId, Long enterpriseId, Order order, Limit limit) {
		Map<String, Object> map = new HashMap<String, Object>(8);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("filter", filter);
		map.put("authServerId", authServerId);
		map.put("order", order);
		map.put("limit", limit);
		map.put("enterpriseId", enterpriseId);
		map.put("departmentId", departmentId);
		return sqlMapClientTemplate.queryForList("EnterpriseUser.getFilterd", map);
	}

	@Override
	public int getByLdapStatusCount(Byte ldapStatus, Long enterpriseId) {
		if (null == ldapStatus || null == enterpriseId) {
			return 0;
		}
		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("ldapStatus", ldapStatus);
		map.put("enterpriseId", enterpriseId);
		return (Integer) sqlMapClientTemplate.queryForObject("EnterpriseUser.getByLdapStatusCount", map);
	}

	@Override
	public List<EnterpriseUser> getByLdapStatus(Byte ldapStatus, Long enterpriseId, Limit limit) {
		if (null == ldapStatus || null == enterpriseId) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("ldapStatus", ldapStatus);
		map.put("enterpriseId", enterpriseId);
		map.put("limit", limit);
		return sqlMapClientTemplate.queryForList("EnterpriseUser.getByLdapStatus", map);
	}

	@Override
	public List<EnterpriseUser> getAllADEnterpriseUser(Long enterpriseId, Long authServerId) {
		Map<String, Object> map = new HashMap<String, Object>(5);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("enterpriseId", enterpriseId);
		map.put("authServerId", authServerId);
		return sqlMapClientTemplate.queryForList("EnterpriseUser.getAllADEnterpriseUser", map);
	}

	@Override
	public List<EnterpriseUser> getAccountUser(long accountId, long enterpriseId, long userSource, String filter, String ids) {

		Map<String, Object> map = new HashMap<String, Object>(8);
		map.put("accountTableSuffix", getTableSuffix(accountId));
		map.put("enterpriseTableSuffix", getTableSuffix(enterpriseId));
		map.put("accountId", accountId);
		map.put("enterpriseId", enterpriseId);
		map.put("userSource", userSource);
		if (StringUtils.isNotBlank(filter)) {
			map.put("filter", filter);
		}
		if (StringUtils.isNotBlank(ids)) {
			map.put("ids", ids);
		}
		return sqlMapClientTemplate.queryForList("EnterpriseUser.getAccountUser", map);
	}

	@Override
	public void deleteByIds(long enterpriseId, String ids) {
		if (null != ids) {
			Map<String, Object> map = new HashMap<String, Object>(2);
			map.put("tableSuffix", getTableSuffix(enterpriseId));
			map.put("ids", ids);
			sqlMapClientTemplate.delete("EnterpriseUser.deleteByIds", map);
		}
	}

	@Override
	public void deleteById(long enterpriseId, Long id) {
		if (null != id) {
			Map<String, Object> map = new HashMap<String, Object>(2);
			map.put("tableSuffix", getTableSuffix(enterpriseId));
			map.put("id", id);
			sqlMapClientTemplate.delete("EnterpriseUser.deleteById", map);
		}
	}

	@Override
	public void updateLdapStatus(List<EnterpriseUser> list, Byte ldapStatus, Long enterpriseId) {
		if (null == list || list.size() < 1) {
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("list", list);
		map.put("ldapStatus", ldapStatus);
		sqlMapClientTemplate.update("EnterpriseUser.updateLdapStatus", map);
	}

	@Override
	public void updateLdapStatusByNotIn(List<EnterpriseUser> list, Byte ldapStatus, Long enterpriseId) {
		if (null == list || list.size() < 1) {
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("list", list);
		map.put("ldapStatus", ldapStatus);
		map.put("enterpriseId", enterpriseId);
		sqlMapClientTemplate.update("EnterpriseUser.updateLdapStatusByNotIn", map);
	}

	public static String getTableSuffix(long enterpriseId) {
		int table = (int) (HashTool.apply(String.valueOf(enterpriseId)) % TABLE_COUNT);
		return "_" + table;
	}

	@Override
	public void updateEnterpriseUser(EnterpriseUser enterpriseUser) {
		enterpriseUser.setTableSuffix(getTableSuffix(enterpriseUser.getEnterpriseId()));
		sqlMapClientTemplate.update("EnterpriseUser.updateEnterpriseUser", enterpriseUser);
	}

	@Override
	public void updateStatus(List<Long> userIds, EnterpriseUserStatus status, Long enterpriseId) {
		if (null == userIds || userIds.size() <= 0) {
			return;
		}

		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("userIds", userIds);
		map.put("status", status.getStatusCode());
		sqlMapClientTemplate.update("EnterpriseUser.updateStatus", map);
	}

	@Override
	public List<EnterpriseUser> getAllEnterpriseUser(Long enterpriseId) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("enterpriseId", enterpriseId);
		return sqlMapClientTemplate.queryForList("EnterpriseUser.getAllEnterpriseUser", map);
	}

	@Override
	public List<EnterpriseUser> getByEnterpriseAndDepartment(long enterpriseId, long depId) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("enterpriseId", enterpriseId);
		map.put("departmentId", depId);
		return sqlMapClientTemplate.queryForList("EnterpriseUser.listByDepId", map);
	}

	@Override
	public void updateDepartmentById(long id, long enterpriseId, long departmentId) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("id", id);
		map.put("departmentId", departmentId);
		sqlMapClientTemplate.update("EnterpriseUser.updateEnterpriseUserDept", map);
	}

	@Override
	public List<EnterpriseUser> getByEnterpriseIdAndDeptIdAndType(Long enterpriseId, Long deptId, Byte type) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("enterpriseId", enterpriseId);
		map.put("departmentId", deptId);
		map.put("type", type);

		return sqlMapClientTemplate.queryForList("EnterpriseUser.getByEnterpriseIdAndDeptIdAndType", map);
	}

	@Override
	public void updateTypeById(long enterpriseId, long id, byte type) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("id", id);
		map.put("type", type);

		sqlMapClientTemplate.update("EnterpriseUser.updateTypeById", map);
	}

	@Override
	public void updateTypeByDept(long enterpriseId, long dept, byte type) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("enterpriseId", enterpriseId);
		map.put("id", dept);
		map.put("type", type);

		sqlMapClientTemplate.update("EnterpriseUser.updateTypeByDept", map);
	}

	@Override
	public void changeTypeInDept(long enterpriseId, long deptId, byte oldValue, byte newValue) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("enterpriseId", enterpriseId);
		map.put("departmentId", deptId);
		map.put("oldValue", oldValue);
		map.put("newValue", newValue);

		sqlMapClientTemplate.update("EnterpriseUser.changeTypeInDept", map);
	}

	@Override
	public EnterpriseUser getByEnterpriseIdAndName(long enterpriseId, String name) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("enterpriseId", enterpriseId);
		map.put("name", name);

		return (EnterpriseUser) sqlMapClientTemplate.queryForObject("EnterpriseUser.getByEnterpriseIdAndName", map);
	}

	@Override
	public int getFilterdCount(String filter, Long authServerId, long enterpriseId, Long departmentId, long type, String status) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("filter", filter);
		map.put("authServerId", authServerId);
		map.put("enterpriseId", enterpriseId);
		if (type != -1) {
			map.put("type", type);
		}
		if (departmentId != null && departmentId != -1) {
			map.put("departmentId", departmentId);
		}
		if (status != null && !"".equals(status)) {
			map.put("status", status);
		}
		return (Integer) sqlMapClientTemplate.queryForObject("EnterpriseUser.getFilterdCount", map);
	}

	@Override
	public List<EnterpriseUser> getFilterd(String filter, Long authServerId, Long departmentId, long enterpriseId, Order order, Limit limit, long type, String status) {
		Map<String, Object> map = new HashMap<String, Object>(8);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("filter", filter);
		map.put("authServerId", authServerId);
		map.put("order", order);
		map.put("limit", limit);
		map.put("enterpriseId", enterpriseId);
		if (departmentId != null && departmentId != -1) {
			map.put("departmentId", departmentId);
		}
		if (status != null && !"".equals(status)) {
			map.put("status", status);
		}
		if (type != -1) {
			map.put("type", type);
		}
		
		return sqlMapClientTemplate.queryForList("EnterpriseUser.getFilterd", map);
	}

	public void changeStatus(EnterpriseUser user) {
		user.setTableSuffix(getTableSuffix(user.getEnterpriseId()));

		sqlMapClientTemplate.update("EnterpriseUser.updateStatusByEnterpriseId", user);
	}

	public List<EnterpriseUser> getByEnterpriseIdAndStatus(long enterpriseId, byte status) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("enterpriseId", enterpriseId);
		map.put("status", status);

		return sqlMapClientTemplate.queryForList("EnterpriseUser.getByEnterpriseIdAndStatus", map);
	}

	// 获取某个部门下的用户信息
	@Override
	public List<EnterpriseUser> getByDeptId(long enterpriseId, long deptId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("enterpriseId", enterpriseId);
		map.put("departmentId", deptId);

		return sqlMapClientTemplate.queryForList("EnterpriseUser.getByDeptId", map);
	}
}
