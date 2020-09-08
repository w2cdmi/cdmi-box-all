package com.huawei.sharedrive.uam.enterprise.manager;

import com.huawei.sharedrive.uam.enterprise.domain.AccountBasicConfig;

@Deprecated
public interface AccountBasicConfigManager
{
    AccountBasicConfig queryAccountBasicConfig(AccountBasicConfig appBasicConfig,String appId);
    
    void addAccountBasicConfig(AccountBasicConfig appBasicConfig);
    
    boolean paramCheck(AccountBasicConfig appBasicConfig);
    
    void convertToNull(AccountBasicConfig appBasicConfig);
    
    boolean rangeCheck(String param, Integer max, Integer min);
}
