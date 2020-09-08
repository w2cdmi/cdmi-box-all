package com.huawei.sharedrive.uam.enterprise.service;

import java.util.List;
import java.util.Locale;

import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;
import com.huawei.sharedrive.uam.enterprise.domain.SecOperation;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface AccessSpaceConfigService
{
    
    List<AccessSpaceConfig> getFilterdList(AccessSpaceConfig filter);
    
    void create(AccessSpaceConfig accessSpaceConfig, Locale locale);
    
    void delete(String id);
    
    long getByDomainExclusiveId(AccessSpaceConfig accessSpaceConfig);
    
    AccessSpaceConfig getById(String id);
    
    AccessSpaceConfig getObject(AccessSpaceConfig spaceConfig);
    
    Page<AccessSpaceConfig> getFilterd(AccessSpaceConfig filter, PageRequest pageRequest, Locale locale);
    
    boolean isDuplicateNetConfigValues(AccessSpaceConfig accessSpaceConfig);
    
    boolean isDuplicateValues(AccessSpaceConfig accessSpaceConfig);
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    boolean judgeSpaceMatrix(long accountId, Integer secRole, Long netRegionId, Integer spaceSecRole,
        SecOperation secOperation, Integer deviceType);
    
    void update(AccessSpaceConfig accessSpaceConfig, Locale locale);
    
    void deleteByCondition(AccessSpaceConfig accessSpaceConfig);
}
