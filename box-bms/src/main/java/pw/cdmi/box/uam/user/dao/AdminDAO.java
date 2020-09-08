package pw.cdmi.box.uam.user.dao;

import java.util.List;
import java.util.Set;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.domain.AdminRole;
import pw.cdmi.core.encrypt.HashPassword;

public interface AdminDAO
{
    void updateStatus(Byte status, Long id);
    
    Admin get(Long id);
    
    Admin getByLoginName(String loginName);
    
    Admin getAdminByObjectSID(String objectSID);
    
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
    
    void updateRoles(long id, Set<AdminRole> roles);
    
    List<Admin> getByRole(AdminRole role);
    
    List<Admin> getAdminByIds(Long[] ids);
    
    List<Admin> getAdminExcludeIds(Long[] ids);
    
    Admin getByLoginNameWithoutCache(String loginName);
}