package com.huawei.sharedrive.uam.authserver.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverManager;
import com.huawei.sharedrive.uam.authserver.service.AccountAuthserverService;
import com.huawei.sharedrive.uam.authserver.service.AuthServerService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.exception.NoSuchAuthServerException;

import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

@Component
public class AccountAuthserverManagerImpl implements AccountAuthserverManager
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountAuthserverManagerImpl.class);
    
    @Autowired
    private AccountAuthserverService accountAuthserverService;
    
    @Autowired
    private EnterpriseAccountService enterpriseAccountService;
    
    @Autowired
    private AuthServerService authServerService;
    
    @Override
    public void bindApp(Long authServerId, Long accountId, byte type)
    {
        AuthServer authServer = authServerService.getAuthServer(authServerId);
        if (null == authServer)
        {
            LOGGER.error("authServer is null authServerId:" + authServerId);
            throw new NoSuchAuthServerException("authServer is null authServerId:" + authServerId);
        }
        if (type == AccountAuthserver.UNDEFINED_OPEN_ACCOUNT)
        {
            type = AccountAuthserver.AUTO_OPEN_ACCOUNT;
        }
        if (StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType()))
        {
            type = AccountAuthserver.MANUAL_OPEN_ACCOUNT;
        }
        AccountAuthserver accountAuthserver = accountAuthserverService.getByAccountAuthId(accountId,
            authServerId);
        if (null != accountAuthserver)
        {
            accountAuthserver.setType(type);
            accountAuthserverService.update(accountAuthserver);
        }
        else
        {
            accountAuthserver = new AccountAuthserver();
            accountAuthserver.setAuthServerId(authServerId);
            accountAuthserver.setAccountId(accountId);
            accountAuthserver.setType(type);
            accountAuthserverService.create(accountAuthserver);
        }
    }
    
    @Override
    public int unBindApp(Long authServerId, Long accountId)
    {
        AuthServer authServer = authServerService.getAuthServer(authServerId);
        if (null == authServer)
        {
            LOGGER.error("authServer is null authServerId:" + authServerId);
            throw new NoSuchAuthServerException("authServer is null authServerId:" + authServerId);
        }
        return accountAuthserverService.delete(accountId, authServerId);
    }
    
    @Override
    public List<AccountAuthserver> listBindApp(Long enterpriseId, Long authServerId)
    {
        List<EnterpriseAccount> enterpriseAccountList = enterpriseAccountService.getAppContextByEnterpriseId(enterpriseId);
        List<AccountAuthserver> accountAuthserverList = new ArrayList<AccountAuthserver>(10);
        if (null == enterpriseAccountList || enterpriseAccountList.size() <= 0)
        {
            LOGGER.error("enterpriseAccountList is null enterpriseId:" + enterpriseId);
            return null;
        }
        long accountId;
        AccountAuthserver accountAuthserver;
        for (EnterpriseAccount enterpriseAccount : enterpriseAccountList)
        {
            accountId = enterpriseAccount.getAccountId();
            accountAuthserver = accountAuthserverService.getByAccountAuthId(accountId,
                authServerId);
            if (null == accountAuthserver)
            {
                LOGGER.warn("accountAuthserver is null accountId:" + accountId + " enterpriseId:"
                    + enterpriseId);
                continue;
            }
            accountAuthserver.setIsBind(true);
            accountAuthserver.setAuthAppId(enterpriseAccount.getAuthAppId());
            accountAuthserver.setDescription(enterpriseAccount.getDescription());
            accountAuthserverList.add(accountAuthserver);
        }
        return accountAuthserverList;
    }
    
    @Override
    public List<AccountAuthserver> listUnBindApp(Long enterpriseId, Long authServerId)
    {
        List<EnterpriseAccount> enterpriseAccountList = enterpriseAccountService.getByEnterpriseId(enterpriseId);
        List<AccountAuthserver> accountAuthserverList = new ArrayList<AccountAuthserver>(10);
        if (null == enterpriseAccountList || enterpriseAccountList.size() <= 0)
        {
            LOGGER.error("enterpriseAccountList is null enterpriseId:" + enterpriseId);
            return null;
        }
        long accountId;
        AccountAuthserver accountAuthserver;
        for (EnterpriseAccount enterpriseAccount: enterpriseAccountList)
        {
            accountId = enterpriseAccount.getAccountId();
            accountAuthserver = accountAuthserverService.getByAccountAuthId(accountId,
                authServerId);
            if (null == accountAuthserver)
            {
                LOGGER.warn("accountAuthserver is null accountId:" + accountId + " enterpriseId:"
                    + enterpriseId);
                accountAuthserver = new AccountAuthserver();
                accountAuthserver.setAuthServerId(authServerId);
                accountAuthserver.setAccountId(accountId);
                accountAuthserver.setType(AccountAuthserver.UNDEFINED_OPEN_ACCOUNT);
                accountAuthserver.setAuthAppId(enterpriseAccount.getAuthAppId());
                accountAuthserverList.add(accountAuthserver);
                continue;
            }
        }
        return accountAuthserverList;
    }
    
    @Override
    public AccountAuthserver getByAccountAuthId(Long accountId, Long authServerId)
    {
        return accountAuthserverService.getByAccountAuthId(accountId, authServerId);
    }
    
    @Override
    public int getAccountId(long authServerId, long enterpriseId, String authAppId)
    {
        
        return accountAuthserverService.getAccountId(authServerId, enterpriseId, authAppId);
    }
}
