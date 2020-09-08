/**
 * 
 */
package com.huawei.sharedrive.isystem.user.service.impl;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.exception.OldPasswordErrorException;
import com.huawei.sharedrive.isystem.exception.PasswordInvalidException;
import com.huawei.sharedrive.isystem.exception.PasswordSameException;
import com.huawei.sharedrive.isystem.exception.ShaEncryptException;
import com.huawei.sharedrive.isystem.user.dao.AdminDAO;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.domain.AdminRole;
import com.huawei.sharedrive.isystem.user.service.AccountRetryCountService;
import com.huawei.sharedrive.isystem.user.service.AdminUpdateService;
import com.huawei.sharedrive.isystem.util.PasswordValidateUtil;

import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.HashPasswordUtil;

/**
 * @author d00199602
 * 
 */
@Component
public class AdminUpdateServiceImpl implements AdminUpdateService
{
    private static Logger logger = LoggerFactory.getLogger(AdminUpdateServiceImpl.class);
    
    @Autowired
    private AccountRetryCountService accountRetryCountService;
    
    @Autowired
    private AdminDAO adminDAO;
    
    @Autowired
    private ApplicationContext context;
    
    private AdminUpdateService proxySelf;
    
    @Override
    public void changeAdminPwd(Admin inputAdmin, HttpServletRequest request)
    {
        accountRetryCountService.checkUserLocked(inputAdmin.getLoginName(), request);
        if (!PasswordValidateUtil.isValidPassword(inputAdmin.getPassword()))
        {
            accountRetryCountService.addUserLocked(inputAdmin.getLoginName());
            throw new PasswordInvalidException();
        }
        long adminId = inputAdmin.getId();
        Admin admin = adminDAO.get(adminId);
        HashPassword hashPassword = new HashPassword();
        hashPassword.setHashPassword(admin.getPassword());
        hashPassword.setIterations(admin.getIterations());
        hashPassword.setSalt(admin.getSalt());
        if (!HashPasswordUtil.validatePassword(inputAdmin.getOldPasswd(), hashPassword))
        {
            accountRetryCountService.addUserLocked(inputAdmin.getLoginName());
            throw new OldPasswordErrorException();
        }
        if (HashPasswordUtil.validatePassword(inputAdmin.getPassword(), hashPassword))
        {
            accountRetryCountService.addUserLocked(inputAdmin.getLoginName());
            throw new PasswordSameException();
        }
        
        try
        {
            HashPassword newHashPassword = HashPasswordUtil.generateHashPassword(inputAdmin.getPassword());
            adminDAO.updatePassword(adminId, newHashPassword);
        }
        catch (Exception e)
        {
            logger.error("update faild", e);
            throw new ShaEncryptException(e);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeAdminPwdByInitLogin(Admin inputAdmin, HttpServletRequest request, String loginIP)
    {
        proxySelf.changeAdminPwd(inputAdmin, request);
        proxySelf.updateEmail(inputAdmin.getId(), inputAdmin.getEmail());
        adminDAO.updateLastLoginTime(inputAdmin.getId());
        adminDAO.updateLastLoginIP(inputAdmin.getId(), loginIP);
    }
    
    public void updateLastLoginTime(long id){
    	adminDAO.updateLastLoginTime(id);
    }
    
    @Override
    public void changeName(Admin inputAdmin)
    {
        adminDAO.updateName(inputAdmin.getId(), inputAdmin.getName());
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changePwdBySuperAdmin(Admin inputAdmin)
    {
        if (!PasswordValidateUtil.isValidPassword(inputAdmin.getPassword()))
        {
            throw new PasswordInvalidException();
        }
        try
        {
            adminDAO.updatePassword(inputAdmin.getId(),
                HashPasswordUtil.generateHashPassword(inputAdmin.getPassword()));
        }
        catch (Exception e)
        {
            logger.error("update faild", e);
            throw new ShaEncryptException(e);
        }
        adminDAO.cleanLastLoginTime(inputAdmin.getId());
    }
    
    
    
    
    
    @Override
    public void initSetAdminPwd(long id, String password)
    {
        setAdminPwd(id, password);
        adminDAO.updateLastLoginTime(id);
    }
    
    
    
    @Override
    public void resetAdminPwd(long id, String password)
    {
        setAdminPwd(id, password);
    }
    
    @PostConstruct
    public void setSelf()
    {
        // 浠庝笂涓嬫枃鑾峰彇浠ｇ悊瀵硅薄锛堝鏋滈�杩噋roxtSelf=this鏄笉瀵圭殑锛宼his鏄洰鏍囧璞★級
        proxySelf = context.getBean(AdminUpdateService.class);
    }
    
    
    @Override
    public void updateEmail(long id, String email)
    {
        adminDAO.updateEmail(id, email);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRoles(long id, Set<AdminRole> roles)
    {
        adminDAO.updateRoles(id, roles);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateStatus(Byte status, Long id)
    {
        
        adminDAO.updateStatus(status, id);
    }
    
    @Override
    public void updateValidKeyAndDynamicPwd(long id, String validateKey, String dynamicPwd)
    {
        adminDAO.updateValidKeyAndDynamicPwd(id, validateKey, dynamicPwd);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    private void setAdminPwd(long id, String password)
    {
        if (!PasswordValidateUtil.isValidPassword(password))
        {
            throw new ValidationException();
        }
        try
        {
            adminDAO.updatePassword(id, HashPasswordUtil.generateHashPassword(password));
        }
        catch (Exception e)
        {
            logger.error("update faild", e);
            throw new ShaEncryptException(e);
        }
        adminDAO.updateValidKeyAndDynamicPwd(id, null, null);
    }
    
    
}
