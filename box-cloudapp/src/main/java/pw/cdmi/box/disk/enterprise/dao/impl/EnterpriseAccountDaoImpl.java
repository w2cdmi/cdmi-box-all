package pw.cdmi.box.disk.enterprise.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.enterprise.dao.EnterpriseAccountDao;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

@Service
@SuppressWarnings({"deprecation"})
public class EnterpriseAccountDaoImpl extends CacheableSqlMapClientDAO implements EnterpriseAccountDao
{
    @Autowired(required = false)
    @Qualifier("uamCacheClient")
    private CacheClient cacheClient;
    
    @Override
    public CacheClient getCacheClient()
    {
        return cacheClient;
    }
    
    @Override
    public EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("enterpriseId", enterpriseId);
        map.put("authAppId", authAppId);
        if (isCacheSupported())
        {
            String key = EnterpriseAccount.getEnterpriseEidAppIdKey(enterpriseId, authAppId);
            EnterpriseAccount enterpriseAccount = (EnterpriseAccount) getCacheClient().getCache(key);
            if (enterpriseAccount != null)
            {
                return enterpriseAccount;
            }
            enterpriseAccount = (EnterpriseAccount) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getByEnterpriseApp",
                map);
            if (enterpriseAccount == null)
            {
                return null;
            }
            getCacheClient().setCache(key, enterpriseAccount);
            return enterpriseAccount;
        }
        return (EnterpriseAccount) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getByEnterpriseApp",
            map);
    }
    
    @Override
    public EnterpriseAccount getByAccountId(long accountId)
    {
        if (isCacheSupported())
        {
            String key = EnterpriseAccount.getEnterpriseAccountIdCacheKey(accountId);
            EnterpriseAccount enterpriseAccount = (EnterpriseAccount) getCacheClient().getCache(key);
            if (enterpriseAccount != null)
            {
                return enterpriseAccount;
            }
            enterpriseAccount = (EnterpriseAccount) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getByAccountId",
                accountId);
            if (enterpriseAccount == null)
            {
                return null;
            }
            getCacheClient().setCache(key, enterpriseAccount);
            return enterpriseAccount;
        }
        return (EnterpriseAccount) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getByAccountId",
            accountId);
    }
    
}
