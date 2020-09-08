package com.huawei.sharedrive.isystem.user.service;

import com.huawei.sharedrive.isystem.user.domain.UserLocked;

public interface NtlmManagerService
{
    
    
    void deleteUserLocked(String userName);
    
    void doCreateUserLocked(String userName, UserLocked userLocked);
    
    UserLocked doReadUserLocked(String userName);
}
