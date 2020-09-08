package com.huawei.sharedrive.uam.sso.manager;

public interface SSOTmpTokenManager
{
    void deleteSSOTmpToken(String tmpToken);
    
    String getSSOLoginName(String tmpToken);
    
    String saveSSOTmpToken(String loginName);
}
