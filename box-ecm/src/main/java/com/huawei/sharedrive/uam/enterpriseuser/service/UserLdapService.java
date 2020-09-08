package com.huawei.sharedrive.uam.enterpriseuser.service;

import java.util.List;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface UserLdapService
{
    
    void insertList(String sessionId, String dn, Long authServerId, List<EnterpriseUser> userList);
    
    List<UserLdap> getByUserLdap(String sessionId, String dn, Long authServerId);
    
    Page<EnterpriseUser> getPagedUser(UserLdap userLdap, EnterpriseUser enterpriseUser,
        PageRequest pageRequest);
    
    List<String> getSessionList();
    
    void deleteBySessionId(String sessionId);
    
    List<String> getFilterdId(UserLdap filter);
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    Page<UserAccount> getPagedUserAccount(UserLdap userLdap, Long enterpriseId, Long accountId,
        String filter, Integer status, PageRequest pageRequest);
    
}
