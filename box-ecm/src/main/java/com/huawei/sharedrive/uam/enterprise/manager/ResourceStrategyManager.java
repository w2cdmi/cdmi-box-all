package com.huawei.sharedrive.uam.enterprise.manager;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface ResourceStrategyManager
{
    
    void create(ResourceStrategy resourceStrategy) throws IOException;
    
    void create(ResourceStrategy resourceStrategy, SafeLevel safeLevel, HttpServletRequest req)
        throws IOException;
    
    void delete(SafeLevel safeLevel) throws IOException;
    
    Page<ResourceStrategy> getFilterd(ResourceStrategy resourceStrategy, PageRequest pageRequest,
        Locale locale);
    
    void delete(ResourceStrategy resourceStrategy) throws IOException;
    
    void modify(ResourceStrategy resourceStrategy) throws IOException;
    
    ResourceStrategy getResourceStrategy(ResourceStrategy resourceStrategy);
    
    ResourceStrategy getResourceStrategyId(long id);
    
    void deleteStrategy(int id) throws IOException;
}
