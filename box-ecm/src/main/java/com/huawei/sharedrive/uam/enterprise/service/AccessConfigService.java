package com.huawei.sharedrive.uam.enterprise.service;

import java.util.List;
import java.util.Locale;

import com.huawei.sharedrive.uam.enterprise.domain.AccessConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface AccessConfigService
{
    
    long create(AccessConfig accessConfig, Locale locale);
    
    boolean isDuplicateValues(AccessConfig accessConfig);
    
    Page<AccessConfig> getFilterd(AccessConfig filter, PageRequest pageRequest, Locale locale);
    
    AccessConfig getById(long id);
    
    long getByDomainExclusiveId(AccessConfig accessConfig);
    
    void updateSecurityRole(AccessConfig accessConfig, Locale locale);
    
    boolean isDuplicateNetConfigValues(AccessConfig accessConfig);
    
    List<AccessConfig> getFilterdList(AccessConfig filter);
    
    List<AccessConfig> getAccessConfigList(long accountId);
    
    void delete(long id, long accountId);
    
    void deleteByFilter(AccessConfig filter);
    
    void deleteByCondition(AccessConfig accessconfig);
    
    AccessConfig getByAllType(AccessSpaceConfig accessSpaceConfig);
}
