package pw.cdmi.box.uam.user.dao;

import pw.cdmi.box.uam.user.domain.UserLocked;

public interface UserLockedDao
{
    
    UserLocked get(long userId, String appId);
    
    void insert(UserLocked userLocked);
    
    void addFailTime(UserLocked userLocked);
    
    void clearFailTime(UserLocked userLocked);
    
}
