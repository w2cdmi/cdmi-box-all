package pw.cdmi.box.uam.user.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.exception.AuthFailedException;
import pw.cdmi.box.uam.exception.UserLockedWebException;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.common.log.SystemLog;

public interface AdminService
{
    long getAll(Admin filter);
    
    void updateStatus(Byte status, Long id);
    
    Admin getAdminByLoginName(String loginName);
    
    /**
     * 
     * @param ids
     * @return
     */
    List<String> getNameByIds(Long[] ids);
    
    /**
     * 
     * @param ids
     * @return
     */
    List<Admin> getAdminByIds(Long[] ids);
    
    /**
     * 
     * @param ids
     * @return
     */
    List<Admin> getAdminExcludeIds(Long[] ids);
    
    List<Admin> getAllAdmin();
    
    /**
     * 
     * @param objectSID
     * @return
     */
    Admin getAdminByObjectSID(String objectSID);
    
    /**
     * 
     * @param admin
     * @param pageRequest
     * @return
     */
    Page<Admin> getPagedAdmins(Admin admin, PageRequest pageRequest);
    
    Page<Admin> getFilterd(Admin filter, PageRequest pageRequest);
    
    Admin get(Long id);
    
    /**
     * 
     * @param user
     */
    void create(Admin admin);
    
    void delete(long id);
    
    /**
     * 
     * @param id
     * @param validateKey
     * @param dynamicPwd
     */
    void updateValidKeyAndDynamicPwd(long id, String validateKey, String dynamicPwd);
    
    /**
     * 
     * @param id
     * @param email
     */
    void updateEmail(long id, String email);
    
    /**
     * 
     * @param admin
     */
    void changeName(Admin admin);
    
    /**
     * 
     * @param admin
     */
    void changeAdminPwd(Admin admin, HttpServletRequest req);
    
    /**
     * 
     * @param id
     * @param password
     */
    void resetAdminPwd(long id, String password);
    
    /**
     * 
     * @param id
     * @param password
     */
    void initSetAdminPwd(long id, String password);
    
    /**
     * 
     * @param admin
     */
    void changeAdminPwdByInitLogin(Admin admin, HttpServletRequest req, String loginIP);
    
    /**
     * 
     * @param userName
     * @param password
     */
    Admin login(String userName, String password, SystemLog systemLog, String loginIP)
        throws AuthFailedException, UserLockedWebException;
    
    /**
     * 
     * @param admin
     */
    void updateAdmin(Admin admin);
    
    void deleteUserLocked(String loginName);
    
    /**
     * 
     * @param userId
     * @throws AuthFailedException
     */
    void addUserLocked(String loginName, SystemLog systemLog);
    
    void synLdapAccountInfo(Admin admin, Admin ldapUser);
    
    void checkUserLocked(String userName, SystemLog systemLog);
    
    boolean isCurrentPassword(String oldPassword);
}
