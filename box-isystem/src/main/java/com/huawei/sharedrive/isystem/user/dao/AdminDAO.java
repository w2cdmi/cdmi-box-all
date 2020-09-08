/**
 * 
 */
package com.huawei.sharedrive.isystem.user.dao;

import java.util.List;
import java.util.Set;

import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.domain.AdminRole;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.encrypt.HashPassword;

/**
 * @author q90003805
 * 
 */
public interface AdminDAO
{
    void updateStatus(Byte status, Long id);
    
    Admin get(Long id);
    
    Admin getByLoginName(String loginName);
    
    List<Admin> getFilterd(Admin filter, Order order, Limit limit);
    
    int getFilterdCount(Admin filter);
    
    void delete(Long id);
    
    void create(Admin admin);
    
    void update(Admin admin);
    
    long getNextAvailableAdminId();
    
    void updateValidKeyAndDynamicPwd(long id, String validateKey, String dynamicPwd);
    
    void updateEmail(long id, String email);
    
    void updatePassword(long id, HashPassword hashPassword);
    
    void updateName(long id, String loginName);
    
    void updateLastLoginTime(long id);
    
    void updateLastLoginIP(long id, String loginIP);
    
    void cleanLastLoginTime(long id);
    
    void updateRoles(long id, Set<AdminRole> roles);
    
    List<Admin> getByRole(AdminRole role);
    
    Admin getByLoginNameWithoutCache(String userName);
}