package com.huawei.sharedrive.app.user.dao.impl;

import com.huawei.sharedrive.app.user.dao.DepartmentDao;
import com.huawei.sharedrive.app.user.domain.*;

import org.springframework.stereotype.Service;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.core.utils.HashTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "unchecked", "deprecation" })
@Service
public class DepartmentDaoImpl extends CacheableSqlMapClientDAO implements DepartmentDao {
	private static final int TABLE_COUNT = 100;

	@Override
	public Department getByEnterpriseIdAndDepartmentId(long enterpriseId, long departmentId) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("enterpriseId", enterpriseId);
		map.put("departmentId", departmentId);
		return (Department) sqlMapClientTemplate.queryForObject("Department.getDeptById", map);
	}

	@Override
	public Department getByEnterpriseIdAndDepartmentCloudUserId(long enterpriseId, long cloudUserId) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("enterpriseId", enterpriseId);
		map.put("cloudUserId", cloudUserId);
		return (Department) sqlMapClientTemplate.queryForObject("Department.getByEnterpriseIdAndDepartmentCloudUserId", map);
	}

	//根据用户cloudUserID，查询所在部门的CloudUserId列表
	public List<Long> getDeptCloudUserIdByCloudUserId(long enterpriseId, long cloudUserId, long accountId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("tableSuffix", getTableSuffix(accountId));
		map.put("enterpriseId", enterpriseId);
		map.put("cloudUserId", cloudUserId);
		map.put("accountId", accountId);

		return sqlMapClientTemplate.queryForList("Department.getDeptCloudUserIdByCloudUserId", map);
	}

	public static String getTableSuffix(long accountId) {
		String tableSuffix;
		int table = (int) (HashTool.apply(String.valueOf(accountId)) % TABLE_COUNT);
		tableSuffix = "_" + table;
		return tableSuffix;
	}

	@Override
	public List<UserAccount> getUsersByDept(long deptCloudUserId, String enterpriseId, Long accountId) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("tableSuffix_enterpriseUser", getTableSuffix(Long.parseLong(enterpriseId)));
		map.put("tableSuffix_userAccount", getTableSuffix(accountId));
		map.put("accountId", accountId);
		map.put("deptCloudUserId", deptCloudUserId);
		map.put("enterpriseId", enterpriseId);
		List<UserAccount> userList = sqlMapClientTemplate.queryForList("Department.getUsersByDept",map);
		return userList;
	}

	@Override
	public List<DepartmentAccount> listUserDepts(long enterpriseUserId, Long enterpriseId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("enterpriseUserId", enterpriseUserId);
		map.put("enterpriseId", enterpriseId);
		return sqlMapClientTemplate.queryForList("Department.listUserDepts",map);
	}

	@Override
	public List<EnterpriseSecurityPrivilege> listPrivilege(EnterpriseSecurityPrivilege filter) {
		return sqlMapClientTemplate.queryForList("Department.listPrivilege",filter);
	}

	@Override
	public long getUserCloudIdByEnterpriseUserId(long enterpriseUserId, long accountId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("tableSuffix_userAccount", getTableSuffix(accountId));
		map.put("userId", enterpriseUserId);
		map.put("accountId", accountId);
		return (long)sqlMapClientTemplate.queryForObject("Department.getUserCloudIdByEnterpriseUserId",map);
	}
	

	@Override
	public void updateEnterpriseUserStatus(long enterpriseUserId, Long enterpriseId,byte status) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("tableSuffix", getTableSuffix(enterpriseId));
		map.put("id", enterpriseUserId);
		map.put("enterpriseId", enterpriseId);
		map.put("status", status);
		sqlMapClientTemplate.update("Department.updateStatus",map);
	}

	@Override
	public UserAccount getUserAccountByCloudUserId(long cloudUserId, long accountId) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("tableSuffix_userAccount", getTableSuffix(accountId));
		map.put("cloudUserId", cloudUserId);
		map.put("accountId", accountId);
		return (UserAccount)sqlMapClientTemplate.queryForObject("Department.getUserAccountByCloudUserId",map);
	}


}
