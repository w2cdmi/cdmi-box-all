/**
 * 
 */
package com.huawei.sharedrive.uam.user.service;

import com.huawei.sharedrive.uam.exception.UserLockedException;
import com.huawei.sharedrive.uam.user.domain.UserLocked;

import pw.cdmi.common.log.UserLog;

public interface UserLockService
{
    String LOCK_WAIT = "lock.time";
    
    String LOCK_TIMES = "lock.failtimes";
    
    UserLocked checkUserLocked(UserLog userLog, long userId, String domainName, String appId)
        throws UserLockedException;
    
    boolean addUserLocked(UserLocked userLocked, long accountId);
    
    void deleteUserLocked(UserLocked userLocked);
    
    boolean isLocked(UserLocked userLocked);
    
    UserLocked getUserLockWithoutLock(String appId, long userId);
    
    void createUserLocked(long userId, String loginName, String domainName, String appId,
        final UserLocked userLocked);
    
    int getConfigByLockWait();
    
    int getConfigByLockTimes();

	int getConfigByAccountLock(long account);
}