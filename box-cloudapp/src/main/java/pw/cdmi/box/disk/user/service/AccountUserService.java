package pw.cdmi.box.disk.user.service;

import pw.cdmi.box.disk.user.domain.UserAccount;

public interface AccountUserService
{
    
    UserAccount getAccountUser(long accountId, long userId);
    
    boolean isLocalAndFirstLogin(long accountId, long userId);
    
    void setNoneFirstLogin(long accountId, long userId);

	UserAccount getUserByCloudUserId(long accountId, long userId);
    
}
