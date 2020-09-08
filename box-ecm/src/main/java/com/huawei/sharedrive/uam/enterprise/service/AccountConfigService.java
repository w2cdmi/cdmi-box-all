package com.huawei.sharedrive.uam.enterprise.service;

import java.util.List;

import pw.cdmi.common.domain.AccountConfig;
public interface AccountConfigService
{
    void create(AccountConfig accountConfig);
    
    AccountConfig get(long accountId, String name);
    
    List<AccountConfig> list(long accountId);
    
    void update(AccountConfig accountConfig);

    void save(AccountConfig config);
}
