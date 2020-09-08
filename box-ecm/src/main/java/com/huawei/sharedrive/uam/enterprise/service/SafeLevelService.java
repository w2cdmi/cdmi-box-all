package com.huawei.sharedrive.uam.enterprise.service;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface SafeLevelService
{
    
    long create(SafeLevel safeLevel, List<ResourceStrategy> listStr);
    
    boolean isDuplicateValues(SafeLevel safeLevel);
    
    Page<SafeLevel> getFilterd(SafeLevel filter, PageRequest pageRequest);
    
    SafeLevel getById(long id);
    
    long getByDomainExclusiveId(SafeLevel safeLevel);
    
    void updateSecurityRole(SafeLevel safeLevel);
    
    boolean isDuplicateNetConfigValues(SafeLevel safeLevel);
    
    List<SafeLevel> getFilterdList(SafeLevel safeLevel);
    
    List<ResourceStrategy> getFilterdList(ResourceStrategy resourceStrategy);
    
    ResourceStrategy getResourceStrategyByCondition(ResourceStrategy resourceStrategy);
    
    void delete(long id);
    
    void deleteByCondition(ResourceStrategy resourceStrategy);
    
    void delete(SafeLevel safeLevel);
    
}
