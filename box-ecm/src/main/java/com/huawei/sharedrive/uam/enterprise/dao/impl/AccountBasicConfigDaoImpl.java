package com.huawei.sharedrive.uam.enterprise.dao.impl;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.dao.AccountBasicConfigDao;
import com.huawei.sharedrive.uam.enterprise.domain.AccountBasicConfig;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Component
@SuppressWarnings("deprecation")
@Deprecated
public class AccountBasicConfigDaoImpl extends CacheableSqlMapClientDAO implements AccountBasicConfigDao
{
    
    @Override
    public void insert(AccountBasicConfig appBasicConfig)
    {
        sqlMapClientTemplate.insert("AccountBasicConfig.insert", appBasicConfig);
    }
    
    @Override
    public void update(AccountBasicConfig appBasicConfig)
    {
        sqlMapClientTemplate.update("AccountBasicConfig.update", appBasicConfig);
    }
    
    @Override
    public AccountBasicConfig get(AccountBasicConfig appBasicConfig)
    {
        return (AccountBasicConfig) sqlMapClientTemplate.queryForObject("AccountBasicConfig.get",
            appBasicConfig);
    }
    
    @Override
    public int getAccountIdNum(AccountBasicConfig appBasicConfig)
    {
        return (int) sqlMapClientTemplate.queryForObject("AccountBasicConfig.getAidNum", appBasicConfig);
    }
    
}
