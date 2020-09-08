package pw.cdmi.box.uam.enterprise.dao.impl;

import java.util.Date;

import org.springframework.stereotype.Component;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.uam.enterprise.dao.AccountSecConfigDao;
import pw.cdmi.box.uam.enterprise.domain.AccountSecConfig;

@SuppressWarnings({"deprecation"})
@Component
public class AccountSecConfigDaoImpl extends CacheableSqlMapClientDAO implements AccountSecConfigDao
{
    
    private final static String CACHE_PREFIX = "secmatrix_account_";
    
    @Override
    public void create(AccountSecConfig accountSecConfig)
    {
        Date now = new Date();
        accountSecConfig.setCreatedAt(now);
        accountSecConfig.setModifiedAt(now);
        sqlMapClientTemplate.insert("AccountSecConfig.insert", accountSecConfig);
    }
    
    @Override
    public void update(AccountSecConfig accountSecConfig)
    {
        accountSecConfig.setModifiedAt(new Date());
        sqlMapClientTemplate.update("AccountSecConfig.modify", accountSecConfig);
        String key = CACHE_PREFIX + accountSecConfig.getAccountId();
        deleteCacheAfterCommit(key);
    }
    
    @Override
    public AccountSecConfig get(int accountId)
    {
        if (!isCacheSupported())
        {
            Object obj = sqlMapClientTemplate.queryForObject("AccountSecConfig.get", accountId);
            if (null != obj)
            {
                return (AccountSecConfig) obj;
            }
            return null;
        }
        String key = CACHE_PREFIX + accountId;
        AccountSecConfig config = (AccountSecConfig) getCacheClient().getCache(key);
        if (config != null)
        {
            return config;
        }
        Object obj = sqlMapClientTemplate.queryForObject("AccountSecConfig.get", accountId);
        if (null == obj)
        {
            return null;
        }
        config = (AccountSecConfig) obj;
        getCacheClient().setCache(key, config);
        return config;
    }
    
}
