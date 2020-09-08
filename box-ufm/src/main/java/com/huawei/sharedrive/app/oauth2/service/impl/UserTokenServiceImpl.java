/**
 * 
 */
package com.huawei.sharedrive.app.oauth2.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.oauth2.domain.DataServerToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenService;

import pw.cdmi.common.cache.CacheClient;

/**
 * 
 * @author q90003805
 * 
 */
@Component
public class UserTokenServiceImpl implements UserTokenService
{
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    /** temp token失效时间 */
    @Value("${auth2.token.temp.expire.second}")
    private int tempTokenExpired;
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.app.oauth2.service.impl.UserTokenService#deleteUserToken(
     * java.lang.String, boolean)
     */
    @Override
    public void deleteUserToken(String token)
    {
        String key = DataServerToken.CACHE_KEY_TEMP_PREFIX_ID + token;
        DataServerToken dataServerToken = (DataServerToken) cacheClient.getCache(key);
        if (dataServerToken != null)
        {
            cacheClient.deleteCache(key);
        }
    }
    
    @Override
    public DataServerToken getUserToken(String token)
    {
        String key = DataServerToken.CACHE_KEY_TEMP_PREFIX_ID + token;
        DataServerToken tokenValue = (DataServerToken) cacheClient.getCache(key);
        return tokenValue;
    }
    
    @Override
    public void saveUserToken(DataServerToken dataServerToken) throws InternalServerErrorException
    {
        String key = DataServerToken.CACHE_KEY_TEMP_PREFIX_ID + dataServerToken.getToken();
        if (dataServerToken.getExpiredAt() == null)
        {
            Date expireTime = new Date(System.currentTimeMillis() + tempTokenExpired*30);
            dataServerToken.setExpiredAt(expireTime);
        }
        boolean success = cacheClient.setCache(key, dataServerToken, dataServerToken.getExpiredAt());
        if (!success)
        {
            throw new InternalServerErrorException("Store token to memcache failed.");
        }
    }
    
    @Override
    public void updateTempToken(String token) throws InternalServerErrorException
    {
        String key = DataServerToken.CACHE_KEY_TEMP_PREFIX_ID + token;
        DataServerToken dataServerToken = (DataServerToken) cacheClient.getCache(key);
        if (dataServerToken != null)
        {
            Date expireTime = new Date(System.currentTimeMillis() + tempTokenExpired);
            dataServerToken.setExpiredAt(expireTime);
            boolean success = cacheClient.setCache(key, dataServerToken, dataServerToken.getExpiredAt());
            if (!success)
            {
                throw new InternalServerErrorException("Store token to memcache failed.");
            }
        }
    }
    
}
