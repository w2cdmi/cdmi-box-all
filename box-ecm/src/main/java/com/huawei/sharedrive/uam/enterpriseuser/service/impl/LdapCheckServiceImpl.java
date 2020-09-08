package com.huawei.sharedrive.uam.enterpriseuser.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterpriseuser.dao.LdapCheckDAO;
import com.huawei.sharedrive.uam.enterpriseuser.domain.LdapCheck;
import com.huawei.sharedrive.uam.enterpriseuser.service.LdapCheckService;

@Component
public class LdapCheckServiceImpl implements LdapCheckService
{
    
    @Autowired
    private LdapCheckDAO ldapCheckDAO;
    
    @Override
    public void insert(LdapCheck ldapCheck)
    {
        ldapCheckDAO.insert(ldapCheck);
    }
    
    @Override
    public LdapCheck get(Long enterpriseId)
    {
        return ldapCheckDAO.get(enterpriseId);
    }
    
    @Override
    public void update(LdapCheck ldapCheck)
    {
        ldapCheckDAO.update(ldapCheck);
    }
}
