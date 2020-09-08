/**
 * 
 */
package com.huawei.sharedrive.uam.user.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.enterprise.dao.AccountConfigDao;
import pw.cdmi.common.domain.AccountConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccountConfigAttribute;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.UserLockedException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.uam.system.domain.MailServer;
import com.huawei.sharedrive.uam.system.service.MailServerService;
import com.huawei.sharedrive.uam.user.dao.UserLockedDao;
import com.huawei.sharedrive.uam.user.domain.UserLocked;
import com.huawei.sharedrive.uam.user.service.UserLockService;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.log.UserLog;

/**
 * 
 */
@Service("userLockService")
public class DbUserLockServiceImpl implements UserLockService
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DbUserLockServiceImpl.class);
    
    @Autowired
    private UserLockedDao userLockedDao;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private SystemConfigDAO systemConfigDao;
    
    @Autowired
    private AccountConfigDao accountConfigDao;
    
    
	@Autowired
	private MailServerService mailServerService;
    
    @Override
    public UserLocked checkUserLocked(UserLog userLog, long userId, String domainName, String appId)
        throws UserLockedException
    {
        UserLocked userLocked = this.userLockedDao.get(userId, appId);
        if (null == userLocked)
        {
            return null;
        }
        if (userLocked.getLockedAt() == null)
        {
            return userLocked;
        }
        if (new Date().getTime() - userLocked.getLockedAt().getTime() > getConfigByLockWait())
        {
            this.deleteUserLocked(userLocked);
            userLogService.saveUserLog(userLog, UserLogType.KEY_UNLOCK_USER, null);
        }
        return userLocked;
    }
    
    @Override
    public void createUserLocked(long userId, String loginName, String domainName, String appId,
        final UserLocked userLocked)
    {
        UserLocked tempUserLocked = userLocked;
        if (null == tempUserLocked)
        {
            tempUserLocked = new UserLocked();
            tempUserLocked.setAppId(appId);
            tempUserLocked.setCreatedAt(new Date());
            tempUserLocked.setUserId(userId);
            tempUserLocked.setLoginFailTimes(0);
            tempUserLocked.setLoginName(domainName + '/' + loginName);
            try
            {
                this.userLockedDao.insert(tempUserLocked);
            }
            catch (DuplicateKeyException e)
            {
                LOGGER.error("DuplicateKeyException");
                tempUserLocked = this.userLockedDao.getWithOutLock(userId, appId);
                createUserLocked(userId, loginName, domainName, appId, tempUserLocked);
            }
        }
    }
    
    

    
    @Override
    public boolean addUserLocked(UserLocked userLocked,long account)
    {
        boolean result = false;
        
        
        if (userLocked.getLoginFailTimes() == getConfigByLockTimes() - 1)
        {
            userLocked.setLockedAt(new Date());
            result = true;
        }
        if (userLocked.getLoginFailTimes() < getConfigByLockTimes())
        {
            this.userLockedDao.addFailTime(userLocked);
        }
        
        return result;
    }
    
    @Override
    public void deleteUserLocked(UserLocked userLocked)
    {
        this.userLockedDao.clearFailTime(userLocked);
    }
    
    @Override
    public boolean isLocked(UserLocked userLocked)
    {
        if (userLocked.getLockedAt() == null)
        {
            return false;
        }
        if (new Date().getTime() - userLocked.getLockedAt().getTime() < getConfigByLockWait())
        {
            return true;
        }
        return false;
    }
    
    @Override
    public UserLocked getUserLockWithoutLock(String appId, long userId)
    {
        // TODO Auto-generated method stub
        return this.userLockedDao.getWithOutLock(userId, appId);
    }
    
    @Override
    public int getConfigByLockWait()
    {
        String lockWait = systemConfigDao.getByPriKey("-1", LOCK_WAIT).getValue();
        return Integer.parseInt(lockWait) * 60 * 1000;
    }
    
    @Override
    public int getConfigByLockTimes()
    {
        String lockTimes = systemConfigDao.getByPriKey("-1", LOCK_TIMES).getValue();
        return Integer.parseInt(lockTimes);
    }
    
    
    @Override
    public int getConfigByAccountLock(long account)
    {
    	AccountConfig notice_enable = accountConfigDao.get(account, AccountConfigAttribute.SECURITY_LOGINFAIL_NOTICE_ENABLE.getName());
    	AccountConfig lock_time = accountConfigDao.get(account, AccountConfigAttribute.SECURITY_LOGINFAIL_LOCK_TIME.getName());
        AccountConfig lockTimes = accountConfigDao.get(account, AccountConfigAttribute.SECURITY_LOGINFAIL_TRY_TIMES.getName());
        return Integer.parseInt(lockTimes.getValue());
    }

}
