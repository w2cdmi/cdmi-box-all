package com.huawei.sharedrive.uam.enterpriseuser.dao.impl;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterpriseuser.dao.LdapCheckDAO;
import com.huawei.sharedrive.uam.enterpriseuser.domain.LdapCheck;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@SuppressWarnings({"deprecation"})
@Service
public class LdapCheckDAOImpl extends CacheableSqlMapClientDAO implements LdapCheckDAO
{
    @Override
    public void insert(LdapCheck ldapCheck)
    {
        sqlMapClientTemplate.insert("LdapCheck.insert", ldapCheck);
    }
    
    @Override
    public LdapCheck get(Long enterpriseId)
    {
        if (null == enterpriseId)
        {
            return null;
        }
        return (LdapCheck) sqlMapClientTemplate.queryForObject("LdapCheck.get", enterpriseId);
    }
    
    @Override
    public void update(LdapCheck ldapCheck)
    {
        sqlMapClientTemplate.update("LdapCheck.update", ldapCheck);
    }
}
