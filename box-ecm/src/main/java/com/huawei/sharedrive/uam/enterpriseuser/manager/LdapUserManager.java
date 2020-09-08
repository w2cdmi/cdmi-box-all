package com.huawei.sharedrive.uam.enterpriseuser.manager;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

@SuppressWarnings("PMD.ExcessiveParameterList")
public interface LdapUserManager
{
    void insertLdapUser(String sessionId, String dn, Long authServerId);
    
    Page<EnterpriseUser> getPagedUserExtend(String sessionId, String dn, Long authServerId,
        Long enterpriseId, String filter, PageRequest pageRequest);
    
    Page<UserAccount> getPagedUserAccount(UserLdap userLdap, Long enterpriseId, Long accountId,
        String filter, Integer status, PageRequest pageRequest);
}
