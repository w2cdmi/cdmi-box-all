package com.huawei.sharedrive.uam.enterpriseuser.manager;

import com.huawei.sharedrive.uam.enterpriseuser.domain.LdapCheck;

public interface LdapCheckManager
{
    void insert(LdapCheck ldapCheck);
    
    LdapCheck get(Long enterpriseId);
    
    void update(LdapCheck ldapCheck);
}
