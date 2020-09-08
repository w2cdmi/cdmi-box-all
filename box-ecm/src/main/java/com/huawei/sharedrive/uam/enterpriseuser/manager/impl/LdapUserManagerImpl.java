package com.huawei.sharedrive.uam.enterpriseuser.manager.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerConfigManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;
import com.huawei.sharedrive.uam.enterpriseuser.manager.LdapUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.UserLdapService;
import com.huawei.sharedrive.uam.ldapauth.manager.impl.LdapAuthServiceManagerImpl;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;

@Component
public class LdapUserManagerImpl implements LdapUserManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserManagerImpl.class);
    
    @Autowired
    private UserLdapService userLdapService;
    
    @Autowired
    private LdapAuthServiceManagerImpl ldapAuthServiceManager;
    
    @Autowired
    private AuthServerConfigManager authServerConfigManager;
    
    @Override
    public void insertLdapUser(String sessionId, String dn, Long authServerId)
    {
        List<UserLdap> userLdapList = userLdapService.getByUserLdap(sessionId, dn, authServerId);
        if (userLdapList == null || userLdapList.size() < 1)
        {
            LOGGER.info("find nul userLdapList  dn:" + dn + " authServerId:"
                + authServerId);
            LdapDomainConfig ldapDomainConfig = authServerConfigManager.getAuthServerObject(authServerId);
            List<EnterpriseUser> all = ldapAuthServiceManager.listAllUsers(ldapDomainConfig,
                authServerId,
                dn,
                true);
            if (CollectionUtils.isNotEmpty(all))
            {
                userLdapService.insertList(sessionId, dn, authServerId, all);
            }
        }
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public Page<EnterpriseUser> getPagedUserExtend(String sessionId, String dn, Long authServerId,
        Long enterpriseId, String filter, PageRequest pageRequest)
    {
        UserLdap userLdap = new UserLdap();
        EnterpriseUser enterpriseUser = new EnterpriseUser();
        userLdap.setAuthServerId(authServerId);
        userLdap.setDn(dn);
        userLdap.setSessionId(sessionId);
        enterpriseUser.setName(filter);
        enterpriseUser.setEnterpriseId(enterpriseId);
        Page<EnterpriseUser> page = userLdapService.getPagedUser(userLdap, enterpriseUser, pageRequest);
        return page;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public Page<UserAccount> getPagedUserAccount(UserLdap userLdap, Long enterpriseId, Long accountId,
        String filter, Integer status, PageRequest pageRequest)
    {
        return userLdapService.getPagedUserAccount(userLdap,
            enterpriseId,
            accountId,
            filter,
            status,
            pageRequest);
    }
}
