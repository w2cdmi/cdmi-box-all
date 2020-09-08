package com.huawei.sharedrive.uam.authserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.authserver.dao.AccountAuthserverNetworkDao;
import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserverNetwork;
import com.huawei.sharedrive.uam.authserver.service.AccountAuthserverNetworkService;
import com.huawei.sharedrive.uam.idgenerate.service.AuthserverNetworkGenerateService;

import pw.cdmi.box.domain.Limit;

@Component
public class AccountAuthserverNetworkServiceImpl implements AccountAuthserverNetworkService
{
    
    @Autowired
    private AccountAuthserverNetworkDao accountAuthserverNetworkDao;
    
    @Autowired
    private AuthserverNetworkGenerateService authserverNetworkGenerateService;
    
    @Override
    public AccountAuthserverNetwork getById(Long id)
    {
        return accountAuthserverNetworkDao.getById(id);
    }
    
    @Override
    public int getNetworkCount(Long authServerId, Long accountId)
    {
        return accountAuthserverNetworkDao.getNetworkCount(authServerId, accountId);
    }
    
    @Override
    public List<AccountAuthserverNetwork> getNetworkList(Long authServerId, Long accountId, Limit limit)
    {
        return accountAuthserverNetworkDao.getNetworkList(authServerId, accountId, limit);
    }
    
    @Override
    public List<AccountAuthserverNetwork> getNetworkListByAccount(Long accountId)
    {
        return accountAuthserverNetworkDao.getNetworkListByAccount(accountId);
    }
    
    @Override
    public void create(AccountAuthserverNetwork accountAuthserverNetwork)
    {
        AccountAuthserverNetwork tempAccountAuthserverNetwork = new AccountAuthserverNetwork();
        tempAccountAuthserverNetwork.setAccountId(accountAuthserverNetwork.getAccountId());
        tempAccountAuthserverNetwork.setAuthServerId(accountAuthserverNetwork.getAuthServerId());
        tempAccountAuthserverNetwork.setIpEnd(accountAuthserverNetwork.getIpEnd());
        tempAccountAuthserverNetwork.setIpStart(accountAuthserverNetwork.getIpStart());
        tempAccountAuthserverNetwork.setIpEndValue(accountAuthserverNetwork.getIpEndValue());
        tempAccountAuthserverNetwork.setIpStartValue(accountAuthserverNetwork.getIpStartValue());
        long id = authserverNetworkGenerateService.getNextId();
        tempAccountAuthserverNetwork.setId(id);
        accountAuthserverNetworkDao.create(tempAccountAuthserverNetwork);
    }
    
    @Override
    public void update(AccountAuthserverNetwork accountAuthserverNetwork)
    {
        accountAuthserverNetworkDao.update(accountAuthserverNetwork);
    }
    
    @Override
    public void deleteByIds(String ids)
    {
        accountAuthserverNetworkDao.deleteByIds(ids);
    }
    
    @Override
    public void deleteAll(Long authServerId, Long accountId)
    {
        accountAuthserverNetworkDao.deleteAll(authServerId, accountId);
    }
}
