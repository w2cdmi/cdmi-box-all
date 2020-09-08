package com.huawei.sharedrive.uam.enterprise.dao;

import com.huawei.sharedrive.uam.enterprise.domain.AccountBasicConfig;

@Deprecated
public interface AccountBasicConfigDao
{
    void insert(AccountBasicConfig appBasicConfig);
    
    void update(AccountBasicConfig appBasicConfig);
    
    AccountBasicConfig get(AccountBasicConfig appBasicConfig);
    
    int getAccountIdNum(AccountBasicConfig appBasicConfig);
}
