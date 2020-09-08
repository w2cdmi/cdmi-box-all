package com.huawei.sharedrive.uam.user.dao;

import com.huawei.sharedrive.uam.user.domain.UserLocked;

public interface UserLockedDao
{
    
    UserLocked get(long userId, String appId);
    
    UserLocked getWithOutLock(long userId, String appId);
    
    void insert(UserLocked userLocked);
    
    void addFailTime(UserLocked userLocked);
    
    void clearFailTime(UserLocked userLocked);
    
}
