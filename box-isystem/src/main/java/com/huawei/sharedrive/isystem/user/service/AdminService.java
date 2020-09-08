/**
 * 绠＄悊鐢ㄦ埛鏈嶅姟绫�
 */
package com.huawei.sharedrive.isystem.user.service;

import java.util.List;

import com.huawei.sharedrive.isystem.exception.AuthFailedException;
import com.huawei.sharedrive.isystem.exception.UserLockedException;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.domain.UserLocked;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.log.UserLog;

/**
 * @author d00199602
 * 
 */
public interface AdminService
{
    void addUserLocked(String userName, UserLog userLog);
    
    void checkAnonUserLocked(String loginName, UserLog userLog);
    
    void checkExistUserLocked(String userName, UserLog userLog) throws UserLockedException;
    
    void checkUserLocked(String username, UserLog userLog);
    
    void create(Admin admin);
    
    void delete(long id);
    
    Admin get(Long id);
    
    Admin getAdminByLoginName(String loginName);
    
    List<Admin> getFilterd(Admin filter, Order order, Limit limit);
    
    Page<Admin> getPagedAdmins(Admin admin, PageRequest pageRequest);
    
    void initSetAdminPwd(long id, String password);
    
    void isLockUser(String username, UserLocked userLocked, UserLog userLog);
    
    Admin login(String userName, String password, String loginIP) throws AuthFailedException;
    
    void resetAdminPwd(long id, String password);
    
}
