package com.huawei.sharedrive.uam.enterpriseuser.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.dto.EnterpriseUserStatus;

import com.huawei.sharedrive.uam.organization.domain.Department;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface EnterpriseUserDao
{
    long getMaxUserId();
    
    long create(EnterpriseUser enterpriseUser);
    
    boolean isExistEmailEnterpriseId(String email, String enterpriseId);
    
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
    
    /**
     * 更新状态
     * @param userIds
     * @param status
     * @param enterpriseId
     */
    void updateStatus(List<Long> userIds, EnterpriseUserStatus status, Long enterpriseId); 
	List<EnterpriseUser> getAllEnterpriseUser(Long enterpriseId);

	List<EnterpriseUser> getByEnterpriseAndDepartment(long enterpriseId, long depId);

	void updateDepartmentById(long id, long enterpriseId,long depId);

    List<EnterpriseUser> getByEnterpriseIdAndDeptIdAndType(Long enterpriseId, Long deptId, Byte type);

    void updateTypeById(long enterpriseId, long id, byte type);

    void updateTypeByDept(long enterpriseId, long dept, byte type);

    void changeTypeInDept(long enterpriseId, long dept, byte oldValue, byte newValue);

    EnterpriseUser getByEnterpriseIdAndName(long enterpriseId, String name);

	int getFilterdCount(String filter, Long authServerId, long enterpriseId, Long departmentId, long type, String status);

	List<EnterpriseUser> getFilterd(String filter, Long authServerId, Long departmentId, long enterpriseId, Order order, Limit limit, long type,String status);

	void changeStatus(EnterpriseUser user);

	List<EnterpriseUser> getByEnterpriseIdAndStatus(long enterpriseId, byte status);

    //获取某个部门下的用户信息
    List<EnterpriseUser> getByDeptId(long enterpriseId, long deptId);
}
