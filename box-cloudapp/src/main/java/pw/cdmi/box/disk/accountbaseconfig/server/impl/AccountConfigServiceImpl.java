package pw.cdmi.box.disk.accountbaseconfig.server.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.disk.accountbaseconfig.dao.AccountConfigDao;
import pw.cdmi.box.disk.accountbaseconfig.server.AccountConfigService;
import pw.cdmi.common.domain.AccountConfig;
@Service
public class AccountConfigServiceImpl implements AccountConfigService {

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

	@Override
	public AccountConfig getByDomainName(String domainName) {
		// TODO Auto-generated method stub
		return accountConfigDao.getByDomainName(domainName);
	}
    

}

