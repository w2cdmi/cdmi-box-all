package com.huawei.sharedrive.app.user.service;

import com.huawei.sharedrive.app.user.domain.*;

import java.util.List;

public interface DepartmentService {
	//根据部门id，查询部门信息
	Department getByEnterpriseIdAndDepartmentId(long enterpriseId, long departmentId);

	//根据部门cloudUserId，查询部门信息
	Department getByEnterpriseIdAndDepartmentCloudUserId(long enterpriseId, long departmentCloudUserId);

	//根据用户cloudUserId，查询所在部门对应的CloudUserId列表
	List<Long> getDeptCloudUserIdByCloudUserId(long enterpriseId, long enterpriseUserId, long accountId);

	List<UserAccount> getUsersByDept(long userId, String enterpriseId, Long accountId);

    List<DepartmentAccount> listUserDepts(long id, Long enterpriseId);

	List<EnterpriseSecurityPrivilege> listPrivilege(EnterpriseSecurityPrivilege filter);

    long getUserCloudIdByEnterpriseUserId(long id, long id1);
    
    UserAccount getUserAccountByCloudUserId(long cloudUserId, long accountId);
    
    void updateEnterpriseUserStatus(long enterpriseUserId, Long enterpriseId, byte status);
}
