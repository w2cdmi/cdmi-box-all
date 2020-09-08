package com.huawei.sharedrive.uam.enterprise.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.dao.ResourceStrategyDao;
import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;
import com.huawei.sharedrive.uam.enterprise.service.ResourceStrategyGenerateService;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@SuppressWarnings({"unchecked", "deprecation"})
@Component
public class ResourceStrategyDaoImpl extends CacheableSqlMapClientDAO implements ResourceStrategyDao
{
    
    @Autowired
    private ResourceStrategyGenerateService resourceStrategyGenerateService;
    
    @Override
    public long create(ResourceStrategy netRegion)
    {
        long id = resourceStrategyGenerateService.getNextId();
        netRegion.setId(id);
        sqlMapClientTemplate.insert("ResourceStrategy.insert", netRegion);
        return id;
    }
    
    @Override
    public boolean isDuplicateValues(ResourceStrategy netRegion)
    {
        int count = (int) sqlMapClientTemplate.queryForObject("ResourceStrategy.getDuplicateValues",
            netRegion);
        if (count > 0)
        {
            return true;
        }
        return false;
    }
    
    @Override
    public int getFilterdCount(ResourceStrategy filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("ResourceStrategy.getFilterdCount", map);
    }
    
    @Override
    public List<ResourceStrategy> getFilterd(ResourceStrategy filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("ResourceStrategy.getFilterd", map);
    }
    
    @Override
    public SafeLevel getById(long id)
    {
        return (SafeLevel) sqlMapClientTemplate.queryForObject("ResourceStrategy.getById", id);
    }
    
    @Override
    public List<ResourceStrategy> getResourceStrategyByCondition(ResourceStrategy resourceStrategy)
    {
        
        return sqlMapClientTemplate.queryForList("ResourceStrategy.getResourceStrategyByCondition",
            resourceStrategy);
    }
    
    @Override
    public List<ResourceStrategy> getResourceStrategyById(ResourceStrategy resourceStrategy)
    {
        
        return sqlMapClientTemplate.queryForList("ResourceStrategy.getResourceStrategyById", resourceStrategy);
    }
    
    @Override
    public long getByDomainExclusiveId(ResourceStrategy resourceStrategy)
    {
        long count = (long) sqlMapClientTemplate.queryForObject("ResourceStrategy.getByDomainExclusiveId",
            resourceStrategy);
        return count;
    }
    
    public void deleteByContidion(ResourceStrategy resourceStrategy)
    {
        sqlMapClientTemplate.delete("ResourceStrategy.deleteByContidion", resourceStrategy);
        String key = ResourceStrategy.CACHE_KEY_PREFIX_ID + resourceStrategy.getAccountId();
        deleteCacheAfterCommit(key);
    }
    
    @Override
    public long getMaxId()
    {
        long selMaxUserId;
        Object maxUserIdObject = sqlMapClientTemplate.queryForObject("ResourceStrategy.getMaxId", null);
        selMaxUserId = maxUserIdObject == null ? 0L : (long) maxUserIdObject;
        return selMaxUserId;
    }
    
    @Override
    public void update(ResourceStrategy resourceStrategy)
    {
        sqlMapClientTemplate.update("ResourceStrategy.modify", resourceStrategy);
        String key = ResourceStrategy.CACHE_KEY_PREFIX_ID + resourceStrategy.getAccountId();
        deleteCacheAfterCommit(key);
    }

    @Override
    public ResourceStrategy getByStrategyId(long id)
    {
        return (ResourceStrategy) sqlMapClientTemplate.queryForObject("ResourceStrategy.getByStrategyId", id);
    }
    
    @Override
    public void deleteStrategy(long id)
    {
        sqlMapClientTemplate.delete("ResourceStrategy.deleteStrategy", id);
    }
}
