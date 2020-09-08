package pw.cdmi.box.disk.user.dao;

import pw.cdmi.box.disk.user.domain.UserLocked;

public interface UserLockedDao
{
    
    UserLocked get(long userId, String appId);
    
    void insert(UserLocked userLocked);
    
    void addFailTime(UserLocked userLocked);
    
    void clearFailTime(UserLocked userLocked);
    
    UserLocked getByLoginName(String selName);
    
}
