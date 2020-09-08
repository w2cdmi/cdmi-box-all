package com.huawei.sharedrive.uam.enterprise.manager.impl;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.domain.AccessConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;
import com.huawei.sharedrive.uam.enterprise.manager.AccessConfigManager;
import com.huawei.sharedrive.uam.enterprise.service.AccessConfigService;
import com.huawei.sharedrive.uam.exception.ExistAccessConfigConflictException;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

@Component
public class AccessConfigManagerImpl implements AccessConfigManager
{
    private static Logger logger = LoggerFactory.getLogger(AccessConfigManagerImpl.class);
    
    @Autowired
    private AccessConfigService accessConfigService;
    
    @Override
    public long create(AccessConfig accessConfig, Locale locale) throws IOException
    {
        if (accessConfigService.isDuplicateValues(accessConfig))
        {
            logger.error("access config is duplicated.");
            throw new ExistAccessConfigConflictException();
        }
        long id = accessConfigService.create(accessConfig, locale);
        
        return id;
    }
    
    @Override
    public void delete(int id, long accountId) throws IOException
    {
        accessConfigService.delete(id, accountId);
    }
    
    @Override
    public void modify(AccessConfig accessConfig, Locale locale) throws IOException
    {
        accessConfigService.updateSecurityRole(accessConfig, locale);
        
    }
    
    @Override
    public Page<AccessConfig> getFilterd(AccessConfig filter, PageRequest pageRequest, Locale locale)
    {
        return accessConfigService.getFilterd(filter, pageRequest, locale);
    }
    
    @Override
    public AccessConfig getById(long id)
    {
        return accessConfigService.getById(id);
    }
    
    @Override
    public void updateEnterpriseInfo(AccessConfig netRegion, Locale locale)
    {
        accessConfigService.updateSecurityRole(netRegion, locale);
    }
    
    @Override
    public List<AccessConfig> getFilterdList(AccessConfig filter)
    {
        return accessConfigService.getFilterdList(filter);
    }
    
    @Override
    public void deleteByCondition(AccessConfig accessconfig)
    {
        accessConfigService.deleteByCondition(accessconfig);
    }
    
    @Override
    public AccessConfig getByAllType(AccessSpaceConfig accessSpaceConfig)
    {
        return accessConfigService.getByAllType(accessSpaceConfig);
    }
    
}
