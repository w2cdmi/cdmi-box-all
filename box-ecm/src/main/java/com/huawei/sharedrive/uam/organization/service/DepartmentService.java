package com.huawei.sharedrive.uam.organization.service;

import java.util.List;

import com.huawei.sharedrive.uam.organization.domain.Department;

public interface DepartmentService {
	Long create(Department DepartmentInfo);
	
	void delete(long enterpriseId, long departmentId);
	
	void update(Department dept);

	void changeState(long enterpriseId, byte state);

	List<Department> listDeptTreeByEnterpriseId(Long enterpriseId);

	Department getEnpDeptByNameAndParent(Long enterpriseid, String name, Long pId);

	Department getByEnterpriseIdAndDepartmentId(long enterpriseId, long departmentId);
	
	List<Department> listRootDepartmentByEnterpriseId(Long enterpriseId);
	
	List<Department> listAllDepartmentByEnterpriseId(long enterpriseId);

	List<Department> listDepByParentDepId(long parentDepId, long enterpriseId);

	List<Department> getByEnterpriseIdAndState(long enterpriseId, byte state);
	
	List<Long> listDeptIdByParentDepId(long parentDepId, long enterpriseId);

	//返回从当前剖部门（deptId)到最高部门的ID列表。如果指定的部门不存在，返回空列表。
	List<Long> listDeptHierarchyOfDept(long enterpriseId, long deptId);

	//如果企业微信通讯录没有设置全部范围可见，同步后的部分部门，其他父部门不可见，需要设置为0，让其他作为root部门显示。
	void fixParentId(long enterpriseId);

	//根据用户ID，查询所在部门对应的CloudUserId列表
	List<Long> getDeptCloudUserIdByUserId(long enterpriseId, long enterpriseUserId, long accountId);

	//获取某个用户所在的部门数据
	int countByEnterpriseUserId(long enterpriseId, long enterpriseUserId);

	//获取某个用户所在的部门列表
	List<Department> getByEnterpriseUserId(long enterpriseId, long enterpriseUserId);

	List<Department> search(long enterpriseId, String name);

}
