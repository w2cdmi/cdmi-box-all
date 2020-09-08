package com.huawei.sharedrive.uam.authserver.manager;

import java.util.List;

import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;

public interface AccountAuthserverManager
{
    
    void bindApp(Long authServerId, Long accountId, byte type);
    
    int unBindApp(Long authServerId, Long accountId);
    
    List<AccountAuthserver> listBindApp(Long enterpriseId, Long authServerId);
    
    List<AccountAuthserver> listUnBindApp(Long enterpriseId, Long authServerId);
    
    AccountAuthserver getByAccountAuthId(Long accountId, Long authServerId);
    
    int getAccountId(long authServerId, long enterpriseId, String authAppId);
    
}
