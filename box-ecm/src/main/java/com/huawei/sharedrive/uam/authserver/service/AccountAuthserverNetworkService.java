package com.huawei.sharedrive.uam.authserver.service;

import java.util.List;

import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserverNetwork;

import pw.cdmi.box.domain.Limit;

public interface AccountAuthserverNetworkService
{
    
    int getNetworkCount(Long authServerId, Long accountId);
    
    List<AccountAuthserverNetwork> getNetworkList(Long authServerId, Long accountId, Limit limit);
    
    void create(AccountAuthserverNetwork accountAuthserverNetwork);
    
    void update(AccountAuthserverNetwork accountAuthserverNetwork);
    
    void deleteByIds(String ids);
    
    void deleteAll(Long authServerId, Long accountId);
    
    AccountAuthserverNetwork getById(Long id);
    
    List<AccountAuthserverNetwork> getNetworkListByAccount(Long accountId);
}
