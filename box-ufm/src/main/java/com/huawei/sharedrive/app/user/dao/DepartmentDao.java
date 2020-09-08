package com.huawei.sharedrive.app.user.dao;

import com.huawei.sharedrive.app.user.domain.*;

import java.util.List;

public interface DepartmentDao {
    //根据部门Id查询部门信息
    Department getByEnterpriseIdAndDepartmentId(long enterpriseId, long cloudUserId);

    //根据部门的cloudUserId查询部门信息
    Department getByEnterpriseIdAndDepartmentCloudUserId(long enterpriseId, long cloudUserId);

    //根据用户ID，查询所在部门对应的CloudUserId列表
    List<Long> getDeptCloudUserIdByCloudUserId(long enterpriseId, long enterpriseUserId, long accountId);

	List<UserAccount> getUsersByDept(long deptId, String enterpriseId, Long accountId);

    List<DepartmentAccount> listUserDepts(long enterpriseUserId, Long enterpriseId);

    List<EnterpriseSecurityPrivilege> listPrivilege(EnterpriseSecurityPrivilege filter);

    long getUserCloudIdByEnterpriseUserId(long enterpriseUserId, long accountId);
    
    UserAccount getUserAccountByCloudUserId(long cloudUserId, long accountId);

	void updateEnterpriseUserStatus(long enterpriseUserId, Long enterpriseId, byte status);
}
