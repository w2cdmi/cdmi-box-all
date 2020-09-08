package pw.cdmi.box.disk.authserver.dao;

import pw.cdmi.box.disk.authserver.domain.AccountAuthserver;

public interface AccountAuthserverDao
{
    AccountAuthserver getByAccountAuthId(Long accountId, Long authserverId);
}
