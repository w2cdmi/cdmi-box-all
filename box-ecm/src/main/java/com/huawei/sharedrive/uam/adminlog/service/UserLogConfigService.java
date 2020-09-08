package com.huawei.sharedrive.uam.adminlog.service;

import pw.cdmi.common.domain.SystemConfig;

public interface UserLogConfigService
{
    
    SystemConfig queryConfig(String appId, String id);
    
}
