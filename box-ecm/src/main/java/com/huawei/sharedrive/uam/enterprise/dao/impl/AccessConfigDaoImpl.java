package com.huawei.sharedrive.uam.enterprise.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.dao.AccessConfigDao;
import com.huawei.sharedrive.uam.enterprise.domain.AccessConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;
import com.huawei.sharedrive.uam.enterprise.service.AccessConfigGenerateService;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@SuppressWarnings({"unchecked", "deprecation"})
@Component
public class AccessConfigDaoImpl extends CacheableSqlMapClientDAO implements AccessConfigDao
{
    @Autowired
    private AccessConfigGenerateService accessConfigGenerateService;
    
    @Override
    public long create(AccessConfig netRegion)
    {
        long id = accessConfigGenerateService.getNextId();
        netRegion.setId(id);
        sqlMapClientTemplate.insert("AccessConfig.insert", netRegion);
        return id;
    }
    
    @Override
    public boolean isDuplicateValues(AccessConfig netRegion)
    {
        int count = (int) sqlMapClientTemplate.queryForObject("AccessConfig.getDuplicateValues", netRegion);
        if (count > 0)
        {
            return true;
        }
        return false;
    }
    
    @Override
    public int getFilterdCount(AccessConfig filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("AccessConfig.getFilterdCount", map);
    }
    
    @Override
    public List<AccessConfig> getFilterd(AccessConfig filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("AccessConfig.getFilterd", map);
    }
    
    @Override
    public AccessConfig getById(long id)
    {
        return (AccessConfig) sqlMapClientTemplate.queryForObject("AccessConfig.getById", id);
    }
    
    @Override
    public void update(AccessConfig netRegion)
    {
        sqlMapClientTemplate.update("AccessConfig.modify", netRegion);
        
        String accountKey = AccessConfig.CACHE_KEY_PREFIX_ACCOUNT_ID + netRegion.getAccountId();
        deleteCacheAfterCommit(accountKey);
    }
    
    @Override
    public long getByDomainExclusiveId(AccessConfig netRegion)
    {
        long count = (long) sqlMapClientTemplate.queryForObject("AccessConfig.getByDomainExclusiveId",
            netRegion);
        return count;
    }
    
    @Override
    public void delete(long id, long accountId)
    {
        sqlMapClientTemplate.delete("AccessConfig.delete", id);
        String accountKey = AccessConfig.CACHE_KEY_PREFIX_ACCOUNT_ID + accountId;
        deleteCacheAfterCommit(accountKey);
    }
    
    @Override
    public void deleteByContidion(AccessConfig accessConfig)
    {
        sqlMapClientTemplate.delete("AccessConfig.deleteByContidion", accessConfig);
        String accountKey = AccessConfig.CACHE_KEY_PREFIX_ACCOUNT_ID + accessConfig.getAccountId();
        deleteCacheAfterCommit(accountKey);
    }
    
    @Override
    public long getMaxId()
    {
        long selMaxUserId;
        Object maxUserIdObject = sqlMapClientTemplate.queryForObject("AccessConfig.getMaxId", null);
        selMaxUserId = maxUserIdObject == null ? 0L : (long) maxUserIdObject;
        return selMaxUserId;
    }
    
    @Override
    public List<AccessConfig> getAccessConfigList(long accountId)
    {
        if (isCacheSupported())
        {
            String key = AccessConfig.CACHE_KEY_PREFIX_ACCOUNT_ID + accountId;
            List<AccessConfig> list = (List<AccessConfig>) getCacheClient().getCache(key);
            if (list != null)
            {
                return list;
            }
            list = sqlMapClientTemplate.queryForList("AccessConfig.getByAccountId", accountId);
            if (list.isEmpty())
            {
                return list;
            }
            getCacheClient().setCache(key, list);
            return list;
        }
        
        return sqlMapClientTemplate.queryForList("AccessConfig.getByAccountId", accountId);
    }
    
    @Override
    public AccessConfig getByAllType(AccessSpaceConfig accessSpaceConfig)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", accessSpaceConfig);
        return (AccessConfig) sqlMapClientTemplate.queryForObject("AccessConfig.getFilterdByAll", map);
    }
}
