package com.huawei.sharedrive.app.account.dao;


import com.huawei.sharedrive.app.account.domain.AccountAccessKey;

public interface AccountAccessKeyDao
{
    void create(AccountAccessKey accessKey);
    
    AccountAccessKey getById(String id);
    
}
