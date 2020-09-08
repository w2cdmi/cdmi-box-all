package com.huawei.sharedrive.uam.user.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationToken;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.exception.AuthFailedException;
import com.huawei.sharedrive.uam.exception.UserLockedWebException;
import com.huawei.sharedrive.uam.user.domain.Admin;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.log.SystemLog;

public interface AdminService {
    long getAll(Admin filter);

    void updateStatus(Byte status, Long id);

    Admin getAdminByLoginName(String loginName);

    // 根据登录名和企业ID查询管理员（不同企业下可能会存在重名）
    Admin getByLoginNameAndEnterpriseId(String loginName, long enterpriseId);

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
    void changeLoginName(Admin admin);

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
    Admin login(String enterpriseName, String userName, String password, SystemLog systemLog, AuthenticationToken authcToken) throws AuthFailedException, UserLockedWebException;

    /**
     * 
     * @param admin
     */
    void updateAdmin(Admin admin);

    /**
     * 
     * @param admin
     * @throws UserLockedException
     */

    /**
     * 
     * @param admin
     * @param ldapUser
     */
    void synLdapAccountInfo(Admin admin, Admin ldapUser);

    /**
     * Get admin by email
     */
    Admin getByEmail(String email);

}
