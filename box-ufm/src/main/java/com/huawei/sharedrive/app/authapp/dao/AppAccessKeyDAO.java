package com.huawei.sharedrive.app.authapp.dao;

import java.util.List;

import pw.cdmi.common.domain.AppAccessKey;

public interface AppAccessKeyDAO
{
    
    AppAccessKey getById(String id);
    
    List<AppAccessKey> listByAppId(String appId);
    
    void deleteCache(String id);
    
    void deleteCacheByAppId(String appId);
}
