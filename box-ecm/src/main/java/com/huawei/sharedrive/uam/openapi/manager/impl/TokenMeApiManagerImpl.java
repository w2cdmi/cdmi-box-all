package com.huawei.sharedrive.uam.openapi.manager.impl;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;
import com.huawei.sharedrive.uam.logininfo.manager.LoginInfoManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.enterpriseuseraccount.manager.EnterpriseUserAccountManager;
import com.huawei.sharedrive.uam.exception.EmailChangeConflictException;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.MobileChangeConflictException;
import com.huawei.sharedrive.uam.exception.NoSuchUserException;
import com.huawei.sharedrive.uam.exception.UserLockedException;
import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;
import com.huawei.sharedrive.uam.ldapauth.manager.AuthServiceManager;
import com.huawei.sharedrive.uam.ldapauth.manager.LoginUpdateManager;
import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;
import com.huawei.sharedrive.uam.logininfo.manager.LoginInfoManager;
import com.huawei.sharedrive.uam.openapi.domain.BasicUserUpdateRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestUserCreateRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestUserUpdateRequest;
import com.huawei.sharedrive.uam.openapi.manager.TokenMeApiCheckManager;
import com.huawei.sharedrive.uam.openapi.manager.TokenMeApiManager;
import com.huawei.sharedrive.uam.user.service.AccountCountService;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.restrpc.RestClient;

@Component
public class TokenMeApiManagerImpl implements TokenMeApiManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenMeApiManager.class);
    
    @Autowired
    private LoginUpdateManager loginUpdateManager;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @Autowired
    private EnterpriseUserManager enterpriseUserManager;
    
    @Autowired
    private EnterpriseUserService enterpriseUserService;
    
    @Autowired
    private UserAccountManager userAccountManager;
    
    @Autowired
    private TokenMeApiCheckManager tokenMeApiCheckManager;
    
    @Autowired
    private EnterpriseUserAccountManager enterpriseUserAccountManager;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private AccountCountService accountCountService;

    @Autowired
    private LoginInfoManager loginInfoManager;
    
    @Override
    public EnterpriseUserAccount createLdapUser(String loginName, String appId, long enterpriseId,
        long accountId) throws IOException
    {
        EnterpriseUser selEnterpriseUser = null;
        List<AuthServer> authServerList = authServerManager.getListByAccountId(enterpriseId, accountId);
        if (null == authServerList || authServerList.size() < 1)
        {
            LOGGER.error("authServerList is null enterpriseId:" + enterpriseId + " accountId:" + accountId);
            throw new InternalServerErrorException();
        }
        long authServerId;
        AuthServiceManager authServiceManager;
        for (AuthServer authServer : authServerList)
        {
            if (StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType()))
            {
                continue;
            }
            authServerId = authServer.getId();
            authServiceManager = authServerManager.getAuthService(authServerId);
            selEnterpriseUser = authServiceManager.getUserByLoginName(authServerId, loginName);
            if (null != selEnterpriseUser)
            {
                selEnterpriseUser.setUserSource(authServerId);
                selEnterpriseUser.setEnterpriseId(enterpriseId);
                break;
            }
        }
        if (null == selEnterpriseUser)
        {
            LOGGER.error("selEnterpriseUser is null enterpriseId:" + enterpriseId + " accountId:" + accountId
                + " loginName:" + loginName);
            throw new NoSuchUserException();
        }
        EnterpriseUserAccount enterpriseUserAccount = loginUpdateManager.save(selEnterpriseUser.getUserSource(),
            selEnterpriseUser.getObjectSid(),
            enterpriseId,
            appId,
            selEnterpriseUser);
        return enterpriseUserAccount;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public EnterpriseUserAccount updateEnterpriseUser(@RequestHeader("Authorization") String authorization,
        long userId, long enterpriseId, long accountId, BasicUserUpdateRequest ruser)
    {
        boolean isUpdateEnterpriseUser = false;
        boolean isUpdateUserAccount = false;
        Enterprise enterprise = enterpriseManager.getById(enterpriseId);
        if (null == enterprise)
        {
            LOGGER.error("enterprise is null enterpriseId:" + enterpriseId);
            throw new InternalServerErrorException();
        }
        EnterpriseAccount enterpriseAccount = enterpriseAccountManager.getByAccountId(accountId);
        if (null == enterpriseAccount)
        {
            LOGGER.error("enterpriseAccount is null accountId:" + accountId);
            throw new InternalServerErrorException();
        }
        if (Constants.STATUS_OF_ACCOUNT_ENABLE != enterpriseAccount.getStatus())
        {
            LOGGER.error("enterpriseAccount staus is disable accoundId:" + accountId);
            throw new InternalServerErrorException();
        }
        EnterpriseUser selEnterpriseUser = enterpriseUserService.get(userId, enterpriseId);
        if (null == selEnterpriseUser)
        {
            String msg = "enterpriseUser is null enterpriseId:" + enterpriseId + " userId:" + userId;
            throw new NoSuchUserException(msg);
        }
        if(null!=ruser.getEmail() &&  !ruser.getEmail().equals(selEnterpriseUser.getEmail())){
            //LoginInfo loginInfo = loginInfoManager.getByDomain(ruser.getEmail(),enterprise.getDomainName());//多企业内唯一性
            List<LoginInfo> loginInfos = loginInfoManager.getByLoginName(ruser.getEmail());//全局唯一性
            if(loginInfos.size()>0 && userId !=loginInfos.get(0).getUserId()){
                throw new EmailChangeConflictException("New email address had exists!");
            }
        }
        if(null!=ruser.getMobile() &&  !ruser.getMobile().equals(selEnterpriseUser.getMobile())){
        	List<LoginInfo> loginInfos = loginInfoManager.getByLoginName(ruser.getMobile());//全局唯一性
            if(loginInfos.size()>0 && userId !=loginInfos.get(0).getUserId()){
                throw new MobileChangeConflictException("New mobile number had exists!");
            }
        }
        AuthServer authServer = authServerManager.getAuthServer(selEnterpriseUser.getUserSource());
        if (null == authServer)
        {
            LOGGER.error("authServer is null authServerId:" + selEnterpriseUser.getUserSource() + " userId:"
                + userId + " enterpriseId:" + enterpriseId);
            throw new InternalServerErrorException();
        }
        if (!StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType()))
        {
            tokenMeApiCheckManager.checkLdapUserUpdateReq(ruser);
        }
        checkAndUpdateUserLockInfo(authorization, userId, ruser, enterpriseAccount, selEnterpriseUser);
        UserAccount userAccount = userAccountManager.get(userId, accountId);
        if (null == userAccount)
        {
            LOGGER.error("userAccount is null enterpriseId:" + enterpriseId + " userId:" + userId
                + " accountId:" + accountId);
            throw new NoSuchUserException();
        }
        isUpdateEnterpriseUser = enterpriseUserManager.update(ruser,
            selEnterpriseUser,
            enterprise.getDomainName());
        isUpdateUserAccount = userAccountManager.update(RestUserUpdateRequest.castTo(ruser), userAccount);
        if (isUpdateEnterpriseUser || isUpdateUserAccount)
        {
            UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
            userHttpClient.updateUser(userAccount, selEnterpriseUser, enterpriseAccount);
        }
        EnterpriseUserAccount enterpriseUserAccount = new EnterpriseUserAccount();
        EnterpriseUserAccount.copyAccountUser(enterpriseUserAccount, userAccount);
        EnterpriseUserAccount.copyEnterpriseUser(enterpriseUserAccount, selEnterpriseUser);
        enterpriseUserAccount.setDomain(enterprise.getDomainName());
        return enterpriseUserAccount;
    }
    
    private void checkAndUpdateUserLockInfo(String authorization, long userId, BasicUserUpdateRequest ruser,
        EnterpriseAccount enterpriseAccount, EnterpriseUser selEnterpriseUser)
    {
        if (accountCountService.checkUserLocked(authorization,
            selEnterpriseUser.getName(),
            enterpriseAccount.getAuthAppId(),
            userId))
        {
            throw new UserLockedException("modify password is locked");
        }
        
        if (StringUtils.isEmpty(ruser.getOldPassword()))
        {
            return;
        }
        if (StringUtils.isEmpty(ruser.getNewPassword()))
        {
            return;
        }
        if (ruser.getNewPassword().equalsIgnoreCase(selEnterpriseUser.getName()))
        {
            accountCountService.addUserLocked(selEnterpriseUser.getName());
            throw new InvalidParamterException("loginName and password cannot be the same");
        }
        accountCountService.addUserLocked(selEnterpriseUser.getName());
    }
    
    @Override
    public EnterpriseUserAccount getUserInfo(Long userId, long accountId, long enterpriseId)
    {
        EnterpriseUserAccount enterpriseUserAccount = enterpriseUserAccountManager.get(userId,
            accountId,
            enterpriseId);
        if (null != enterpriseUserAccount)
        {
            EnterpriseAccount enterpriseAccount = enterpriseAccountManager.getByAccountId(accountId);
            if (null == enterpriseAccount)
            {
                LOGGER.error("enterpriseAccount is null accountId:" + accountId);
                return null;
            }
            UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
            RestUserCreateRequest restUserCreateRequest = userHttpClient.getUserInfo(enterpriseUserAccount.getCloudUserId(),
                enterpriseAccount);
            if (restUserCreateRequest != null)
            {
                enterpriseUserAccount.setSpaceUsed(restUserCreateRequest.getSpaceUsed());
                enterpriseUserAccount.setFileCount(restUserCreateRequest.getFileCount());
            }
            enterpriseUserAccount.setAppId(enterpriseAccount.getAuthAppId());
        }
        return enterpriseUserAccount;
    }

    @Override
    public EnterpriseUserAccount getUserInfoByImAccount(String imAccount, long accountId, long enterpriseId) {
        Enterprise enterprise = enterpriseManager.getById(enterpriseId);
        if (null == enterprise) {
            LOGGER.error("enterprise is null enterpriseId:" + enterpriseId);
            return null;
        }
        EnterpriseUserAccount enterpriseUserAccount = enterpriseUserAccountManager.getByImAccount(imAccount, accountId, enterpriseId);
        if (null != enterpriseUserAccount) {
            EnterpriseAccount enterpriseAccount = enterpriseAccountManager.getByAccountId(accountId);
            if (null == enterpriseAccount) {
                LOGGER.error("enterpriseAccount is null accountId:" + accountId);
                return null;
            }
            UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
            RestUserCreateRequest restUserCreateRequest = userHttpClient.getUserInfo(enterpriseUserAccount.getCloudUserId(), enterpriseAccount);
            if (restUserCreateRequest != null) {
                enterpriseUserAccount.setSpaceUsed(restUserCreateRequest.getSpaceUsed());
                enterpriseUserAccount.setFileCount(restUserCreateRequest.getFileCount());
            }
            enterpriseUserAccount.setAppId(enterpriseAccount.getAuthAppId());
            enterpriseUserAccount.setDomain(enterprise.getDomainName());
        }
        return enterpriseUserAccount;
    }

	@Override
	public RestUserCreateRequest getPersonUserInfo(Long cloudUserId, String token) {
		// TODO Auto-generated method stub
		  UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
          RestUserCreateRequest restUserCreateRequest = userHttpClient.getPersonUserInfo(cloudUserId, token);
		return restUserCreateRequest;
	}
}
