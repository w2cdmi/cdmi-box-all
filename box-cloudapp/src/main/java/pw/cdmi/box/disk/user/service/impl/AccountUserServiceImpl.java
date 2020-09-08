package pw.cdmi.box.disk.user.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.authserver.dao.AuthServerDao;
import pw.cdmi.box.disk.authserver.service.AuthServerService;
import pw.cdmi.box.disk.user.dao.UserAccountDao;
import pw.cdmi.box.disk.user.domain.UserAccount;
import pw.cdmi.box.disk.user.service.AccountUserService;
import pw.cdmi.common.domain.AuthServer;

@Component("accountUserService")
public class AccountUserServiceImpl implements AccountUserService
{
    
    @Autowired
    private AuthServerDao authServerDao;
    
    @Autowired
    private UserAccountDao userAccountDao;
    
    @Override
    public UserAccount getAccountUser(long accountId, long userId)
    {
        return userAccountDao.getByUserId(accountId, userId);
    }
    
    @Override
    public boolean isLocalAndFirstLogin(long accountId, long userId)
    {
        if(accountId==0){
            return false;
        }
        UserAccount accountUser = this.userAccountDao.getByUserId(accountId, userId);
        if (accountUser.getFirstLogin() == null || accountUser.getFirstLogin() == UserAccount.STATUS_FIRST_LOGIN)
        {
            AuthServer authServer = this.authServerDao.get(accountUser.getResourceType());
            if (null == authServer)
            {
                return false;
            }
            if (StringUtils.equals(authServer.getType(), AuthServerService.AUTH_TYPE_LOCAL))
            {
                return true;
            }
        }

        return false;
    }
    
    @Override
    public void setNoneFirstLogin(long accountId, long userId)
    {
        UserAccount userAccount = new UserAccount();
        userAccount.setAccountId(accountId);
        userAccount.setUserId(userId);
        userAccount.setFirstLogin(UserAccount.STATUS_NONE_FIRST_LOGIN);
        userAccountDao.updateFirstLogin(userAccount);
    }

	@Override
	public UserAccount getUserByCloudUserId(long accountId, long userId) {
		// TODO Auto-generated method stub
		return userAccountDao.getByCloudUserId(accountId, userId);
	}
    
}
