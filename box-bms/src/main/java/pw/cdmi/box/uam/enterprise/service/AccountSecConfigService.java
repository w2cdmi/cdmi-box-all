package pw.cdmi.box.uam.enterprise.service;

import pw.cdmi.box.uam.enterprise.domain.AccountSecConfig;

public interface AccountSecConfigService
{
    
    // void create(AccountSecConfig accountSecConfig);
    
    // void update(AccountSecConfig accountSecConfig);
    
    void setConfig(int accountId, Byte enableSpaceSec, Byte enalbeFileSec, Byte enalbeFileCopySec);
    
    AccountSecConfig get(int accountId);
    
}
