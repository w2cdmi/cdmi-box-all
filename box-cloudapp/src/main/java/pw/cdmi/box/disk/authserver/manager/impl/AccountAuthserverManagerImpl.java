package pw.cdmi.box.disk.authserver.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.authserver.domain.AccountAuthserver;
import pw.cdmi.box.disk.authserver.manager.AccountAuthserverManager;
import pw.cdmi.box.disk.authserver.service.AccountAuthserverService;

@Component
public class AccountAuthserverManagerImpl implements AccountAuthserverManager
{
    
    @Autowired
    private AccountAuthserverService accountAuthserverService;
    
    @Override
    public AccountAuthserver getByAccountAuthId(Long accountId, Long authserverId)
    {
        return accountAuthserverService.getByAccountAuthId(accountId, authserverId);
    }
}
