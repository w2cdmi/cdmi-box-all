package pw.cdmi.box.disk.enterprise.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.enterprise.dao.EnterpriseDao;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.enterprise.Enterprise;

@SuppressWarnings({"unchecked", "deprecation"})
@Component
public class EnterpriseDaoImpl extends CacheableSqlMapClientDAO implements EnterpriseDao
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
    public List<Enterprise> listEnterprise()
    {
        if (isCacheSupported())
        {
            String key = Enterprise.getEnterpriseListKey();
            List<Enterprise> enterpriseList = (List<Enterprise>) getCacheClient().getCache(key);
            if (enterpriseList != null && !enterpriseList.isEmpty())
            {
                return enterpriseList;
            }
            enterpriseList = sqlMapClientTemplate.queryForList("Enterprise.listEnterprise");
            if (enterpriseList == null)
            {
                return null;
            }
            getCacheClient().setCache(key, enterpriseList);
            return enterpriseList;
        }
        List<Enterprise> list = sqlMapClientTemplate.queryForList("Enterprise.listEnterprise");
        return list;
    }
    
    @Override
    public Enterprise getByDomainName(String domainName)
    {
        if (isCacheSupported())
        {
            String key = Enterprise.getEnterpriseDomainCacheKey(domainName);
            Enterprise enterprise = (Enterprise) getCacheClient().getCache(key);
            if (enterprise != null)
            {
                return enterprise;
            }
            enterprise = (Enterprise) sqlMapClientTemplate.queryForObject("Enterprise.getByDomainName",
                domainName);
            if (enterprise == null)
            {
                return null;
            }
            getCacheClient().setCache(key, enterprise);
            return enterprise;
        }
        Enterprise enterprise = (Enterprise) sqlMapClientTemplate.queryForObject("Enterprise.getByDomainName",
            domainName);
        return enterprise;
    }
    
    @Override
    public Enterprise getById(long enterpriseId)
    {
        
        Enterprise enterprise = (Enterprise) sqlMapClientTemplate.queryForObject("Enterprise.getById",enterpriseId);
        return enterprise;
    }

    @Override
    public Enterprise getByName(String name) {
        return (Enterprise) sqlMapClientTemplate.queryForObject("Enterprise.getByName", name);
    }
}
