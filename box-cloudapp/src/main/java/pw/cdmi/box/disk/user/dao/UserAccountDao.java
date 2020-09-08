package pw.cdmi.box.disk.user.dao;

import pw.cdmi.box.disk.user.domain.UserAccount;

public interface UserAccountDao
{
    UserAccount getByCloudUserId(long accountId, long cloudUserId);
    
    UserAccount getByUserId(long accountId, long userId);
    
    void updateFirstLogin(UserAccount userAccount);
}
