package com.huawei.sharedrive.uam.enterprise.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface SafeLevelDao
{
    
    long create(SafeLevel safeLevel);
    
    boolean isDuplicateValues(SafeLevel safeLevel);
    
    int getFilterdCount(SafeLevel safeLevel);
    
    List<SafeLevel> getFilterd(SafeLevel safeLevel, Order order, Limit limit);
    
    SafeLevel getById(long id);
    
    long getByDomainExclusiveId(SafeLevel safeLevel);
    
    void updateEnterpriseInfo(SafeLevel safeLevel);
    
    void delete(long id);
    
    long getMaxId();
    
}
