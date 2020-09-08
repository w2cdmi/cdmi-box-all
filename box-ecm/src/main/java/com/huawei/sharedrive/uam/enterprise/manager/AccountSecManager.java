package com.huawei.sharedrive.uam.enterprise.manager;

import com.huawei.sharedrive.uam.enterprise.domain.AccountSecConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccountSecConfigExt;

public interface AccountSecManager
{
    AccountSecConfig getSwitch(Integer accountId);
    
    void updateSwitch(AccountSecConfigExt configExt);
}
