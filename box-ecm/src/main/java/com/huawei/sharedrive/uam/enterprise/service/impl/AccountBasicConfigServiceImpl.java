package com.huawei.sharedrive.uam.enterprise.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterprise.dao.AccountBasicConfigDao;
import com.huawei.sharedrive.uam.enterprise.domain.AccountBasicConfig;
import com.huawei.sharedrive.uam.enterprise.service.AccountBasicConfigService;

@Service
@Deprecated
public class AccountBasicConfigServiceImpl implements AccountBasicConfigService
{
    @Autowired
    private AccountBasicConfigDao accountBasicConfigDao;
    
    @Override
    public void insert(AccountBasicConfig appBasicConfig)
    {
        accountBasicConfigDao.insert(appBasicConfig);
    }
    
    @Override
    public void update(AccountBasicConfig appBasicConfig)
    {
        accountBasicConfigDao.update(appBasicConfig);
    }
    
    @Override
    public AccountBasicConfig get(AccountBasicConfig appBasicConfig)
    {
        return accountBasicConfigDao.get(appBasicConfig);
    }
    
    @Override
    public int getAccountIdNum(AccountBasicConfig appBasicConfig)
    {
        return accountBasicConfigDao.getAccountIdNum(appBasicConfig);
    }
    
}
