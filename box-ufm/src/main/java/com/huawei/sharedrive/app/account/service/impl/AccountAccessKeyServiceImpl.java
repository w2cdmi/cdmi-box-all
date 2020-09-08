package com.huawei.sharedrive.app.account.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.account.dao.AccountAccessKeyDao;
import com.huawei.sharedrive.app.account.domain.AccountAccessKey;
import com.huawei.sharedrive.app.account.service.AccountAccessKeyService;

@Component
public class AccountAccessKeyServiceImpl implements AccountAccessKeyService
{

    @Autowired
    private AccountAccessKeyDao accessDao;
    
    @Override
    public void create(AccountAccessKey accessKey)
    {
        accessDao.create(accessKey);
    }

    @Override
    public AccountAccessKey getById(String id)
    {
        return accessDao.getById(id);
    }
    
}
