package com.huawei.sharedrive.uam.sso.service;

public interface SSOTmpTokenCacheService
{
    void deleteSSOTmpToken(String tmpToken);
    
    String getSSOLoginName(String tmpToken);
    
    void saveSSOTmpToken(String ObjectSid, String tmpToken);
}
