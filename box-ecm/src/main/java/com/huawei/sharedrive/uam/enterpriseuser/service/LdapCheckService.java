package com.huawei.sharedrive.uam.enterpriseuser.service;

import com.huawei.sharedrive.uam.enterpriseuser.domain.LdapCheck;

public interface LdapCheckService
{
    void insert(LdapCheck ldapCheck);
    
    LdapCheck get(Long enterpriseId);
    
    void update(LdapCheck ldapCheck);
}
