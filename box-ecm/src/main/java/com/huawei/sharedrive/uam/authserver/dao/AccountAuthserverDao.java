package com.huawei.sharedrive.uam.authserver.dao;

import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;

public interface AccountAuthserverDao
{
    
    void create(AccountAuthserver accountAuthserver);
    
    AccountAuthserver getByAccountAuthId(Long accountId, Long authServerId);
    
    void update(AccountAuthserver accountAuthserver);
    
    int delete(Long accountId, Long authServerId);
    
    void deleteByAuthServerId(Long authServerId);
    
    int getAccountId(long authServerId, long enterpriseId, String authAppId);
    
}
