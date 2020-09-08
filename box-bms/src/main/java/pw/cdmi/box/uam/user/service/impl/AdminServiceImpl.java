package pw.cdmi.box.uam.user.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.exception.AuthFailedException;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.exception.OldPasswordErrorException;
import pw.cdmi.box.uam.exception.PasswordInvalidException;
import pw.cdmi.box.uam.exception.PasswordSameException;
import pw.cdmi.box.uam.exception.ShaEncryptException;
import pw.cdmi.box.uam.exception.UserLockedWebException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.user.dao.AdminAppPermissionDAO;
import pw.cdmi.box.uam.user.dao.AdminDAO;
import pw.cdmi.box.uam.user.dao.ManagerLockedDao;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.domain.ManagerLocked;
import pw.cdmi.box.uam.user.service.AccountRetryCountService;
import pw.cdmi.box.uam.user.service.AdminService;
import pw.cdmi.box.uam.user.service.UserLockService;
import pw.cdmi.box.uam.util.PasswordValidateUtil;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.log.SystemLog;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.core.utils.HashPasswordUtil;

@Component
public class AdminServiceImpl implements AdminService
{
    
    @Autowired
    private AdminDAO adminDAO;
    
    @Autowired
    private AdminAppPermissionDAO adminAppPermissionDAO;
    
    @Autowired
    private ManagerLockedDao managerLockedDao;
    
    @Autowired
    private UserLockService userLockService;
    
    @Autowired
    private ApplicationContext context;
    
    private static final int ONE_FAIL_TIME = 1;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    private AdminService proxySelf;
    
    private static Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    
    @Autowired
    private AccountRetryCountService accountRetryCountService;
    
    @Autowired
    private CacheClient cacheClient;
    
    @PostConstruct
    public void setSelf()
    {
        proxySelf = context.getBean(AdminService.class);
    }
    
    @Override
    public List<String> getNameByIds(Long[] ids)
    {
        List<String> list = new ArrayList<String>(10);
        List<Admin> adminList = adminDAO.getAdminByIds(ids);
        if (adminList != null)
        {
            for (Admin admin : adminList)
            {
                list.add(admin.getName());
            }
        }
        return list;
    }
    
    @Override
    public List<Admin> getAdminByIds(Long[] ids)
    {
        return adminDAO.getAdminByIds(ids);
    }
    
    @Override
    public List<Admin> getAdminExcludeIds(Long[] ids)
    {
        return adminDAO.getAdminExcludeIds(ids);
    }
    
    @Override
    public List<Admin> getAllAdmin(){
    	return adminDAO.getFilterd(null, null, null);
    }
    
    @Override
    public Admin getAdminByLoginName(String loginName)
    {
        return adminDAO.getByLoginNameWithoutCache(loginName);
    }
    
    @Override
    public Page<Admin> getPagedAdmins(Admin admin, PageRequest pageRequest)
    {
        return null;
    }
    
    @Override
    public Page<Admin> getFilterd(Admin filter, PageRequest pageRequest)
    {
        int total = adminDAO.getFilterdCount(filter);
        List<Admin> content = adminDAO.getFilterd(filter, pageRequest.getOrder(), pageRequest.getLimit());
        Page<Admin> page = new PageImpl<Admin>(content, pageRequest, total);
        return page;
    }
    
    
    
    @Override
    public long getAll(Admin filter)
    {
        long total = adminDAO.getFilterdCount(filter);
        return total;
    }
    
    @Override
    public Admin get(Long id)
    {
        return adminDAO.get(id);
    }
    
    @Override
    public void create(Admin admin)
    {
        try
        {
            HashPassword hashPassword = HashPasswordUtil.generateHashPassword(admin.getPassword());
            admin.setPassword(hashPassword.getHashPassword());
            admin.setIterations(hashPassword.getIterations());
            admin.setSalt(hashPassword.getSalt());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("hash NoSuchAlgorithmException", e);
            throw new ShaEncryptException("hash NoSuchAlgorithmException", e);
        }
        catch (InvalidKeySpecException e)
        {
            logger.error("hash InvalidKeySpecException", e);
            throw new ShaEncryptException("hash InvalidKeySpecException", e);
        }
        
        long id = adminDAO.getNextAvailableAdminId();
        admin.setId(id);
        Date now = new Date();
        admin.setCreatedAt(now);
        admin.setModifiedAt(now);
        if (0 == admin.getType())
        {
            admin.setType(Admin.ROLE_MANAGER);
        }
        adminDAO.create(admin);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(long id)
    {
        deleteCacheById(id);
        
        adminDAO.delete(id);
        adminAppPermissionDAO.deleteByAdminId(id);
        // refreshSession(id);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateStatus(Byte status, Long id)
    {
        deleteCacheById(id);
        
        adminDAO.updateStatus(status, id);
        // refreshSession(id);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeAdminPwdByInitLogin(Admin inputAdmin, HttpServletRequest req, String loginIP)
    {
        proxySelf.changeAdminPwd(inputAdmin, req);
        adminDAO.updateLastLoginTime(inputAdmin.getId());
        adminDAO.updateLastLoginIP(inputAdmin.getId(), loginIP);
    }
    
    @Override
    public void resetAdminPwd(long id, String password)
    {
        setAdminPwd(id, password);
    }
    
    @Override
    public void initSetAdminPwd(long id, String password)
    {
        setAdminPwd(id, password);
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
    
    @Override
    public void changeName(Admin inputAdmin)
    {
        deleteCacheById(inputAdmin.getId());
        adminDAO.updateName(inputAdmin.getId(), inputAdmin.getName());
    }
    
    @Override
    public void changeAdminPwd(Admin inputAdmin, HttpServletRequest req)
    {
        accountRetryCountService.checkUserLocked(inputAdmin.getLoginName(), req);
        if (!PasswordValidateUtil.isValidPassword(inputAdmin.getPassword()))
        {
            accountRetryCountService.addUserLocked(inputAdmin.getLoginName());
            throw new PasswordInvalidException();
        }
        long adminId = inputAdmin.getId();
        Admin tempAdmin = adminDAO.get(adminId);
        Admin admin = null;
        HashPassword hashPassword = new HashPassword();
        if (tempAdmin != null)
        {
            admin = tempAdmin;
            if (admin.getLoginName().equalsIgnoreCase(inputAdmin.getPassword()))
            {
                logger.error("loginName and password cannot be the same");
                accountRetryCountService.addUserLocked(inputAdmin.getLoginName());
                throw new InvalidParamterException("loginName and password cannot be the same");
            }
            hashPassword.setHashPassword(admin.getPassword());
            hashPassword.setIterations(admin.getIterations());
            hashPassword.setSalt(admin.getSalt());
        }
        
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
    public Admin getAdminByObjectSID(String objectSID)
    {
        return adminDAO.getAdminByObjectSID(objectSID);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAdmin(Admin admin)
    {
        if (null == admin)
        {
            logger.warn("admin is null");
            return;
        }
        
        deleteCacheById(admin.getId());
        
        adminDAO.update(admin);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Admin login(String userName, String password, SystemLog systemLog, String loginIP)
        throws AuthFailedException, UserLockedWebException
    {
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(userName))
        {
            throw new AuthFailedException();
        }
        Admin admin = adminDAO.getByLoginNameWithoutCache(userName);
        
        if (Admin.ROLE_ADMIN == admin.getType() || Admin.ROLE_MANAGER == admin.getType())
        {
            HashPassword hashPassword = new HashPassword();
            hashPassword.setHashPassword(admin.getPassword());
            hashPassword.setIterations(admin.getIterations());
            hashPassword.setSalt(admin.getSalt());
            if (!HashPasswordUtil.validatePassword(password, hashPassword))
            {
                throw new AuthFailedException();
            }
            adminDAO.updateLastLoginIP(admin.getId(), loginIP);
            if (admin.getLastLoginTime() != null)
            {
                adminDAO.updateLastLoginTime(admin.getId());
            }
        }
        else
        {
            throw new AuthFailedException("admin role type is " + admin.getType());
        }
        return admin;
    }
    
    @Override
    public void checkUserLocked(String userName, SystemLog systemLog)
    {
        Admin admin = adminDAO.getByLoginName(userName);
        if (admin == null || (Admin.ROLE_ADMIN != admin.getType() && Admin.ROLE_MANAGER != admin.getType()))
        {
            checkAnonUserLocked(userName, systemLog);
        }
        else
        {
            checkExistUserLocked(userName, systemLog);
        }
    }
    
    @Override
    public void synLdapAccountInfo(Admin admin, Admin ldapUser)
    {
        if (isNotEqual(admin.getLoginName(), ldapUser.getLoginName())
            || isNotEqual(admin.getName(), ldapUser.getName())
            || isNotEqual(admin.getEmail(), ldapUser.getEmail()))
        {
            admin.setLoginName(ldapUser.getLoginName());
            admin.setName(ldapUser.getName());
            admin.setEmail(ldapUser.getEmail());
            
            deleteCacheById(admin.getId());
            adminDAO.update(admin);
        }
    }
    
    private boolean isNotEqual(String src, String target)
    {
        if (src == null && target == null)
        {
            return false;
        }
        if (src != null && target != null && src.equals(target))
        {
            return false;
        }
        return true;
    }
    
    @Override
    public void updateValidKeyAndDynamicPwd(long id, String validateKey, String dynamicPwd)
    {
        adminDAO.updateValidKeyAndDynamicPwd(id, validateKey, dynamicPwd);
    }
    
    @Override
    public void updateEmail(long id, String email)
    {
        adminDAO.updateEmail(id, email);
    }
    
    private void checkAnonUserLocked(String loginName, SystemLog systemLog)
    {
        // TODO Auto-generated method stub
        ManagerLocked anonManagerLocked = null;
        
        anonManagerLocked = (ManagerLocked) cacheClient.getCache(ManagerLockedDao.ANON_ADMIN_KEY + loginName);
        
        if (null != anonManagerLocked)
        {
            if (anonManagerLocked.getLoginFailTimes() >= userLockService.getConfigByLockTimes())
            {
                String msg = "Anonymous user:" + loginName + " has been locked at "
                    + systemLog.getClientAddress();
                logger.error(msg);
                throw new UserLockedWebException(msg);
            }
        }
        throw new AuthFailedException();
        
    }
    
    private void checkExistUserLocked(String loginName, SystemLog systemLog)
    {
        ManagerLocked managerLocked = managerLockedDao.get(loginName);
        if (null == managerLocked)
        {
            return;
        }
        
        if (managerLocked.getLockedAt() != null)
        {
            if (new Date().getTime() - managerLocked.getLockedAt().getTime() > userLockService.getConfigByLockWait())
            {
                logger.error("user unlock");
                
                managerLockedDao.delete(loginName);
                
                SystemLog lockLog = new SystemLog();
                lockLog.setClientAddress(systemLog.getClientAddress());
                lockLog.setClientDeviceName(EnvironmentUtils.getHostName());
                lockLog.setLoginName(systemLog.getLoginName());
                lockLog.setShowName(systemLog.getShowName());
                systemLogManager.saveSuccessLog(lockLog,
                    OperateType.Unlock,
                    OperateDescription.USER_UNLOCK,
                    null,
                    new String[]{lockLog.getLoginName()});
            }
            else
            {
                throw new UserLockedWebException();
            }
        }
        
    }
    
    @Override
    public void deleteUserLocked(String loginName)
    {
        // TODO Auto-generated method stub
        Admin admin = adminDAO.getByLoginName(loginName);
        if (null == admin || (Admin.ROLE_ADMIN != admin.getType() && Admin.ROLE_MANAGER != admin.getType()))
        {
            cacheClient.deleteCache(ManagerLockedDao.ANON_ADMIN_KEY + loginName);
        }
        else
        {
            managerLockedDao.delete(loginName);
        }
        
    }
    
    @Override
    public void addUserLocked(String loginName, SystemLog systemLog)
    {
        Admin admin = adminDAO.getByLoginName(loginName);
        if (admin == null || (Admin.ROLE_ADMIN != admin.getType() && Admin.ROLE_MANAGER != admin.getType()))
        {
            addAnonUserLocked(loginName);
            
        }
        else
        {
            addExistUserLocked(loginName, systemLog);
        }
        
    }
    
    private void addExistUserLocked(String loginName, SystemLog systemLog)
    {
        ManagerLocked managerLocked = managerLockedDao.get(loginName);
        if (null == managerLocked)
        {
            managerLocked = new ManagerLocked();
            managerLocked.setCreatedAt(new Date());
            managerLocked.setLoginFailTimes(ONE_FAIL_TIME);
            managerLocked.setLoginName(loginName);
            managerLockedDao.insert(managerLocked);
        }
        else
        {
            managerLockedDao.addFailTime(managerLocked);
            managerLocked.setLoginFailTimes(managerLocked.getLoginFailTimes() + ONE_FAIL_TIME);
            isLockUser(systemLog, managerLocked);
        }
    }
    
    private void isLockUser(SystemLog systemLog, ManagerLocked managerLocked)
    {
        if (managerLocked.getLoginFailTimes() == userLockService.getConfigByLockTimes())
        {
            logger.error("user locked");
            managerLocked.setLockedAt(new Date());
            managerLockedDao.lock(managerLocked);
            SystemLog lockLog = new SystemLog();
            lockLog.setClientAddress(systemLog.getClientAddress());
            lockLog.setClientDeviceName(EnvironmentUtils.getHostName());
            lockLog.setLoginName(systemLog.getLoginName());
            lockLog.setShowName(systemLog.getShowName());
            lockLog.setOperateResult(true);
            systemLogManager.saveSuccessLog(lockLog,
                OperateType.Lock,
                OperateDescription.USER_LOCK,
                null,
                new String[]{lockLog.getLoginName()});
        }
    }
    
    private void addAnonUserLocked(String loginName)
    {
        ManagerLocked managerLocked = (ManagerLocked) cacheClient.getCache(ManagerLockedDao.ANON_ADMIN_KEY
            + loginName);
        if (null == managerLocked)
        {
            ManagerLocked newManagerLocked = new ManagerLocked();
            newManagerLocked.setCreatedAt(new Date());
            newManagerLocked.setLoginFailTimes(ONE_FAIL_TIME);
            newManagerLocked.setLoginName(loginName);
            cacheClient.setCache(ManagerLockedDao.ANON_ADMIN_KEY + loginName,
                newManagerLocked,
                userLockService.getConfigByLockWait());
        }
        else
        {
            managerLocked.setLoginFailTimes(managerLocked.getLoginFailTimes() + ONE_FAIL_TIME);
            cacheClient.replaceCache(ManagerLockedDao.ANON_ADMIN_KEY + loginName,
                managerLocked,
                userLockService.getConfigByLockWait());
        }
    }
    
    private void deleteCacheById(long id)
    {
        Admin admin = get(id);
        if (null == admin)
        {
            logger.warn("admin by id [ {} ] is not exists.", id);
            return;
        }
        deleteCache(admin);
    }
    
    private void deleteCache(Admin admin)
    {
        if (null == admin)
        {
            return;
        }
        String idKey = Admin.CACHE_KEY_PREFIX_ID + admin.getId();
        String nameKey = Admin.CACHE_KEY_PREFIX_LOGINNAME + StringUtils.trimToEmpty(admin.getLoginName());
        
        logger.info("delete admin cache by id [ {} ], loginName [ {} ]", admin.getId(), admin.getLoginName());
        
        if (!cacheClient.deleteCache(idKey))
        {
            logger.warn("delete admin cache by id [ {} ] failed.", admin.getId());
        }
        
        if (!cacheClient.deleteCache(nameKey))
        {
            logger.warn("delete admin cache by loginName [ {} ] failed.", admin.getLoginName());
        }
    }
    
    @Override
    public boolean isCurrentPassword(String oldPassword)
    {
        if (!PasswordValidateUtil.isValidPassword(oldPassword))
        {
            return false;
        }
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        Admin tempAdmin = adminDAO.get(admin.getId());
        HashPassword hashPassword = new HashPassword();
        if (tempAdmin != null)
        {
            hashPassword.setHashPassword(tempAdmin.getPassword());
            hashPassword.setIterations(tempAdmin.getIterations());
            hashPassword.setSalt(tempAdmin.getSalt());
        }
        
        if (!HashPasswordUtil.validatePassword(oldPassword, hashPassword))
        {
            return false;
        }
        return true;
    }
    
}
