package com.huawei.sharedrive.uam.enterprise.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.dao.SafeLevelDao;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;
import com.huawei.sharedrive.uam.enterprise.service.SafeLevelGenerateService;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@SuppressWarnings({"unchecked", "deprecation"})
@Component
public class SafeLevelDaoImpl extends CacheableSqlMapClientDAO implements SafeLevelDao
{
    private static Logger logger = LoggerFactory.getLogger(SafeLevelDaoImpl.class);
    
    @Autowired
    private SafeLevelGenerateService safeLevelGenerateService;
    
    @Override
    public long create(SafeLevel safeLevel)
    {
        long id = safeLevelGenerateService.getNextId();
        safeLevel.setId(id);
        sqlMapClientTemplate.insert("SafeLevel.insert", safeLevel);
        return id;
    }
    
    public void delete(long id)
    {
        sqlMapClientTemplate.delete("SafeLevel.delete", id);
        try
        {
            if (isCacheSupported())
            {
                String key = SafeLevel.CACHE_KEY_PREFIX_ID + id;
                getCacheClient().deleteCache(key);
            }
        }
        catch (Exception e)
        {
            logger.warn("Fail to delete cache ", e);
        }
    }
    
    @Override
    public long getByDomainExclusiveId(SafeLevel netRegion)
    {
        long count = (long) sqlMapClientTemplate.queryForObject("SafeLevel.getByDomainExclusiveId", netRegion);
        return count;
    }
    
    @Override
    public SafeLevel getById(long id)
    {
        if (isCacheSupported())
        {
            String key = SafeLevel.CACHE_KEY_PREFIX_ID + id;
            SafeLevel securityRole = (SafeLevel) getCacheClient().getCache(key);
            if (securityRole != null)
            {
                return securityRole;
            }
            securityRole = (SafeLevel) sqlMapClientTemplate.queryForObject("SafeLevel.getById", id);
            if (securityRole == null)
            {
                return null;
            }
            getCacheClient().setCache(key, securityRole);
            return securityRole;
        }
        
        return (SafeLevel) sqlMapClientTemplate.queryForObject("SafeLevel.getById", id);
    }
    
    @Override
    public List<SafeLevel> getFilterd(SafeLevel filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("SafeLevel.getFilterd", map);
    }
    
    @Override
    public int getFilterdCount(SafeLevel filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("SafeLevel.getFilterdCount", map);
    }
    
    @Override
    public long getMaxId()
    {
        Object maxUserIdObject = sqlMapClientTemplate.queryForObject("SafeLevel.getMaxId", null);
        if (maxUserIdObject == null)
        {
            return 0L;
        }
        else
        {
            return (long) maxUserIdObject;
        }
    }
    
    @Override
    public boolean isDuplicateValues(SafeLevel safeLevel)
    {
        int count = (int) sqlMapClientTemplate.queryForObject("SafeLevel.getDuplicateValues", safeLevel);
        if (count > 0)
        {
            return true;
        }
        return false;
    }
    
    @Override
    public void updateEnterpriseInfo(SafeLevel netRegion)
    {
        sqlMapClientTemplate.update("SafeLevel.modify", netRegion);
        String key = SafeLevel.CACHE_KEY_PREFIX_ID + netRegion.getId();
        deleteCacheAfterCommit(key);
    }
}
