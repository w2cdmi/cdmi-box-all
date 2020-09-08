package pw.cdmi.box.disk.authserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.authserver.dao.AccountAuthserverDao;
import pw.cdmi.box.disk.authserver.domain.AccountAuthserver;
import pw.cdmi.box.disk.authserver.service.AccountAuthserverService;

@Component
public class AccountAuthserverServiceImpl implements AccountAuthserverService
{
    
    @Autowired
    private AccountAuthserverDao accountAuthserverDao;
    
    @Override
    public AccountAuthserver getByAccountAuthId(Long accountId, Long authserverId)
    {
        return accountAuthserverDao.getByAccountAuthId(accountId, authserverId);
    }
}
