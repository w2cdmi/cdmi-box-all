package com.huawei.sharedrive.uam.enterprise.service;

import com.huawei.sharedrive.uam.enterprise.domain.AccountSecConfig;

public interface AccountSecConfigService
{
    void setConfig(int accountId, Byte enableSpaceSec, Byte enalbeFileSec, Byte enalbeFileCopySec);
    
    AccountSecConfig get(int accountId);
    
}
