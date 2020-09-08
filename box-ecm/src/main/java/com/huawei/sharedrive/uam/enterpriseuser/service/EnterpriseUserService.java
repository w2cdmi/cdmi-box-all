package com.huawei.sharedrive.uam.enterpriseuser.service;

import java.util.List;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseSecurityPrivilege;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.dto.EnterpriseUserStatus;

import com.huawei.sharedrive.uam.organization.domain.Department;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface EnterpriseUserService
{
    
    long create(EnterpriseUser enterpriseUser);
    
    List<EnterpriseUser> getFilterd(String filter, Long authServerId,Long departmentId, Long enterpriseId, Order order,
        Limit limit);
    
    int getFilterdCount(String filter, Long authServerId, Long enterpriseId,Long departmentId);
    
    EnterpriseUser get(long userId, long enterpriseId);
    
    EnterpriseUser getUserInfo(long userId, long enterpriseId, String authType);
    
    EnterpriseUser getByObjectSid(String objectSid, long enterpriseId);
    
    void update(EnterpriseUser enterpriseUser);
    
    void updateEnterpriseUser(EnterpriseUser enterpriseUser);
    
    List<EnterpriseUser> getAccountUser(long accountId, long enterpriseId, long userSource, String filter,
        String ids);
    
    void deleteByIds(long enterpriseId, String ids);
    
    void deleteById(long enterpriseId, Long id);
    
    List<EnterpriseUser> getAllADEnterpriseUser(Long enterpriseId, Long authServerId);
    
    void updateLdapStatus(List<EnterpriseUser> list, Byte ldapStatus, Long enterpriseId);
    
    int getByLdapStatusCount(Byte ldapStatus, Long enterpriseId);
    
    List<EnterpriseUser> getByLdapStatus(Byte ldapStatus, Long enterpriseId, Limit limit);
    
    void updateLdapStatusByNotIn(List<EnterpriseUser> list, Byte ldapStatus, Long enterpriseId);
    
    void updateStatus(List<Long> userIds, EnterpriseUserStatus status, Long enterpriseId);
	
	List<EnterpriseUser> getAllEnterpriseUser(long enterpriseId);

    void changeDepartment(long enterpriseUserId, long enterpriseId, long deptId);

    EnterpriseUser getByEnterpriseIdAndName(long enterpriseId, String name);

    //重置用户密码
    EnterpriseUser resetPassword(long enterpriseId, long enterpriseUserId);

	int getFilterdCount(String filter, Long authServerId, long enterpriseId, Long departmentId, long type, String status);

	List<EnterpriseUser> getFilterd(String filter, Long authServerId, Long departmentId, long enterpriseId, Order order, Limit limit, long type, String status);

	void changeStatus(long enterpriseId, byte status);

    List<EnterpriseUser> getByEnterpriseIdAndStatus(long enterpriseId, byte status);

    //获取某个部门下的用户信息
    List<EnterpriseUser> getByDeptId(long enterpriseId, long deptId);

}

