package com.huawei.sharedrive.uam.organization.dao;

import java.util.List;

import com.huawei.sharedrive.uam.organization.domain.Department;

public interface DepartmentDao {

	Long createDept(Department department);

    void updateDept(Department dept);

	void updateStateByEnterpriseId(Department dept);
	
	void deleteDept(Department department);

	Department getById(long enterpriseId, long departmentId);

	List<Department> getByEnterpriseId(Long enterpriseId);

	Department getEnpDeptByNameAndParent(Long enterpriseid, String name, Long pId);

	long queryMaxExecuteRecordId();

	/*获取企业下最大的ID，用于departmentId自增*/
	long getMaxDepartmentIdInEnterprise(long enterpriseId);

	List<Department> listRootDeptByEnterpriseId(Long enterpriseId);

	List<Department> listAllDeptByEnterpriseId(long enterpriseId);

	List<Department> listDepByParentDepId(long parentDepId, long enterpriseId);
	
	List<Long> listDeptIdByParentDepId(long parentDepId, long enterpriseId);

	List<Department> getByEnterpriseIdAndState(long enterpriseId, byte state);

	//根据用户ID，查询所在部门对应的CloudUserId列表
	List<Long> getDeptCloudUserIdByUserId(long enterpriseId, long enterpriseUserId, long accountId);

	//获取某个用户所在的部门数据
	int countByEnterpriseUserId(long enterpriseId, long enterpriseUserId);

	//获取某个用户所在的部门列表
	List<Department> getByEnterpriseUserId(long enterpriseId, long enterpriseUserId);

	List<Department> search(long enterpriseId, String name);
}
