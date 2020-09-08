package com.huawei.sharedrive.uam.sso.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.sso.manager.SSOTmpTokenManager;
import com.huawei.sharedrive.uam.sso.service.SSOTmpTokenCacheService;
import com.huawei.sharedrive.uam.util.RandomKeyGUID;

@Component
public class SSOTmpTokenManagerImpl implements SSOTmpTokenManager
{
    private static final String APPID_SEP = "/";
    
    @Autowired
    private SSOTmpTokenCacheService sSOTmpTokenCacheService;
    
    @Override
    public void deleteSSOTmpToken(String tmpToken)
    {
        sSOTmpTokenCacheService.deleteSSOTmpToken(tmpToken);
    }
    
    @Override
    public String getSSOLoginName(String tmpToken)
    {
        return sSOTmpTokenCacheService.getSSOLoginName(tmpToken);
    }
    
    @Override
    public String saveSSOTmpToken(String loginName)
    {
        String tmpToken = generateToken();
        sSOTmpTokenCacheService.saveSSOTmpToken(loginName, tmpToken);
        return tmpToken;
    }
    
    private static String generateToken()
    {
        return new StringBuilder().append("SSO")
            .append(APPID_SEP)
            .append(RandomKeyGUID.getSecureRandomGUID())
            .toString();
    }
}
