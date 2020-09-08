package com.huawei.sharedrive.uam.authserver.service;

import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;

public interface AccountAuthserverService
{
    
    void create(AccountAuthserver accountAuthserver);
    
    void update(AccountAuthserver accountAuthserver);
    
    int delete(Long accountId, Long authServerId);
    
    AccountAuthserver getByAccountAuthId(Long accountId, Long authServerId);
    
    void deleteByAuthServerId(Long authServerId);
    
    int getAccountId(long authServerId, long enterpriseId, String authAppId);
    
}
