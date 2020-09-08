package com.huawei.sharedrive.app.account.mamager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.account.domain.AccountAccessKey;
import com.huawei.sharedrive.app.account.mamager.AccountAccessKeyManager;
import com.huawei.sharedrive.app.account.service.AccountAccessKeyService;
import com.huawei.sharedrive.app.exception.NoSuchAccessKeyException;

@Component
public class AccountAccessKeyManagerImpl implements AccountAccessKeyManager
{
    
    @Autowired
    private AccountAccessKeyService accessKeyService;
    
    @Override
    public AccountAccessKey getAccountKeyById(String appId, String id)
    {
        AccountAccessKey accessKey = accessKeyService.getById(id);
        if (accessKey == null)
        {
            throw new NoSuchAccessKeyException();
        }
        return accessKey;
    }
    
}
