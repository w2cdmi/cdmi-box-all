package com.huawei.sharedrive.uam.enterprise.dao;

import com.huawei.sharedrive.uam.enterprise.domain.AccountSecConfig;

public interface AccountSecConfigDao
{
    
    void create(AccountSecConfig accountSecConfig);
    
    void update(AccountSecConfig accountSecConfig);
    
    AccountSecConfig get(int account);
}
