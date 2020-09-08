package com.huawei.sharedrive.uam.sso.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.common.cache.CacheClient;
import com.huawei.sharedrive.uam.cmb.control.manager.Constants;
import com.huawei.sharedrive.uam.sso.service.SSOTmpTokenCacheService;

@Component
public class SSOTmpTokenCacheServiceImpl implements SSOTmpTokenCacheService
{
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    @Override
    public void deleteSSOTmpToken(String tmpToken)
    {
        String key = getTmpTokenKey(tmpToken);
        cacheClient.deleteCache(key);
    }
    
    @Override
    public String getSSOLoginName(String tmpToken)
    {
        String loginName = "";
        String key = getTmpTokenKey(tmpToken);
        Object obj = cacheClient.getCache(key);
        if (null != obj)
        {
            loginName = obj.toString();
        }
        return loginName;
    }
    
    @Override
    public void saveSSOTmpToken(String loginName, String tmpToken)
    {
        int expiredTime = Constants.TMP_TOKEN_EXPIRED;
        String key = getTmpTokenKey(tmpToken);
        Date expireTime = new Date(System.currentTimeMillis() + expiredTime);
        cacheClient.setCache(key, loginName, expireTime);
        
    }
    
    private String getTmpTokenKey(String tmpToken)
    {
        return Constants.TMP_TOKEN_KEY + tmpToken;
    }
}
