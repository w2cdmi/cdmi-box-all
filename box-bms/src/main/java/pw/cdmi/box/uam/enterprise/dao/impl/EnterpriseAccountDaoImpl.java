package pw.cdmi.box.uam.enterprise.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.uam.enterprise.dao.EnterpriseAccountDao;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.domain.enterprise.EnterpriseAccountVo;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class EnterpriseAccountDaoImpl extends CacheableSqlMapClientDAO implements EnterpriseAccountDao
{
    @Override
    public EnterpriseAccount getByAccessKeyId(String accessKeyId)
    {
        if (isCacheSupported())
        {
            String key = EnterpriseAccount.getEnterpriseAccountAKCacheKey(accessKeyId);
            EnterpriseAccount enterpriseAccount = (EnterpriseAccount) getCacheClient().getCache(key);
            if (enterpriseAccount != null)
            {
                return enterpriseAccount;
            }
            enterpriseAccount = (EnterpriseAccount) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getByAccessKeyId",
                accessKeyId);
            if (enterpriseAccount == null)
            {
                return null;
            }
            getCacheClient().setCache(key, enterpriseAccount);
            return enterpriseAccount;
        }
        return (EnterpriseAccount) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getByAccessKeyId",
            accessKeyId);
    }
    
    @Override
    public List<EnterpriseAccount> getByEnterpriseId(long enterpriseId)
    {
        return sqlMapClientTemplate.queryForList("EnterpriseAccount.getByEnterpriseId", enterpriseId);
    }
    
    @Override
    public List<EnterpriseAccount> getAppContextByEnterpriseId(long enterpriseId)
    {
        return sqlMapClientTemplate.queryForList("EnterpriseAccount.getAppContextByEnterpriseId",
            enterpriseId);
    }
    
    @Override
    public int deleteByAccountId(long accountId)
    {
        EnterpriseAccount enterpriseAccount = getByAccountId(accountId);
        if (null == enterpriseAccount)
        {
            return 0;
        }
        int delNum = sqlMapClientTemplate.delete("EnterpriseAccount.deleteByAccountId", accountId);
        deleteCacheAfterCommit(EnterpriseAccount.getEnterpriseAccountAKCacheKey(enterpriseAccount.getAccessKeyId()));
        deleteCacheAfterCommit(EnterpriseAccount.getEnterpriseAccountIdCacheKey(enterpriseAccount.getAccountId()));
        deleteCacheAfterCommit(EnterpriseAccount.getEnterpriseEidAppIdKey(enterpriseAccount.getEnterpriseId(),
            enterpriseAccount.getAuthAppId()));
        return delNum;
    }
    
    @Override
    public void create(EnterpriseAccount enterpriseAccount)
    {
        sqlMapClientTemplate.insert("EnterpriseAccount.insert", enterpriseAccount);
    }
    
    @Override
    public List<String> getAppByEnterpriseId(long enterpriseId)
    {
        return sqlMapClientTemplate.queryForList("EnterpriseAccount.getAppByEnterpriseId", enterpriseId);
    }
    
    @Override
    public List<Long> getAccountIdByEnterpriseId(long enterpriseId)
    {
        return sqlMapClientTemplate.queryForList("EnterpriseAccount.getAccountIdByEnterpriseId", enterpriseId);
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
    public List<EnterpriseAccountVo> getEnterpriseAccountFilterd(EnterpriseAccountVo account,Order order,
        Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(8);
        map.put("authAppId", account.getAuthAppId());
        map.put("filter", account.getName());
        map.put("status", account.getStatus());
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("EnterpriseAccount.getEnterpriseAccountFilterd", map);
    }

    @Override
    public int getEnterpriseAccountFilterdCount(EnterpriseAccountVo account)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("authAppId", account.getAuthAppId());
        map.put("status", account.getStatus());
        map.put("filter", account.getName());
        return (Integer) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getEnterpriseAccountFilterdCount", map);
    }
    
    @Override
    public void modifyEnterpriseAccount(EnterpriseAccount enterpriseAccount)
    {
        sqlMapClientTemplate.update("EnterpriseAccount.update", enterpriseAccount);
        if (isCacheSupported())
        {
            String keyAuthApp = EnterpriseAccount.getEnterpriseEidAppIdKey(enterpriseAccount.getEnterpriseId(), enterpriseAccount.getAuthAppId());
            String key = EnterpriseAccount.getEnterpriseAccountIdCacheKey(enterpriseAccount.getAccountId());
            deleteCacheAfterCommit(key);
            deleteCacheAfterCommit(keyAuthApp);
        }
    }
}
