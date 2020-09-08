package com.huawei.sharedrive.uam.enterprisecontrol.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterprisecontrol.EnterpriseAuthControlManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.exception.NoSuchUserException;
import com.huawei.sharedrive.uam.ldapauth.manager.AuthServiceManager;
import com.huawei.sharedrive.uam.ldapauth.manager.LoginAuthManager;
import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;
import com.huawei.sharedrive.uam.logininfo.manager.LoginInfoManager;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

@Component
public class EnterpriseAuthControlManagerImpl implements EnterpriseAuthControlManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseAuthControlManager.class);
    
    @Autowired
    private LoginAuthManager loginAuthManager;
    
    @Autowired
    private LoginInfoManager loginInfoManager;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private AccountAuthserverManager accountAuthServerManager;
    
    @Override
    public LoginInfo getLoginInfo(String loginName, String domainName, String appId)
    {
        LoginInfo loginInfo = null;
        Enterprise enterprise = null;
        EnterpriseAccount enterpriseAccount = null;
        if (StringUtils.isBlank(domainName))
        {
            List<LoginInfo> loginInfoList = loginInfoManager.getByLoginName(loginName);
            if (CollectionUtils.isNotEmpty(loginInfoList))
            {
                if (loginInfoList.size() > 1)
                {
                    LOGGER.error("loginInfo table has too many user:" + loginName);
                    throw new LoginAuthFailedException();
                }
                loginInfo = loginInfoList.get(0);
                enterprise = enterpriseManager.getById(loginInfo.getEnterpriseId());
            }
            if (null == enterprise)
            {
                PageRequest request = new PageRequest();
                request.setSize(Constants.DEFAULT_OTHER_SIZE);
                Page<Enterprise> enterprisePage = enterpriseManager.getFilterd(null, null, request);
                List<Enterprise> enterpriseList = enterprisePage.getContent();
                if (enterpriseList == null)
                {
                    LOGGER.error("enterpriseList is null");
                    throw new InternalServerErrorException();
                }
                if (enterpriseList.size() != 1)
                {
                    LOGGER.error("enterpriseList out of range size:" + enterpriseList.size());
                    throw new InternalServerErrorException();
                }
                enterprise = enterpriseList.get(0);
            }
        }
        else
        {
            loginInfo = loginInfoManager.getByDomain(loginName, domainName);
            enterprise = enterpriseManager.getByDomainName(domainName);
        }
        if (null == enterprise)
        {
            LOGGER.error("invalid domain:" + domainName);
            throw new InvalidParamterException();
        }
        if (null == loginInfo)
        {
            loginInfo = new LoginInfo();
        }
        enterpriseAccount = enterpriseAccountManager.getByEnterpriseApp(enterprise.getId(), appId);
        if (null == enterpriseAccount)
        {
            LOGGER.error("enterpriseAccount is null appId:" + appId + " enterpriseId:" + enterprise.getId());
            throw new InternalServerErrorException();
        }
        loginInfo.setEnterpriseId(enterprise.getId());
        loginInfo.setDomainName(enterprise.getDomainName());
        loginInfo.setAccountId(enterpriseAccount.getAccountId());
        return loginInfo;
    }
    
    @Override
    public LoginInfo getUserLoginInfo(String loginName, String domainName, String appId)
    {
        LoginInfo loginInfo = null;
        Enterprise enterprise = null;
        EnterpriseAccount enterpriseAccount = null;

        //未提供域名，直接根据用户名查询，如果只有一个账户信息，放行。
        if (StringUtils.isBlank(domainName)) {
            List<LoginInfo> loginInfoList = loginInfoManager.getByLoginName(loginName);
            if (CollectionUtils.isNotEmpty(loginInfoList)) {
                if (loginInfoList.size() > 1) {
                    LOGGER.error("loginInfo table has too many user:" + loginName);
                    throw new LoginAuthFailedException();
                }
                loginInfo = loginInfoList.get(0);
                enterprise = enterpriseManager.getById(loginInfo.getEnterpriseId());
            }

/*          //
            if (enterprise == null) {
                PageRequest request = new PageRequest();
                request.setSize(Constants.DEFAULT_OTHER_SIZE);
                Page<Enterprise> enterprisePage = enterpriseManager.getFilterd(null, null, request);
                List<Enterprise> enterpriseList = enterprisePage.getContent();
                if (enterpriseList == null) {
                    LOGGER.error("enterpriseList is null");
                    throw new InternalServerErrorException();
                }
                if (enterpriseList.size() != 1) {
                    LOGGER.error("enterpriseList out of range size:" + enterpriseList.size());
                    throw new NoSuchUserException("enterpriseList out of range size:" + enterpriseList.size());
                }
                enterprise = enterpriseList.get(0);
            }*/
        } else {
            loginInfo = loginInfoManager.getByDomain(loginName, domainName);
            enterprise = enterpriseManager.getByDomainName(domainName);
        }

        if (enterprise == null) {
            LOGGER.error("invalid domain:" + domainName);
            throw new InvalidParamterException();
        }
        
        enterpriseAccount = enterpriseAccountManager.getByEnterpriseApp(enterprise.getId(), appId);
        if (null == enterpriseAccount) {
            LOGGER.error("enterpriseAccount is null appId:" + appId + " enterpriseId:" + enterprise.getId());
            throw new InternalServerErrorException();
        }

        if (loginInfo == null) {
            EnterpriseUser searchUser = getLdapUser(enterprise.getId(), loginName);
            if (searchUser != null) {
                loginInfo = new LoginInfo();
            } else {
                throw new NoSuchUserException();
            }
        }
        
        loginInfo.setEnterpriseId(enterprise.getId());
        loginInfo.setDomainName(enterprise.getDomainName());
        loginInfo.setAccountId(enterpriseAccount.getAccountId());
        return loginInfo;
    }
    
    private EnterpriseUser getLdapUser(long enterpriseId, String loginName)
    {
        List<AuthServer> authServerList = authServerManager.getByEnterpriseId(enterpriseId);
        if (CollectionUtils.isEmpty(authServerList))
        {
            return null;
        }
        
        EnterpriseUser selEnterpriseUser = null;
        AuthServiceManager authServiceManager;
        for (AuthServer authServer : authServerList)
        {
            if (StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType()))
            {
                continue;
            }
            authServiceManager = authServerManager.getAuthService(authServer.getId());
            selEnterpriseUser = authServiceManager.getUserByLoginName(authServer.getId(), loginName);
            if (null != selEnterpriseUser)
            {
                break;
            }
        }
        return selEnterpriseUser;
    }
    
    @Override
    public AuthServer getNtlmAuthServer(String appId)
    {
        Enterprise enterprise = null;
        EnterpriseAccount enterpriseAccount = null;
        AuthServer authServer = null;
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_OTHER_SIZE);
        Page<Enterprise> enterprisePage = enterpriseManager.getFilterd(null, null, request);
        List<Enterprise> enterpriseList = enterprisePage.getContent();
        if (enterpriseList == null)
        {
            LOGGER.error("enterpriseList is null");
            throw new InternalServerErrorException();
        }
        if (enterpriseList.size() != 1)
        {
            LOGGER.error("enterpriseList out of range size:" + enterpriseList.size());
            throw new InternalServerErrorException();
        }
        enterprise = enterpriseList.get(0);
        enterpriseAccount = enterpriseAccountManager.getByEnterpriseApp(enterprise.getId(), appId);
        if (null == enterpriseAccount)
        {
            LOGGER.error("enterpriseAccount is null appId:" + appId + " enterpriseId:" + enterprise.getId());
            throw new InternalServerErrorException();
        }
        List<AuthServer> authServerList = authServerManager.getByEnterpriseId(enterprise.getId());
        authServer = checkAndGetADAuthServer(authServerList);
        AccountAuthserver accountAuthserver = accountAuthServerManager.getByAccountAuthId(enterpriseAccount.getAccountId(),
            authServer.getId());
        if (null == accountAuthserver)
        {
            LOGGER.error("authServer is unbind appId:" + appId + " enterpriseId:" + enterprise.getId()
                + " accountId:" + enterpriseAccount.getAccountId());
            throw new InternalServerErrorException();
        }
        return authServer;
    }
    
    @Override
    public EnterpriseUser authenticate(String name, String password, LoginInfo loginInfo, String realIp)
    {
        Enterprise enterprise = enterpriseManager.getById(loginInfo.getEnterpriseId());
        if (null == enterprise)
        {
            return null;
        }
        EnterpriseUser ldapEnterpriseUser = null;
        if (null != enterprise.getNetworkAuthStatus()
            && (Enterprise.NETWORK_ENABLE == enterprise.getNetworkAuthStatus()))
        {
            ldapEnterpriseUser = loginAuthManager.authenticateByNetwork(name,
                password,
                loginInfo,
                loginInfo.getEnterpriseId(),
                realIp);
        }
        else
        {
            ldapEnterpriseUser = loginAuthManager.authenticate(name,
                password,
                loginInfo,
                loginInfo.getEnterpriseId());
        }
        return ldapEnterpriseUser;
    }
    
    private AuthServer checkAndGetADAuthServer(List<AuthServer> authServerList)
    {
        AuthServer reAuthServer = null;
        if (null == authServerList || authServerList.size() < 1)
        {
            LOGGER.error("signEnterprise authServer is null");
            throw new InternalServerErrorException();
        }
        int countAD = 0;
        for (AuthServer authServer : authServerList)
        {
            if (AuthServer.AUTH_TYPE_AD.equalsIgnoreCase(authServer.getType()))
            {
                countAD = countAD + 1;
                reAuthServer = authServer;
            }
        }
        if (countAD != 1)
        {
            LOGGER.error("there is too much ad authservers countAD:" + countAD);
            throw new InternalServerErrorException();
        }
        return reAuthServer;
    }
}
