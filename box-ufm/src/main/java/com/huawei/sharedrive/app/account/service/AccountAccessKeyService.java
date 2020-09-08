package com.huawei.sharedrive.app.account.service;

import com.huawei.sharedrive.app.account.domain.AccountAccessKey;

public interface AccountAccessKeyService
{
    void create(AccountAccessKey accessKey);
    
    AccountAccessKey getById(String id);
}
