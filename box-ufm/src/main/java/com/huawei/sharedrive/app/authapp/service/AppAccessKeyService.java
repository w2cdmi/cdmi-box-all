package com.huawei.sharedrive.app.authapp.service;

import pw.cdmi.common.domain.AppAccessKey;

public interface AppAccessKeyService
{
    
    AppAccessKey getById(String id);
    
    AppAccessKey getByAppId(String appId);
    
    void deleteCache(String id);
    
    void deleteCacheByAppId(String appId);
    
}
