package com.huawei.sharedrive.uam.enterprise.manager.impl;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;
import com.huawei.sharedrive.uam.enterprise.manager.ResourceStrategyManager;
import com.huawei.sharedrive.uam.enterprise.service.ResourceStrategyService;
import com.huawei.sharedrive.uam.enterprise.service.SafeLevelService;
import com.huawei.sharedrive.uam.exception.ExistResourceStrategyConflictException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.util.SecurityConfigConstants;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

@Component
public class ResourceStrategyManagerImpl implements ResourceStrategyManager
{
    private static Logger logger = LoggerFactory.getLogger(ResourceStrategyManagerImpl.class);
    
    @Autowired
    private ResourceStrategyService resourceStrategyService;
    
    @Autowired
    private SafeLevelService safeLevelService;
    
    @Override
    public void create(ResourceStrategy safeLevel)
    {
        if (resourceStrategyService.isDuplicateValues(safeLevel))
        {
            logger.error("access config is duplicated.");
            throw new ExistResourceStrategyConflictException();
        }
        if (safeLevel.getNetRegionId() == SecurityConfigConstants.TYPE_OF_SELECT_ALL
            && safeLevel.getSecurityRoleId() == SecurityConfigConstants.TYPE_OF_SELECT_ALL)
        {
            throw new InvalidParamterException("the regionid and security id cannot be -1 at the same time");
        }
        resourceStrategyService.create(safeLevel);
    }
    
    @Override
    public void create(ResourceStrategy safeLevel, SafeLevel saflevel, HttpServletRequest req)
    {
        if (resourceStrategyService.isDuplicateValues(safeLevel))
        {
            delete(saflevel);
            logger.error("access config is duplicated.");
            throw new ExistResourceStrategyConflictException();
        }
        if (safeLevel.getNetRegionId() == SecurityConfigConstants.TYPE_OF_SELECT_ALL
            && safeLevel.getSecurityRoleId() == SecurityConfigConstants.TYPE_OF_SELECT_ALL)
        {
            delete(saflevel);
            throw new InvalidParamterException("the regionid and security id cannot be -1 at the same time");
        }
        resourceStrategyService.create(safeLevel);
    }
    
    @Override
    public Page<ResourceStrategy> getFilterd(ResourceStrategy safeLevel, PageRequest pageRequest,
        Locale locale)
    {
        return resourceStrategyService.getFilterd(safeLevel, pageRequest, locale);
    }
    
    @Override
    public void delete(ResourceStrategy resourceStrategy)
    {
        resourceStrategyService.delete(resourceStrategy);
    }
    
    @Override
    public void modify(ResourceStrategy resourceStrategy)
    {
        resourceStrategyService.update(resourceStrategy);
        
    }
    
    @Override
    public ResourceStrategy getResourceStrategy(ResourceStrategy resourceStrategy)
    {
        return resourceStrategyService.getResourceStrategy(resourceStrategy);
    }
    
    @Override
    public ResourceStrategy getResourceStrategyId(long id)
    {
        return resourceStrategyService.getResourceStrategyId(id);
    }
    
    @Override
    public void deleteStrategy(int id) throws IOException
    {
        resourceStrategyService.deleteStrategy(id);
    }
    
    @Override
    public void delete(SafeLevel safeLevel)
    {
        safeLevelService.delete(safeLevel);
        
    }
}
