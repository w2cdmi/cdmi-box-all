package com.huawei.sharedrive.uam.user.service;

import com.huawei.sharedrive.uam.user.domain.UserLocked;

public interface AccountCountService
{
    /**
     * 
     * @param userName
     */
    void deleteUserLocked(String authorization, String userName, String appId, long userId);
    
    /**
     * 
     * @param userName
     * @param userLocked
     */
    void doCreateUserLocked(String userName, UserLocked userLocked);
    
    /**
     * 
     * @param userName
     * @return
     */
    UserLocked doReadUserLocked(String userName);
    
    /**
     * 
     * @param userName
     */
    void addUserLocked(String userName);
    
    /**
     * user modify password locked
     * 
     * @param admin
     */
    boolean checkUserLocked(String authorization, String userName, String appId, long userId);
}
