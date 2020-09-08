package com.huawei.sharedrive.uam.enterprise.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface ResourceStrategyDao
{
    
    long create(ResourceStrategy rs);
    
    boolean isDuplicateValues(ResourceStrategy rs);
    
    int getFilterdCount(ResourceStrategy rs);
    
    List<ResourceStrategy> getFilterd(ResourceStrategy rs, Order order, Limit limit);
    
    SafeLevel getById(long id);
    
    List<ResourceStrategy> getResourceStrategyByCondition(ResourceStrategy resourceStrategy);
    
    long getByDomainExclusiveId(ResourceStrategy rs);
    
    long getMaxId();
    
    void deleteByContidion(ResourceStrategy resourceStrategy);
    
    void update(ResourceStrategy resourceStrategy);
    
    List<ResourceStrategy> getResourceStrategyById(ResourceStrategy resourceStrategy);
    
    ResourceStrategy getByStrategyId(long id);
    
    void deleteStrategy(long id);
}
