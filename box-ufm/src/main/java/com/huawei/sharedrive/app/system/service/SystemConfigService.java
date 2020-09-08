package com.huawei.sharedrive.app.system.service;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.SystemConfig;

public interface SystemConfigService
{
    SystemConfig getConfig(String key);
    
    List<SystemConfig> getByPrefix(Limit limit, String prefix);
}
