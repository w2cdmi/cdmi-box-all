package pw.cdmi.box.uam.user.service;

import javax.servlet.http.HttpServletRequest;

import pw.cdmi.box.uam.exception.UserLockedException;
import pw.cdmi.box.uam.user.domain.UserLocked;

/**
 * 用户修改密码锁定，处理暴力破解旧密码
 * 
 * @author d00199602
 * 
 */
public interface AccountRetryCountService
{
    /**
     * 删除用户尝试旧密码错误记录
     * 
     * @param userName
     */
    void deleteUserLocked(String userName, HttpServletRequest request);
    
    /**
     * 创建用户尝试旧密码错误记录
     * 
     * @param userName
     * @param userLocked
     */
    void doCreateUserLocked(String userName, UserLocked userLocked);
    
    /**
     * 读取用户尝试旧密码错误记录
     * 
     * @param userName
     * @return
     */
    UserLocked doReadUserLocked(String userName);
    
    /**
     * 增加用户尝试旧密码失败次数
     * 
     * @param userName
     */
    void addUserLocked(String userName);
    
    /**
     * 用户修改密码锁定
     * 
     * @param admin
     * @throws UserLockedException
     */
    void checkUserLocked(String userName, HttpServletRequest request) throws UserLockedException;
}
