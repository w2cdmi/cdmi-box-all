package pw.cdmi.box.disk.accountbaseconfig.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.accountbaseconfig.dao.AccountBasicConfigDao;
import pw.cdmi.box.disk.accountbaseconfig.domain.AccountBasicConfig;
import pw.cdmi.common.cache.CacheClient;

@Component
@SuppressWarnings("deprecation")
public class AccountBasicConfigDaoImpl extends CacheableSqlMapClientDAO implements AccountBasicConfigDao {

	@Autowired(required = false)
	@Qualifier("uamCacheClient")
	private CacheClient cacheClient;

	@Override
	public AccountBasicConfig get(AccountBasicConfig appBasicConfig) {
		Object result =sqlMapClientTemplate.queryForObject("AccountBasicConfig.get", appBasicConfig);
		if(result !=null){
			return (AccountBasicConfig)result;
		}
		return null;
	}

	@Override
	public CacheClient getCacheClient() {
		return cacheClient;
	}

}
