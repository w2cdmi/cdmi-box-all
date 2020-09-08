package com.huawei.sharedrive.isystem.plugin.service;

import java.util.List;

import pw.cdmi.common.domain.AppAccessKey;

public interface PluginAccessKeyService
{
    int ACCESSKEY_PER_APP_LIMIT = 4;
    
    AppAccessKey getById(String id);
    
    List<AppAccessKey> getByAppId(String appId);
    
    int delete(String id);
    
    AppAccessKey createAppAccessKeyForApp(String appId);
    
}
