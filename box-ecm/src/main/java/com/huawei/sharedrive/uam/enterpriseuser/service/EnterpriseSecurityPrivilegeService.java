package com.huawei.sharedrive.uam.enterpriseuser.service;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseSecurityPrivilege;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import java.util.List;

public interface EnterpriseSecurityPrivilegeService {
    long create(EnterpriseSecurityPrivilege privilege);

    void update(EnterpriseSecurityPrivilege privilege);

    void delete(long id);

    void deleteBy(long enterpriseId);

    void deleteBy(long enterpriseId, long departmentId);

    void deleteBy(long enterpriseId, long departmentId, byte role);

    List<EnterpriseUser> listInfoSecurityManager(long enterpriseId);

    void setInfoSecurityManager(long enterpriseId, long enterpriseUserId,long deptId);

    int countWithFilter(long enterpriseId, Long deptId, String authServerId, String filter,byte roleType);

    List<EnterpriseUser> getWithFilter(long enterpriseId, Long deptId, String authServerId, String filter, Order order, Limit limit,byte roleType);


	void deletePrivilegeOwner(long enterpriseId, long enterpriseUserId, byte roleType);

	EnterpriseSecurityPrivilege get(EnterpriseSecurityPrivilege securityPrivilege);

	List<EnterpriseSecurityPrivilege> listSecurityPrivilege(EnterpriseSecurityPrivilege filter, Limit limit);

	EnterpriseSecurityPrivilege getDeptDirector(long enterpriseId, long deptId);

	void addDeptDirector(long enterpriseId, long deptId, long enterpriseUserId);

	void addArchiveOwner(long enterpriseId, long deptId, long enterpriseUserId,UserToken userToken);

	void deletePrivilege(EnterpriseSecurityPrivilege enterpriseSecurityPrivilege);

	int listSecurityPrivilegeTotal(EnterpriseSecurityPrivilege filter, Limit limitObj);
}
