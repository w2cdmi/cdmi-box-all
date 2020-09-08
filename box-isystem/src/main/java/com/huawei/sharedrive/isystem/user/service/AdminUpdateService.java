package com.huawei.sharedrive.isystem.user.service;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.domain.AdminRole;

/**
 * @author d00199602
 * 
 */
public interface AdminUpdateService
{
    void changeAdminPwd(Admin admin, HttpServletRequest request);
    
    void changeAdminPwdByInitLogin(Admin admin, HttpServletRequest request, String loginIP);
    
    void changeName(Admin admin);
    
    void changePwdBySuperAdmin(Admin admin);
    
    void initSetAdminPwd(long id, String password);
    
    void resetAdminPwd(long id, String password);
    
    void updateEmail(long id, String email);
    
    void updateRoles(long id, Set<AdminRole> roles);
    
    void updateStatus(Byte status, Long id);
    
    void updateValidKeyAndDynamicPwd(long id, String validateKey, String dynamicPwd);
    
    void updateLastLoginTime(long id);
    
}
