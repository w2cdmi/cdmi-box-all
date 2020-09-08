package com.huawei.sharedrive.uam.enterprise.manager.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;
import com.huawei.sharedrive.uam.enterprise.manager.SafeLevelManager;
import com.huawei.sharedrive.uam.enterprise.service.SafeLevelService;
import com.huawei.sharedrive.uam.exception.ExistEnterpriseConflictException;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

@Component
public class SafeLevelManagerImpl implements SafeLevelManager
{
    
    @Autowired
    private SafeLevelService safeLevelService;
    
    @Override
    public long create(SafeLevel safeLevel, List<ResourceStrategy> listStr) throws IOException
    {
        if (safeLevelService.isDuplicateValues(safeLevel))
        {
            throw new ExistEnterpriseConflictException();
        }
        long id = safeLevelService.create(safeLevel, listStr);
        
        return id;
    }
    
    @Override
    public void delete(int id) throws IOException
    {
        safeLevelService.delete(id);
    }
    
    @Override
    public void modify(SafeLevel safeLevel) throws IOException
    {
        safeLevelService.updateSecurityRole(safeLevel);
        
    }
    
    @Override
    public Page<SafeLevel> getFilterd(SafeLevel safeLevel, PageRequest pageRequest)
    {
        return safeLevelService.getFilterd(safeLevel, pageRequest);
    }
    
    @Override
    public SafeLevel getById(long id)
    {
        return safeLevelService.getById(id);
    }
    
    @Override
    public List<SafeLevel> getFilterdList(SafeLevel safeLevel)
    {
        return safeLevelService.getFilterdList(safeLevel);
    }
    
    public List<ResourceStrategy> getFilterdResourceStrategyList(ResourceStrategy safeLevel)
    {
        return safeLevelService.getFilterdList(safeLevel);
    }
    
    @Override
    public void delete(SafeLevel safeLevel) throws IOException
    {
        safeLevelService.delete(safeLevel);
        
    }
    
}
