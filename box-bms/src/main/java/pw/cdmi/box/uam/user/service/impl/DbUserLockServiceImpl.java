/**
 * 
 */
package pw.cdmi.box.uam.user.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.exception.UserLockedException;
import pw.cdmi.box.uam.system.dao.SystemConfigDAO;
import pw.cdmi.box.uam.user.dao.UserLockedDao;
import pw.cdmi.box.uam.user.domain.UserLocked;
import pw.cdmi.box.uam.user.service.UserLockService;
import pw.cdmi.box.uam.util.Constants;

/**
 * 
 */
@Service("userLockService")
public class DbUserLockServiceImpl implements UserLockService
{
    
    private static Logger logger = LoggerFactory.getLogger(DbUserLockServiceImpl.class);
    
    @Autowired
    private UserLockedDao userLockedDao;
    
    @Autowired
    private SystemConfigDAO systemConfigDao;
    
    @Override
    public UserLocked checkUserLocked(long userId, String loginName, String appId) throws UserLockedException
    {
        UserLocked userLocked = this.userLockedDao.get(userId, appId);
        if (null == userLocked)
        {
            userLocked = new UserLocked();
            userLocked.setAppId(appId);
            userLocked.setCreatedAt(new Date());
            userLocked.setUserId(userId);
            userLocked.setLoginFailTimes(0);
            userLocked.setLoginName(loginName);
            try
            {
                this.userLockedDao.insert(userLocked);
            }
            catch (DuplicateKeyException e)
            {
                logger.error(Constants.LINE_SEPARATOR + "DuplicateKeyException");
                checkUserLocked(userId, loginName, appId);
            }
        }
        if (userLocked.getLockedAt() == null)
        {
            return userLocked;
        }
        if (new Date().getTime() - userLocked.getLockedAt().getTime() > getConfigByLockWait())
        {
            this.deleteUserLocked(userLocked);
        }
        return userLocked;
    }
    
    @Override
    public boolean addUserLocked(UserLocked userLocked)
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
        else
        {
            result = true;
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
}
