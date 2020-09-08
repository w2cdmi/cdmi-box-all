package com.huawei.sharedrive.uam.enterprise.dao;

import java.util.List;

import pw.cdmi.common.domain.AccountConfig;
public interface AccountConfigDao
{
    void create(AccountConfig accountConfig);
    
    AccountConfig get(long accountId, String name);
    
    List<AccountConfig> list(long accountId);

    List<AccountConfig> listWithPrefix(long accountId, String prefix);

    int update(AccountConfig accountConfig);
}
