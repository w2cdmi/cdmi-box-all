package com.huawei.sharedrive.uam.ldapauth.manager.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverNetworkManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.ldapauth.manager.AuthServiceManager;
import com.huawei.sharedrive.uam.ldapauth.manager.LoginAuthManager;
import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;

import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;

@Component
public class LoginAuthManagerImpl implements LoginAuthManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAuthManager.class);
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private AccountAuthserverNetworkManager accountAuthserverNetworkManager;
    
    @Override
    public EnterpriseUser authenticate(String name, String password, LoginInfo loginInfo, long enterpriseId)
    {
        Enterprise enterprise = enterpriseManager.getById(enterpriseId);
        if (null == enterprise)
        {
            LOGGER.error("enterprise is null enterpriseId:" + enterpriseId);
            return null;
        }
        EnterpriseUser selEnterpriseUser = null;
        List<AuthServer> authServerList = authServerManager.getListByAccountId(enterpriseId, loginInfo.getAccountId());

        if (null == authServerList || authServerList.size() < 1)
        {
            return null;
        }
        long authServerId;
        AuthServiceManager authServiceManager;
        EnterpriseUser enterpriseUser = new EnterpriseUser();
        for (AuthServer authServer : authServerList)
        {
            authServerId = authServer.getId();
            authServiceManager = authServerManager.getAuthService(authServerId);
            if (null == loginInfo.getLoginType())
            {
                if (StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType()))
                {
                    continue;
                }
                try
                {
                    selEnterpriseUser = authServiceManager.authenticateByName(authServerId,
                        name,
                        password,
                        loginInfo.getDomainName());
                    if (null != selEnterpriseUser)
                    {
                        selEnterpriseUser.setUserSource(authServerId);
                        selEnterpriseUser.setEnterpriseId(enterpriseId);
                        break;
                    }
                }
                catch (Exception e)
                {
                    LOGGER.error("find failed authServerId:" + authServerId + " name:" + name, e);
                }
            }
            else
            {
                enterpriseUser.setName(name);
                enterpriseUser.setPassword(password);
                enterpriseUser.setUserSource(authServerId);
                selEnterpriseUser = getEnterpriseUser(loginInfo,
                    authServiceManager,
                    enterpriseUser,
                    authServer);
                if(null != selEnterpriseUser)
                {
                    break;
                }
            }
            
        }
        return selEnterpriseUser;
    }
    
    @Override
    public EnterpriseUser authenticateByNetwork(String name, String password, LoginInfo loginInfo,
        long enterpriseId, String realIp)
    {
        Enterprise enterprise = enterpriseManager.getById(enterpriseId);
        if (null == enterprise)
        {
            LOGGER.error("enterprise is null enterpriseId:" + enterpriseId);
            return null;
        }
        EnterpriseUser selEnterpriseUser = null;
        AuthServer authServer = accountAuthserverNetworkManager.checkAndGetAuthServerId(realIp,
            loginInfo.getAccountId());
        if (null == authServer)
        {
            LOGGER.error("there is no authServer by network  realIp:" + realIp + " name:" + name
                + " accountId:" + loginInfo.getAccountId());
            return null;
        }
        long authServerId = authServer.getId();
        AuthServiceManager authServiceManager = authServerManager.getAuthService(authServerId);
        if (null == loginInfo.getLoginType())
        {
            if (StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType()))
            {
                LOGGER.error("authServer  is local type:" + authServer.getType() + " name:" + name);
                return null;
            }
            try
            {
                selEnterpriseUser = authServiceManager.authenticateByName(authServerId,
                    name,
                    password,
                    loginInfo.getDomainName());
                if (null != selEnterpriseUser)
                {
                    selEnterpriseUser.setUserSource(authServerId);
                    selEnterpriseUser.setEnterpriseId(enterpriseId);
                }
            }
            catch (Exception e)
            {
                LOGGER.error("find failed authServerId:" + authServerId + " name:" + name, e);
            }
        }
        else
        {
            EnterpriseUser enterpriseUser = new EnterpriseUser();
            enterpriseUser.setName(name);
            enterpriseUser.setPassword(password);
            enterpriseUser.setUserSource(authServerId);
            selEnterpriseUser = getEnterpriseUser(loginInfo, authServiceManager, enterpriseUser, authServer);
        }
        return selEnterpriseUser;
    }
    
    private EnterpriseUser getEnterpriseUser(LoginInfo loginInfo, AuthServiceManager authServiceManager,
        EnterpriseUser enterpriseUser, AuthServer authServer)
    {
        EnterpriseUser selEnterpriseUser = new EnterpriseUser();
        switch (loginInfo.getLoginType())
        {
            case LoginInfo.LOGININGO_EMAIL:
                selEnterpriseUser = authServiceManager.authenticateByMail(enterpriseUser.getUserSource(),
                    enterpriseUser.getName(),
                    enterpriseUser.getPassword());
                break;
            case LoginInfo.LOGININGO_NAME:
                selEnterpriseUser = authServiceManager.authenticateByName(enterpriseUser.getUserSource(),
                    enterpriseUser.getName(),
                    enterpriseUser.getPassword(),
                    loginInfo.getDomainName());
                break;
            case LoginInfo.LOGININGO_MOBILE:
                selEnterpriseUser = authServiceManager.authenticateByMobile(enterpriseUser.getUserSource(),
                    enterpriseUser.getName(),
                    enterpriseUser.getPassword());
                break;
            case LoginInfo.LOGININGO_SATFF:
                selEnterpriseUser = authServiceManager.authenticateByStaff(enterpriseUser.getUserSource(),
                    enterpriseUser.getName(),
                    enterpriseUser.getPassword());
                break;
            default:
                break;
        }
        if (null != selEnterpriseUser)
        {
            selEnterpriseUser.setUserSource(enterpriseUser.getUserSource());
            selEnterpriseUser.setEnterpriseId(authServer.getEnterpriseId());
        }
        return selEnterpriseUser;
    }
    
}
