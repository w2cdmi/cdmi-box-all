package com.huawei.sharedrive.uam.authserver.dao;

import java.util.List;

import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserverNetwork;

import pw.cdmi.box.domain.Limit;

public interface AccountAuthserverNetworkDao
{
    
    long getMaxId();
    
    int getNetworkCount(Long authServerId, Long enterpriseId);
    
    List<AccountAuthserverNetwork> getNetworkList(Long authServerId, Long enterpriseId, Limit limit);
    
    void create(AccountAuthserverNetwork accountAuthserverNetwork);
    
    void update(AccountAuthserverNetwork accountAuthserverNetwork);
    
    void deleteByIds(String ids);
    
    void deleteAll(Long authServerId, Long accountId);
    
    AccountAuthserverNetwork getById(Long id);
    
    List<AccountAuthserverNetwork> getNetworkListByAccount(Long accountId);
}
