package pw.cdmi.box.disk.authserver.service;

import pw.cdmi.box.disk.authserver.domain.AccountAuthserver;

public interface AccountAuthserverService
{
    AccountAuthserver getByAccountAuthId(Long accountId, Long authserverId);
}
