package com.huawei.sharedrive.uam.organization.dao.impl;

import com.huawei.sharedrive.uam.organization.dao.DepartmentDao;
import com.huawei.sharedrive.uam.organization.domain.Department;
import org.springframework.stereotype.Service;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "unchecked", "deprecation" })
@Service
public class DepartmentDaoImpl extends CacheableSqlMapClientDAO implements DepartmentDao {

	@Override
	public Long createDept(Department departmentInfo) {
		sqlMapClientTemplate.insert("Department.addDept", departmentInfo);
		return departmentInfo.getDepartmentId();
	}

	@Override
	public void updateDept(Department dept) {
		sqlMapClientTemplate.update("Department.updateDept", dept);
	}

	public void updateStateByEnterpriseId(Department dept) {
		sqlMapClientTemplate.update("Department.updateStateByEnterpriseId", dept);
	}

	@Override
	public Department getById(long enterpriseId, long departmentId) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("enterpriseId", enterpriseId);
		map.put("departmentId", departmentId);
		return (Department) sqlMapClientTemplate.queryForObject("Department.getDeptById", map);
	}

	@Override
	public List<Department> getByEnterpriseId(Long enterpriseid) {
		return sqlMapClientTemplate.queryForList("Department.queryByEnterpriseId", enterpriseid);
	}

	@Override
	public Department getEnpDeptByNameAndParent(Long enterpriseid, String name, Long pId) {

		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("enterpriseId", enterpriseid);
		map.put("name", name);
		map.put("parentId", pId);
		return (Department) sqlMapClientTemplate.queryForObject("Department.getEnpDeptByNameAndParent", map);
	}

	@Override
	public List<Department> listRootDeptByEnterpriseId(Long enterpriseId) {
		return listDepByParentDepId(-1, enterpriseId);
	}

	@Override
	public List<Department> listAllDeptByEnterpriseId(long enterpriseId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("enterpriseId", enterpriseId);
		List<Department> list = sqlMapClientTemplate.queryForList("Department.deptList", map);
		return list;
	}

	@Override
	public List<Department> listDepByParentDepId(long parentDepId, long enterpriseId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("enterpriseId", enterpriseId);
		map.put("parentId", parentDepId);
		List<Department> list = sqlMapClientTemplate.queryForList("Department.listDepByParentDepId", map);
		return list;
	}

	@Override
	public long queryMaxExecuteRecordId() {
		Object maxId = sqlMapClientTemplate.queryForObject("Department.getMaxId");
		if (maxId == null) {
			return 0L;
		}
		return (Long) maxId;
	}

	@Override
	public long getMaxDepartmentIdInEnterprise(long enterpriseId) {
		Object maxId = sqlMapClientTemplate.queryForObject("Department.getMaxDepartmentIdInEnterprise",enterpriseId);
		if (maxId == null) {
			return 0L;
		}

		return (Long) maxId;
	}

	@Override
	public void deleteDept(Department department) {
		sqlMapClientTemplate.delete("Department.deleteDept", department);
	}

	@Override
	public List<Long> listDeptIdByParentDepId(long parentDepId, long enterpriseId) {
		Map<String,Long> map = new HashMap<String,Long>(3);
		map.put("enterpriseId", enterpriseId);
		map.put("parentId", parentDepId);

		List<Long> list = sqlMapClientTemplate.queryForList("Department.listDeptIdByParentDepId",map);
		return list;
	}

	@Override
	public List<Department> getByEnterpriseIdAndState(long enterpriseId, byte state) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("enterpriseId", enterpriseId);
		map.put("state", state);

		List<Department> list = sqlMapClientTemplate.queryForList("Department.getByEnterpriseIdAndState", map);
		return list;
	}

	//根据用户ID，查询所在部门对应的CloudUserId列表
	public List<Long> getDeptCloudUserIdByUserId(long enterpriseId, long enterpriseUserId, long accountId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("enterpriseId", enterpriseId);
		map.put("enterpriseUserId", enterpriseUserId);
		map.put("accountId", accountId);

		return sqlMapClientTemplate.queryForList("Department.getDeptCloudUserIdByUserId", map);
	}

	//获取某个用户所在的部门数量
	@Override
	public int countByEnterpriseUserId(long enterpriseId, long enterpriseUserId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("enterpriseId", enterpriseId);
		map.put("enterpriseUserId", enterpriseUserId);

		return (Integer)sqlMapClientTemplate.queryForObject("Department.countByEnterpriseUserId", map);
	}

	//获取某个用户所在的部门列表
	@Override
	public List<Department> getByEnterpriseUserId(long enterpriseId, long enterpriseUserId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("enterpriseId", enterpriseId);
		map.put("enterpriseUserId", enterpriseUserId);

		return sqlMapClientTemplate.queryForList("Department.getByEnterpriseUserId", map);
	}

	@Override
	public List<Department> search(long enterpriseId, String name) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("enterpriseId", enterpriseId);
		map.put("name", name);
		return sqlMapClientTemplate.queryForList("Department.search",map);
	}
}
