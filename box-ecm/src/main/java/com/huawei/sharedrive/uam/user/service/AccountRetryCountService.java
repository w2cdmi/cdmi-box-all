package com.huawei.sharedrive.uam.user.service;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.exception.UserLockedException;
import com.huawei.sharedrive.uam.user.domain.ModifyPasswdLocked;

public interface AccountRetryCountService
{
    void deleteUserLocked(String userName, HttpServletRequest request);
    
    void doCreateUserLocked(String userName, ModifyPasswdLocked userLocked);
    
    ModifyPasswdLocked doReadUserLocked(String userName);
    
    void addUserLocked(String userName);
    
    void checkUserLocked(String userName, HttpServletRequest request) throws UserLockedException;
}
