package com.huawei.sharedrive.uam.enterprise.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.domain.AccountSecConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccountSecConfigExt;
import com.huawei.sharedrive.uam.enterprise.manager.AccountSecManager;
import com.huawei.sharedrive.uam.enterprise.service.AccountSecConfigService;

@Component
public class AccountSecManagerImpl implements AccountSecManager
{
    @Autowired
    private AccountSecConfigService accountSecService;
    
    @Override
    public AccountSecConfig getSwitch(Integer accountId)
    {
        return accountSecService.get(accountId);
    }
    
    @Override
    public void updateSwitch(AccountSecConfigExt configExt)
    {
        accountSecService.setConfig(configExt.getAccountId(),
            configExt.getEnableSpaceSec(),
            configExt.getEnableFileSec(),
            configExt.getEnableFileCopySec());
    }
    
}
