package com.huawei.sharedrive.uam.ldapauth.manager.impl;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;
import com.huawei.sharedrive.uam.ldapauth.manager.LoginUpdateManager;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.restrpc.RestClient;

@Component
public class LoginUpdateManagerImpl implements LoginUpdateManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginUpdateManager.class);
    
    @Autowired
    private AccountAuthserverManager accountAuthserverManager;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private EnterpriseAccountService enterpriseAccountService;
    
    @Autowired
    private EnterpriseUserManager enterpriseUserManager;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private UserAccountManager userAccountManager;
    
    @Override
    public EnterpriseUserAccount save(long authServerId, String objectSid, long enterpriseId,
        String authAppId, EnterpriseUser ldapEnterpriseUser) throws IOException
    {
        EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(enterpriseId,
            authAppId);
        EnterpriseUser enterpriseUser;
        
        if (null == enterpriseAccount)
        {
            LOGGER.error("enterpriseAccount is null enterpriseId:" + enterpriseId + " authAppId:" + authAppId);
            throw new BusinessException();
        }
        if (Constants.STATUS_OF_ACCOUNT_ENABLE != enterpriseAccount.getStatus())
        {
            LOGGER.error("enterpriseAccount staus is disable, enterpriseId: " + enterpriseId, "authAppId: "
                + authAppId);
            throw new BusinessException();
        }
        AccountAuthserver accountAuthserver = accountAuthserverManager.getByAccountAuthId(enterpriseAccount.getAccountId(),
            authServerId);
        if (null == accountAuthserver)
        {
            LOGGER.error("accountAuthserver is null authServerId:" + authServerId + " accountId:"
                + enterpriseAccount.getAccountId());
            throw new BusinessException();
        }
        AuthServer authServer = authServerManager.getAuthServer(authServerId);
        if (null == authServer)
        {
            LOGGER.error("authServer is null authServerId:" + authServerId);
            throw new BusinessException();
        }
        enterpriseUser = enterpriseUserManager.getByObjectSid(objectSid, enterpriseId);
        judgeAutherServer(objectSid, enterpriseId, enterpriseUser, accountAuthserver, authServer);
        EnterpriseUser oldEnterpriseUser = enterpriseUser;
        if (null == enterpriseUser)
        {
            enterpriseUserManager.createLdap(ldapEnterpriseUser);
            enterpriseUser = enterpriseUserManager.getByObjectSid(objectSid, enterpriseId);
        }
        else
        {
            enterpriseUserManager.updateLdap(ldapEnterpriseUser, enterpriseUser.getId(), enterpriseId);
        }
        UserAccount userAccount = userAccountManager.getUserAccountByApp(enterpriseUser.getId(),
            enterpriseId,
            authAppId);
        
        judgeAuthServer(enterpriseId, authAppId, userAccount, accountAuthserver, authServer);
        if (null == userAccount)
        {
            userAccountManager.create(enterpriseUser.getId(), enterpriseId, authAppId,enterpriseUser.getName());
            userAccount = userAccountManager.getUserAccountByApp(enterpriseUser.getId(),
                enterpriseId,
                authAppId);
        }
        userAccountManager.updateLoginTime(userAccount);
        updateUfmUser(ldapEnterpriseUser, enterpriseAccount, enterpriseUser, userAccount, oldEnterpriseUser);
        EnterpriseUserAccount enterpriseUserAccount = new EnterpriseUserAccount();
        enterpriseUserAccount.setAppId(authAppId);
        EnterpriseUserAccount.copyAccountUser(enterpriseUserAccount, userAccount);
        EnterpriseUserAccount.copyEnterpriseUser(enterpriseUserAccount, enterpriseUser);
        return enterpriseUserAccount;
    }
    
    private void updateUfmUser(EnterpriseUser ldapEnterpriseUser, EnterpriseAccount enterpriseAccount,
        EnterpriseUser enterpriseUser, UserAccount userAccount, EnterpriseUser oldEnterpriseUser)
    {
        if (null == oldEnterpriseUser)
        {
            return;
        }
        boolean attChanged = false;
        if (!StringUtils.equals(ldapEnterpriseUser.getAlias(), enterpriseUser.getAlias()))
        {
            attChanged = true;
        }
        else if (!StringUtils.equals(enterpriseUser.getEmail(), ldapEnterpriseUser.getEmail()))
        {
            attChanged = true;
        }
        else if (!StringUtils.equals(enterpriseUser.getName(), ldapEnterpriseUser.getName()))
        {
            attChanged = true;
        }
        else if (!StringUtils.equals(enterpriseUser.getDescription(), ldapEnterpriseUser.getDescription()))
        {
            attChanged = true;
        }

        if (attChanged)
        {
            UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
            transEnterpriseUser(ldapEnterpriseUser, enterpriseUser);
            userHttpClient.updateUser(userAccount, enterpriseUser, enterpriseAccount);
        }
    }
    
    private void judgeAutherServer(String objectSid, long enterpriseId, EnterpriseUser enterpriseUser,
        AccountAuthserver accountAuthserver, AuthServer authServer)
    {
        if (null != enterpriseUser)
        {
            return;
        }
        if (StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType()))
        {
            String msg = "enterpriseUser is null :" + objectSid + " enterpriseId:" + enterpriseId + " type:"
                + authServer.getType() + " accountAuthserverType:" + accountAuthserver.getType();
            throw new BusinessException(msg);
        }
        else
        {
            if (AccountAuthserver.AUTO_OPEN_ACCOUNT != accountAuthserver.getType())
            {
                String msg = "enterpriseUser is null :" + objectSid + " enterpriseId:" + enterpriseId
                    + " type:" + authServer.getType() + " accountAuthserverType:"
                    + accountAuthserver.getType();
                throw new BusinessException(msg);
            }
            
        }
    }
    
    private void judgeAuthServer(long enterpriseId, String authAppId, UserAccount userAccount,
        AccountAuthserver accountAuthserver, AuthServer authServer)
    {
        if (null != userAccount)
        {
            return;
        }
        if (StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType()))
        {
            LOGGER.error("userAccount is null enterpriseId:" + enterpriseId + " authAppId:" + authAppId
                + " userId:" + " type:" + authServer.getType());
            throw new BusinessException();
        }
        else
        {
            if (AccountAuthserver.AUTO_OPEN_ACCOUNT != accountAuthserver.getType())
            {
                LOGGER.error("userAccount is null enterpriseId:" + enterpriseId + " authAppId:" + authAppId
                    + " userId:" + " type:" + authServer.getType());
                throw new BusinessException();
            }
        }
    }
    
    private void transEnterpriseUser(EnterpriseUser ldapEnterpriseUser, EnterpriseUser enterpriseUser)
    {
        if (ldapEnterpriseUser.getAlias() != null)
        {
            enterpriseUser.setAlias(ldapEnterpriseUser.getAlias());
        }
        if (ldapEnterpriseUser.getEmail() != null)
        {
            enterpriseUser.setEmail(ldapEnterpriseUser.getEmail());
        }
        if (ldapEnterpriseUser.getName() != null)
        {
            enterpriseUser.setName(ldapEnterpriseUser.getName());
        }
        if (ldapEnterpriseUser.getDescription() != null)
        {
            enterpriseUser.setDescription(ldapEnterpriseUser.getDescription());
        }
        if (ldapEnterpriseUser.getModifiedAt() != null)
        {
            enterpriseUser.setModifiedAt(ldapEnterpriseUser.getModifiedAt());
        }
    }
}
