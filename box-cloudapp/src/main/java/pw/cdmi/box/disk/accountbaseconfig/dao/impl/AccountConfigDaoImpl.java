package pw.cdmi.box.disk.accountbaseconfig.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.accountbaseconfig.dao.AccountConfigDao;
import pw.cdmi.common.domain.AccountConfig;

@Component
@SuppressWarnings("deprecation")
public class AccountConfigDaoImpl extends CacheableSqlMapClientDAO implements AccountConfigDao{

    
    @Override
    public void create(AccountConfig accountConfig)
    {
        sqlMapClientTemplate.insert("AccountConfig.create", accountConfig);
    }
    
    @Override
    public AccountConfig get(long accountId, String name)
    {
        AccountConfig accountConfig = new AccountConfig();
        accountConfig.setAccountId(accountId);
        accountConfig.setName(name);
        return (AccountConfig) sqlMapClientTemplate.queryForObject("AccountConfig.get", accountConfig);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<AccountConfig> list(long accountId)
    {
        AccountConfig accountConfig = new AccountConfig();
        accountConfig.setAccountId(accountId);
        return sqlMapClientTemplate.queryForList("AccountConfig.list", accountConfig);
    }
    
    @Override
    public int update(AccountConfig accountConfig)
    {
        return sqlMapClientTemplate.update("AccountConfig.update", accountConfig);
    }

	@Override
	public AccountConfig getByDomainName(String domainName) {
		// TODO Auto-generated method stub
		return (AccountConfig) sqlMapClientTemplate.queryForObject("AccountConfig.getByDomainName",domainName);
	}
    


}
