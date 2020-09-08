package com.huawei.sharedrive.uam.organization.service;

import com.huawei.sharedrive.uam.organization.domain.EnterpriseUserDept;

import java.util.List;

public interface EnterpriseUserDeptService {
	public List<EnterpriseUserDept> getByEnterpriseIdAndDeptId(long enterpriseId, long deptId);

	List<EnterpriseUserDept> getByEnterPriseUser(EnterpriseUserDept enterpriseUserDept);

	public void create(EnterpriseUserDept userDept);

	public void deleteByEnterpriseId(long enterpriseId);

	public void deleteByDepartmentId(long enterpriseId, long departmentId);

	public void deleteByEnterpriseUserId(long enterpriseId, long enterpriseUserId);

	public void deleteByDepartmentIdAndEnterpriseUserId(long enterpriseId, long departmentId, long enterpriseUserId);

	//因为存在部门不可见的情况，所以删除不可见的部门，如果用户所有的部门都不可见，增加一个根部门(id=0)
	public void fixDepartmentId(long enterpriseId, long userId);

    void update(EnterpriseUserDept userDept);
}
