package com.huawei.sharedrive.uam.enterprise.manager;

import java.io.IOException;
import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface SafeLevelManager
{
    
    long create(SafeLevel safeLevel, List<ResourceStrategy> listStr) throws IOException;
    
    void delete(int id) throws IOException;
    
    void modify(SafeLevel safeLevel) throws IOException;
    
    Page<SafeLevel> getFilterd(SafeLevel filter, PageRequest pageRequest);
    
    SafeLevel getById(long id);
    
    List<SafeLevel> getFilterdList(SafeLevel safeLevel);
    
    List<ResourceStrategy> getFilterdResourceStrategyList(ResourceStrategy safeLevel);
    
    void delete(SafeLevel safeLevel) throws IOException;
}
