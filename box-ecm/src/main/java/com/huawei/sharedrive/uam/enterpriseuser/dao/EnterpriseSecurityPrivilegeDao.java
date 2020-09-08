package com.huawei.sharedrive.uam.enterpriseuser.dao;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseSecurityPrivilege;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.dto.EnterpriseUserStatus;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import java.util.List;

public interface EnterpriseSecurityPrivilegeDao
{
    long getMaxId();
    
    long create(EnterpriseSecurityPrivilege privilege);

    void update(EnterpriseSecurityPrivilege privilege);

    void deleteBy(long enterpriseId);
    
    void deleteBy(long enterpriseId, long departmentId);

    void deleteBy(long enterpriseId, long departmentId, byte role);

    List<EnterpriseSecurityPrivilege> getIdByEnterpriseAndDepartmentAndRole(long enterpriseId, long departmentId, byte role);

    List<EnterpriseUser> getUserByEnterpriseAndDepartmentAndRole(long enterpriseId, long departmentId, byte role);

    List<EnterpriseUser> getByEnterpriseAndRole(long enterpriseId, byte role);

    int countWithFilter(long enterpriseId, Long deptId, String authServerId, String filter, byte role);

    List<EnterpriseUser> getWithFilter(long enterpriseId, Long deptId, String authServerId, String filter, byte role, Order order, Limit limit);

	void deleteByDeptAndRole(long enterpriseId, long departmentId, byte role);

	void delete(long enterpriseId, long enterpriseUserId, byte roleType);

	EnterpriseSecurityPrivilege get(EnterpriseSecurityPrivilege securityPrivilege);

	List<EnterpriseSecurityPrivilege> listSecurityPrivilege(EnterpriseSecurityPrivilege filter, Limit limit);

	void deletePrivilege(EnterpriseSecurityPrivilege enterpriseSecurityPrivilege);

	int listSecurityPrivilegeTotal(EnterpriseSecurityPrivilege filter, Limit limitObj);
}
