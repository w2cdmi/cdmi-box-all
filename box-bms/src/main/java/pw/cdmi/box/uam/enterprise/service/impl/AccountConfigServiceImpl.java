package pw.cdmi.box.uam.enterprise.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.enterprise.dao.AccountConfigDao;
import pw.cdmi.common.domain.AccountConfig;
import pw.cdmi.box.uam.enterprise.service.AccountConfigService;

@Service
public class AccountConfigServiceImpl implements AccountConfigService
{
    @Autowired
    private AccountConfigDao accountConfigDao;
    
    @Override
    public void create(AccountConfig accountConfig)
    {
        accountConfigDao.create(accountConfig);
    }
    
    @Override
    public AccountConfig get(long accountId, String name)
    {
        return accountConfigDao.get(accountId, name);
    }
    
    @Override
    public List<AccountConfig> list(long accountId)
    {
        return accountConfigDao.list(accountId);
    }
    
    @Override
    public void update(AccountConfig accountConfig)
    {
        accountConfigDao.update(accountConfig);
    }
    
}
