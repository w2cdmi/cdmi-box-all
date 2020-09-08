package com.huawei.sharedrive.uam.enterprise.manager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.huawei.sharedrive.uam.enterprise.domain.AccessConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface AccessConfigManager
{
    
    long create(AccessConfig accessConfig, Locale locale) throws IOException;
    
    void delete(int id, long accountId) throws IOException;
    
    void modify(AccessConfig accessConfig, Locale locale) throws IOException;
    
    Page<AccessConfig> getFilterd(AccessConfig filter, PageRequest pageRequest, Locale locale);
    
    AccessConfig getById(long id);
    
    void updateEnterpriseInfo(AccessConfig accessConfig, Locale locale);
    
    List<AccessConfig> getFilterdList(AccessConfig filterr);
    
    void deleteByCondition(AccessConfig accessconfig);
    
    AccessConfig getByAllType(AccessSpaceConfig accessSpaceConfig);
}
