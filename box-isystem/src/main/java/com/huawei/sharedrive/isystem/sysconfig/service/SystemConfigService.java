package com.huawei.sharedrive.isystem.sysconfig.service;

import pw.cdmi.common.domain.SystemConfig;

public interface SystemConfigService
{
    SystemConfig getSystemConfig(String key);
    
    void setSystemConfig(String key, String value);
}
