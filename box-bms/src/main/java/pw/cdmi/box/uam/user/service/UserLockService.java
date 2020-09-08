/**
 * 
 */
package pw.cdmi.box.uam.user.service;

import pw.cdmi.box.uam.exception.UserLockedException;
import pw.cdmi.box.uam.user.domain.UserLocked;

public interface UserLockService
{
    String LOCK_WAIT = "lock.time";
    
    String LOCK_TIMES = "lock.failtimes";
    
    /**
     * 检查用户是否已锁定
     * 
     * @param loginName
     * @param appId
     * @return 是否在本次检查时解锁
     * @throws UserLockedException 用户已锁定
     */
    UserLocked checkUserLocked(long userId, String loginName, String appId) throws UserLockedException;
    
    /**
     * 增加一次登录失败
     * 
     * @param loginName
     * @param appId
     * @return 是否在本次增加后锁定
     */
    boolean addUserLocked(UserLocked userLocked);
    
    /**
     * 清除用户锁定信息
     * 
     * @param loginName
     * @param appId
     * @return 本次清除前，用户是否已锁定
     */
    void deleteUserLocked(UserLocked userLocked);
    
    boolean isLocked(UserLocked userLocked);
    
    int getConfigByLockWait();
    
    int getConfigByLockTimes();
    
}