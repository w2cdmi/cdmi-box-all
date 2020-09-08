package com.huawei.sharedrive.app.files.service.impl.metadata;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.exception.InternalServerErrorException;

import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.utils.RandomGUID;

@Service
public class BridgeTokenHelper
{
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    private final static String CACHE_KEY_PREFIX_ID = "fb_";
    
    public String createBridgeToken(long userId)
    {
        String token = new RandomGUID().getValueAfterMD5();
        boolean res = cacheClient.setCache(CACHE_KEY_PREFIX_ID + token, userId, 30000);
        if(res)
        {
            return token;
        }
        throw new InternalServerErrorException("File bridge token to memcache failed.");
    }
    
    
}
