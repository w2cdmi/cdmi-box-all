package com.huawei.sharedrive.isystem.authapp.service;

import java.util.List;

import pw.cdmi.common.domain.AppAccessKey;

public interface AppAccessKeyService
{
    int ACCESSKEY_PER_APP_LIMIT = 4;
    
    AppAccessKey getById(String id);
    
    List<AppAccessKey> getByAppId(String appId);
    
    int delete(String id);
    
    int deleteByAppId(String appId);
    
    AppAccessKey createAppAccessKeyForApp(String appId);
    
    void updateFirstScan(AppAccessKey appId);
    
}
