package com.huawei.sharedrive.uam.organization.manager;

import java.util.List;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUserExtend;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.domain.DeptNode;

import pw.cdmi.box.domain.Page;

public interface DepartmentManager {
	Long create(Department DepartmentInfo);
	
	Page<EnterpriseUserExtend> delete(Long departmentId,long enterpriseId,long authServerId);

	List<DeptNode> listDeptTreeByEnterpriseId(Long enterpriseId);

	Department getDepartmentByPath(long enterpriseId, String deptPath);

	List<DeptNode> getAllByEnterpriseId(long id, long accountId);
	public void update(Department dept);
	Department getDeptById(long enterpriseId, long id);
	List<DeptNode> getRootDepartment(long enterpriseId);
	List<Department> listDepByParentDepId(long parentDepId,long enterpriseId);

	List<Department> search(long enterpriseId, String name);
}
