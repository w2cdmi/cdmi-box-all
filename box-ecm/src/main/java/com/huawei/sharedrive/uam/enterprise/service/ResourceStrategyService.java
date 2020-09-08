package com.huawei.sharedrive.uam.enterprise.service;

import java.util.List;
import java.util.Locale;

import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface ResourceStrategyService
{
    
    Page<ResourceStrategy> getFilterd(ResourceStrategy filter, PageRequest pageRequest, Locale locale);
    
    void create(ResourceStrategy resourceStrategy);
    
    void delete(ResourceStrategy resourceStrategy);
    
    void update(ResourceStrategy resourceStrategy);
    
    ResourceStrategy getResourceStrategy(ResourceStrategy resourceStrategy);
    
    boolean isDuplicateValues(ResourceStrategy resourceStrategy);
    
    ResourceStrategy getResourceStrategyId(long id);
    
    void deleteStrategy(long id);
    
    Page<ResourceStrategy> queryFilterd(List<ResourceStrategy> content, ResourceStrategy resourceStrategy,
        PageRequest pageRequest, Locale locale);
    
}
