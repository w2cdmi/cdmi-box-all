package com.huawei.sharedrive.uam.enterpriseuser.manager.impl;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerConfigManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserCheckManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.SyncEnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.exception.AccountAuthFailedException;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.LicenseException;
import com.huawei.sharedrive.uam.ldapauth.manager.impl.LdapAuthServiceManagerImpl;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;

@Component
public class SyncEnterpriseUserManagerImpl implements SyncEnterpriseUserManager
{
    private final static Logger LOGGER = LoggerFactory.getLogger(SyncEnterpriseUserManagerImpl.class);
    
    @Autowired
    private LdapAuthServiceManagerImpl ldapAuthServiceManager;
    
    @Autowired
    private AuthServerConfigManager authServerConfigManager;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private EnterpriseUserManager enterpriseUserManager;
    
    @Autowired
    private EnterpriseUserService enterpriseUserService;
    
    @Autowired
    private EnterpriseUserCheckManager enterpriseUserCheckManager;
    
    @Autowired
    private UserAccountManager userAccountManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @Autowired
    private AccountAuthserverManager accountAuthserverManager;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @Override
    public boolean syncEnterpriseUser(Long authServerId, LogOwner logOwner)
    {
        LOGGER.info("sync user begin authServerId:" + authServerId);
        AuthServer authServer = authServerManager.getAuthServer(authServerId);
        Long enterpriseId = authServer.getEnterpriseId();
        LdapDomainConfig ldapDomainConfig = authServerConfigManager.getAuthServerObject(authServerId);
        // start sync user log
        String syncNodes = ldapDomainConfig.getLdapNodeFilterConfig().getSyncNode();
        String[] description = new String[]{authServer.getName(), syncNodes};
        logOwner.setAuthServerId(authServerId);
        logOwner.setEnterpriseId(enterpriseId);
        adminLogManager.saveAdminLog(logOwner, AdminLogType.KEY_SYNC_USER_START, description);
        if (StringUtils.isBlank(syncNodes))
        {
            description = new String[]{authServer.getName()};
            adminLogManager.saveAdminLog(logOwner, AdminLogType.KEY_SYNC_USER_START_ERROR, description);
            return false;
        }
        List<EnterpriseUser> enterpriseUserList = ldapAuthServiceManager.listAllUsers(ldapDomainConfig,
            authServerId,
            null,
            false);
        if (null == enterpriseUserList || enterpriseUserList.size() <= 0)
        {
            LOGGER.warn("enterpriseUserList is null,sync user failed authServerId:" + authServerId);
            description = new String[]{authServer.getName()};
            adminLogManager.saveAdminLog(logOwner, AdminLogType.KEY_SYNC_USER_START_ERROR, description);
            return false;
        }
        
        boolean result = true;
        int failCount = 0;
        for (EnterpriseUser enterpriseUser : enterpriseUserList)
        {
            try
            {
                enterpriseUser.setEnterpriseId(enterpriseId);
                enterpriseUser.setUserSource(authServerId);
                
                syncEnterpriseUser(enterpriseUser);
            }
            catch (LicenseException e)
            {
                LOGGER.warn("License is invalid", e);
                result = false;
                failCount++;
                saveFailSyncLog(logOwner, authServer, enterpriseUser.getName(), "License is invalid");
                break;
            }
            catch (DuplicateKeyException e)
            {
                LOGGER.warn("The key is duplicate");
                result = false;
                failCount++;
                saveFailSyncLog(logOwner, authServer, enterpriseUser.getName(), "The key is duplicate");
                continue;
            }
            catch (AccountAuthFailedException e)
            {
                LOGGER.warn("EnterpriseAccount status is disable");
                result = false;
                failCount++;
                saveFailSyncLog(logOwner,
                    authServer,
                    enterpriseUser.getName(),
                    "EnterpriseAccount status is disable");
                continue;
            }
            catch (InternalServerErrorException e)
            {
                LOGGER.warn("Create account user fail");
                result = false;
                failCount++;
                saveFailSyncLog(logOwner, authServer, enterpriseUser.getName(), "Create account user fail");
                continue;
            }
            catch (InvalidParamterException e)
            {
                LOGGER.warn("Enterprise User parameter error");
                result = false;
                failCount++;
                saveFailSyncLog(logOwner, authServer, enterpriseUser.getName(), "Enterprise User parameter error");
                continue;
            }
            catch (Exception e)
            {
                LOGGER.warn("Sync user failed", e);
                saveFailSyncLog(logOwner, authServer, enterpriseUser.getName(), "Sync user failed");
                result = false;
                failCount++;
            }
        }
        // end sync user log
        int syncUserTotal = enterpriseUserList.size();
        int successTotal = syncUserTotal - failCount;
        int failTotal = failCount;
        description = new String[]{authServer.getName(), String.valueOf(syncUserTotal),
            String.valueOf(successTotal), String.valueOf(failTotal)};
        adminLogManager.saveAdminLog(logOwner, AdminLogType.KEY_SYNC_USER_END, description);
        return result;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void syncEnterpriseUser(EnterpriseUser enterpriseUser) throws IOException
    {
        EnterpriseUser selEnterpriseUser = enterpriseUserManager.getByObjectSid(enterpriseUser.getObjectSid(),
            enterpriseUser.getEnterpriseId());
        
        if (null == selEnterpriseUser)
        {
            long id = enterpriseUserManager.createLdap(enterpriseUser);
            createAccountUsers(id, enterpriseUser.getEnterpriseId(), enterpriseUser.getUserSource());
        }
        else
        {
            Enterprise enterprise = enterpriseManager.getById(selEnterpriseUser.getEnterpriseId());
            if (null == enterprise)
            {
                LOGGER.error("enterprise is null enterpriseId:" + selEnterpriseUser.getEnterpriseId());
                return;
            }
            if (!enterpriseUserCheckManager.isUpdateByLdap(selEnterpriseUser, enterpriseUser))
            {
                return;
            }
            enterpriseUserManager.updateLdap(enterpriseUser,
                selEnterpriseUser.getId(),
                selEnterpriseUser.getEnterpriseId());
        }
    }
    
    @Override
    public void syncDeleteEnterpriseUser(Long enterpriseId)
    {
        LOGGER.info("sync delete user begin enterpriseId:" + enterpriseId);
        if (null == enterpriseId)
        {
            LOGGER.warn("enterpriseId is null");
            return;
        }
        List<AuthServer> list = authServerManager.getByEnterpriseId(enterpriseId);
        if (null == list || list.size() < 1)
        {
            LOGGER.warn("AuthServerList is null enterpriseId:" + enterpriseId);
            return;
        }
        Long localAuthServerId = null;
        for (AuthServer authServer : list)
        {
            if (StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType()))
            {
                localAuthServerId = authServer.getId();
                break;
            }
        }
        List<EnterpriseUser> enterpriseUserList = enterpriseUserService.getAllADEnterpriseUser(enterpriseId,
            localAuthServerId);
        if (null == enterpriseUserList || enterpriseUserList.size() < 1)
        {
            LOGGER.warn("enterpriseUserList is null,sync delete user failed enterpriseId:" + enterpriseId);
            return;
        }
        
        if (null == localAuthServerId)
        {
            LOGGER.warn("there is not have local auth server enterpriseId:" + enterpriseId);
            return;
        }
        for (AuthServer authServer : list)
        {
            if (localAuthServerId == authServer.getId())
            {
                continue;
            }
            ldapAuthServiceManager.checkDeleteUsers(authServer.getId(), localAuthServerId, enterpriseUserList);
        }
        enterpriseUserService.updateLdapStatus(enterpriseUserList, EnterpriseUser.AD_NOTEXISTS, enterpriseId);
        enterpriseUserService.updateLdapStatusByNotIn(enterpriseUserList,
            EnterpriseUser.AD_EXISTS,
            enterpriseId);
    }
    
    @Override
    public int getByLdapStatusCount(Long enterpriseId)
    {
        return enterpriseUserService.getByLdapStatusCount(EnterpriseUser.AD_NOTEXISTS, enterpriseId);
    }
    
    @Override
    public List<EnterpriseUser> listByLdapStatus(Long enterpriseId, Limit limit)
    {
        return enterpriseUserService.getByLdapStatus(EnterpriseUser.AD_NOTEXISTS, enterpriseId, limit);
    }
    
    private void createAccountUsers(long userId, long enterpriseId, long authServerId)
    {
        List<EnterpriseAccount> list = enterpriseAccountManager.getByEnterpriseId(enterpriseId);
        AccountAuthserver accountAuthserver;
        for (EnterpriseAccount enterpriseAccount : list)
        {
            accountAuthserver = accountAuthserverManager.getByAccountAuthId(enterpriseAccount.getAccountId(),
                authServerId);
            if (null != accountAuthserver
                && AccountAuthserver.AUTO_OPEN_ACCOUNT == accountAuthserver.getType())
            {
                userAccountManager.create(userId, enterpriseId, enterpriseAccount.getAuthAppId(),null);
            }
            
        }
    }
    
    private void saveFailSyncLog(LogOwner logOwner, AuthServer authServer, String name, String messages)
    {
        try
        {
            Enterprise enterprise = enterpriseManager.getById(logOwner.getEnterpriseId());
            String enterpriseName = null;
            String authName = null;
            if (null != enterprise)
            {
                enterpriseName = enterprise.getName();
            }
            if (null != authServer)
            {
                authName = authServer.getName();
            }
            String[] description = new String[]{enterpriseName, authName, name, messages};
            adminLogManager.saveAdminLog(logOwner, AdminLogType.KEY_SYNC_USER_ERROR, description);
        }
        catch (Exception e)
        {
            LOGGER.warn("save sync user systemLog fail", e);
        }
    }
}
