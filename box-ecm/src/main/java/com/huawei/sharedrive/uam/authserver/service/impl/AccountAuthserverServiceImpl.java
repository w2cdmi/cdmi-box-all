package com.huawei.sharedrive.uam.authserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.authserver.dao.AccountAuthserverDao;
import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;
import com.huawei.sharedrive.uam.authserver.service.AccountAuthserverService;

@Component
public class AccountAuthserverServiceImpl implements AccountAuthserverService
{
    
    @Autowired
    private AccountAuthserverDao accountAuthserverDao;
    
    @Override
    public void create(AccountAuthserver accountAuthserver)
    {
        accountAuthserverDao.create(accountAuthserver);
    }
    
    @Override
    public void update(AccountAuthserver accountAuthserver)
    {
        accountAuthserverDao.update(accountAuthserver);
    }
    
    @Override
    public int delete(Long accountId, Long authServerId)
    {
        return accountAuthserverDao.delete(accountId, authServerId);
    }
    
    @Override
    public void deleteByAuthServerId(Long authServerId)
    {
        accountAuthserverDao.deleteByAuthServerId(authServerId);
    }
    
    @Override
    public AccountAuthserver getByAccountAuthId(Long accountId, Long authServerId)
    {
        return accountAuthserverDao.getByAccountAuthId(accountId, authServerId);
    }
    
    @Override
    public int getAccountId(long authServerId, long enterpriseId, String authAppId)
    {
        
        return accountAuthserverDao.getAccountId(authServerId, enterpriseId, authAppId);
    }
    
}
