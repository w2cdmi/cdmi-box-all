package com.huawei.sharedrive.uam.enterprise.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.AccessConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface AccessConfigDao
{
    
    long create(AccessConfig accessConfig);
    
    boolean isDuplicateValues(AccessConfig accessConfig);
    
    int getFilterdCount(AccessConfig accessConfig);
    
    List<AccessConfig> getFilterd(AccessConfig accessConfig, Order order, Limit limit);
    
    List<AccessConfig> getAccessConfigList(long accountId);
    
    AccessConfig getById(long id);
    
    long getByDomainExclusiveId(AccessConfig accessConfig);
    
    void update(AccessConfig accessConfig);
    
    void delete(long id, long accountId);
    
    long getMaxId();
    
    void deleteByContidion(AccessConfig accessConfig);
    
    AccessConfig getByAllType(AccessSpaceConfig accessSpaceConfig);
}
