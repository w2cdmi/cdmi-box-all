package pw.cdmi.box.disk.authserver.manager;

import pw.cdmi.box.disk.authserver.domain.AccountAuthserver;

public interface AccountAuthserverManager
{
    
    AccountAuthserver getByAccountAuthId(Long accountId, Long authserverId);
    
}
