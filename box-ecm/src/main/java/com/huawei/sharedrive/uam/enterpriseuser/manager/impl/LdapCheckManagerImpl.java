package com.huawei.sharedrive.uam.enterpriseuser.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterpriseuser.domain.LdapCheck;
import com.huawei.sharedrive.uam.enterpriseuser.manager.LdapCheckManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.LdapCheckService;

@Component
public class LdapCheckManagerImpl implements LdapCheckManager
{
    @Autowired
    private LdapCheckService ldapCheckService;
    
    @Override
    public void insert(LdapCheck ldapCheck)
    {
        ldapCheckService.insert(ldapCheck);
    }
    
    @Override
    public LdapCheck get(Long enterpriseId)
    {
        return ldapCheckService.get(enterpriseId);
    }
    
    @Override
    public void update(LdapCheck ldapCheck)
    {
        ldapCheckService.update(ldapCheck);
    }
}
