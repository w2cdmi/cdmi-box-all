package com.huawei.sharedrive.uam.enterpriseuser.dao;

import com.huawei.sharedrive.uam.enterpriseuser.domain.LdapCheck;

public interface LdapCheckDAO
{
    
    void insert(LdapCheck ldapCheck);
    
    LdapCheck get(Long enterpriseId);
    
    void update(LdapCheck ldapCheck);
}