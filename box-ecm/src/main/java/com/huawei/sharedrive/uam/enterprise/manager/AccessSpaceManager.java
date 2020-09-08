package com.huawei.sharedrive.uam.enterprise.manager;

import java.io.IOException;
import java.util.Locale;

import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfigExt;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface AccessSpaceManager
{
    void createConfig(AccessSpaceConfigExt spaceConfig, Locale locale);
    
    Page<AccessSpaceConfig> getAccessSpaceList(AccessSpaceConfigExt accessSpace, PageRequest pagerRequest,
        Locale locale);
    
    void deleteConfig(String id);
    
    
    void modify(AccessSpaceConfigExt accessSpaceConfigExt, Locale locale) throws IOException;
    
    AccessSpaceConfig getById(String id);
    
}
