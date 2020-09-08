package com.huawei.sharedrive.uam.oauth2.service.impl;

import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.UserTokenCacheService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pw.cdmi.common.cache.CacheClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class UserTokenCacheServiceImpl implements UserTokenCacheService
{
    public static final String CACHE_KEY_SESSION_LIST_PREFIX = "uam_session_list_";

    public static final String CACHE_KEY_USER_TOKEN_PREFIX = "uam_user_token_";

    public static final String CACHE_KEY_REFRESH_TOKEN_PREFIX = "uam_refresh_token_";
    
    public static final String CACHE_KEY_TOKEN_REFRESH_PREFIX = "uam_token_refresh_";
    
    public static final String CACHE_KEY_CLOUD_USER_ID_PREFIX = "uam_cloud_user_id_";
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    @Value("${auth2.token.expire}")
    private long tokenExpireTime = 1800000;
    
    @Value("${auth2.refresh.token.expire}")
    private long refreshTokenExpireTime;

    /**
     * 缓存引用关系：
     * cloudUserId  -> List<userKeyId>
     * accessToken  -> userKeyId -> userToken
     * accessToken  -> refreshToken
     * refreshToken -> userToken
     */
    @Override
    public void saveUserToken(UserToken userToken) {
        //未指定失效时间，使用默认值。
        if(userToken.getExpiredAt() == null) {
            userToken.setExpiredAt(new Date(System.currentTimeMillis() + tokenExpireTime));
        }

        //缓存同一个cloudUserId下的session（每个session对应不同的登录终端）
        String cloudUserId = String.valueOf(userToken.getCloudUserId());
        Date expireTime = userToken.getExpiredAt();
        String sessionInfo = getUserSessionInfo(userToken);

        //缓存session信息
        saveCloudUserIdAndSessionInfoMapping(cloudUserId, sessionInfo, expireTime);

        //缓存accessToken与userInfo
        String token = userToken.getToken();
        saveAccessTokenAndSessionInfoMapping(token, sessionInfo, expireTime);

        //缓存userInfo与UserToken
        saveSessionInfoAndUserTokenMapping(sessionInfo, userToken, expireTime);

        //缓存accessToken与refreshToken
        saveAccessTokenAndRefreshTokenMapping(token, userToken.getRefreshToken(), expireTime);

        //缓存refreshToken与UserToken
        saveRefreshTokenAndUserTokenMapping(userToken.getRefreshToken(), userToken, refreshTokenExpireTime);
    }

    @Override
    public UserToken getUserTokenByRefreshToken(String refreshToken) {
        String key = getRefreshTokenKey(refreshToken);
        return (UserToken) cacheClient.getCache(key);
    }
    
    @Override
    public UserToken getUserToken(String token) {
        String sessionInfo = getSessionInfoByAccessToken(token);
        if (StringUtils.isBlank(sessionInfo)) {
            return null;
        }

        return getUserTokenBySessionInfo(sessionInfo);
    }

    @Override
    public void deleteToken(String token) {
        String sessionInfo = getSessionInfoByAccessToken(token);
        if(StringUtils.isNotBlank(sessionInfo)) {
            UserToken userToken = getUserTokenBySessionInfo(sessionInfo);
            if(userToken != null) {
                String cloudUserId = String.valueOf(userToken.getCloudUserId());
                //从session列表中删除当前的session
                deleteCloudUserIdAndSessionInfoMapping(cloudUserId, sessionInfo);

                //删除refreshToken与userT
                deleteRefreshTokenAndUserTokenMapping(userToken.getRefreshToken());
            }

            //删除session与userToken
            deleteSessionInfoAndUserTokenMapping(sessionInfo);
        }

        //删除accessToken与session
        deleteAccessTokenAndSessionInfoMapping(token);

        //删除accessToken与refreshToken
        deleteAccessTokenAndRefreshTokenMapping(token);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deleteUserToken(long cloudUserId) {
        //主要删除userToken相关的缓存，其他缓存可以等memcache超时机制清除。
        List<String> sessionList = getSessionInfoByCloudUserId(String.valueOf(cloudUserId));
        if(sessionList != null) {
            for (String session : sessionList) {
                UserToken userToken = getUserTokenBySessionInfo(session);
                if(userToken != null) {
                    //删除refreshToken与userT
                    deleteRefreshTokenAndUserTokenMapping(userToken.getRefreshToken());
                }

                //删除session与userToken
                deleteSessionInfoAndUserTokenMapping(session);
            }
        }

        //清空用户对应的session列表
        deleteCloudUserIdAndSessionInfoMapping(String.valueOf(cloudUserId));
    }

    @SuppressWarnings("unchecked")
    private List<String> getSessionInfoByCloudUserId(String cloudUserId) {
        String key = getSessionListKey(cloudUserId);
        return (List<String>) cacheClient.getCache(key);
    }

    @SuppressWarnings("unchecked")
    private void saveCloudUserIdAndSessionInfoMapping(String cloudUserId, String sessionInfo, Date expireTime) {
        String key = getSessionListKey(cloudUserId);
        List<String> sessionList = (List<String>) cacheClient.getCache(key);
        if (sessionList == null) {
            sessionList = new ArrayList<>(16);
        }

        if (!sessionList.contains(sessionInfo)) {
            sessionList.add(sessionInfo);
            cacheClient.setCache(key, sessionList, expireTime);
        }
    }

    @SuppressWarnings("unchecked")
    private void deleteCloudUserIdAndSessionInfoMapping(String cloudUserId, String sessionInfo) {
        String key = getSessionListKey(cloudUserId);
        List<String> sessionList = (List<String>) cacheClient.getCache(key);
        if(sessionList != null) {
            if(sessionList.remove(sessionInfo)) {
                //此处只能使用默认的Token超时值
                cacheClient.setCache(cloudUserId, sessionList, new Date(System.currentTimeMillis() + tokenExpireTime));
            }
        }
    }

    private void deleteCloudUserIdAndSessionInfoMapping(String cloudUserId) {
        String key = getSessionListKey(cloudUserId);
        cacheClient.deleteCache(key);
    }

    private String getSessionInfoByAccessToken(String accessToken) {
        String key = getTokenKey(accessToken);
        return (String)cacheClient.getCache(key);
    }

    private void saveAccessTokenAndSessionInfoMapping(String accessToken, String userInfo, Date expireTime) {
        String tokenKey = getTokenKey(accessToken);
        cacheClient.setCache(tokenKey, userInfo, expireTime);
    }

    private void deleteAccessTokenAndSessionInfoMapping(String accessToken) {
        String tokenKey = getTokenKey(accessToken);
        cacheClient.deleteCache(tokenKey);
    }

    private UserToken getUserTokenBySessionInfo(String sessionInfo) {
        String key = getSessionKey(sessionInfo);
        return (UserToken)cacheClient.getCache(key);
    }

    private void saveSessionInfoAndUserTokenMapping(String sessionInfo, UserToken userToken, Date expireTime) {
        String key = getSessionKey(sessionInfo);
        cacheClient.setCache(key, userToken, expireTime);
    }

    private void deleteSessionInfoAndUserTokenMapping(String userInfo) {
        String key = getSessionKey(userInfo);
        cacheClient.deleteCache(key);
    }

    private void saveAccessTokenAndRefreshTokenMapping(String accessToken, String refreshToken, Date expireTime) {
        String key = getAccessToken_RefreshTokenKey(accessToken);
        cacheClient.setCache(key, refreshToken, expireTime);
    }

    private void deleteAccessTokenAndRefreshTokenMapping(String accessToken) {
        String key = getAccessToken_RefreshTokenKey(accessToken);
        cacheClient.deleteCache(key);
    }

    private void saveRefreshTokenAndUserTokenMapping(String refreshToken, UserToken userToken, long refreshTokenExpireTime) {
        String key = getRefreshTokenKey(refreshToken);
        if (refreshTokenExpireTime > 0) {
            cacheClient.setCache(key, userToken, new Date(System.currentTimeMillis() + refreshTokenExpireTime));
        } else {
            cacheClient.setCacheNoExpire(key, userToken);
        }
    }

    private void deleteRefreshTokenAndUserTokenMapping(String refreshToken) {
        String key = getRefreshTokenKey(refreshToken);
        cacheClient.deleteCache(key);
    }

    private String getSessionKey(String cloudUserId) {
        return CACHE_KEY_CLOUD_USER_ID_PREFIX + cloudUserId;
    }

    private String getSessionListKey(String cloudUserId) {
        return CACHE_KEY_SESSION_LIST_PREFIX + cloudUserId;
    }
    private String getRefreshTokenKey(String refreshToken) {
        return CACHE_KEY_REFRESH_TOKEN_PREFIX + refreshToken;
    }

    private String getTokenKey(String token) {
        return CACHE_KEY_USER_TOKEN_PREFIX + token;
    }

    private String getAccessToken_RefreshTokenKey(String token) {
        return CACHE_KEY_TOKEN_REFRESH_PREFIX + token;
    }

    private String getUserSessionInfo(UserToken userToken) {
        StringBuilder userKeyId = new StringBuilder();
        userKeyId.append("cloudUserId_");
        userKeyId.append(userToken.getCloudUserId());
        userKeyId.append("_deviceType_");
        userKeyId.append(userToken.getDeviceType());
        userKeyId.append("_ip_");
        userKeyId.append(userToken.getDeviceAddress());
        userKeyId.append("_agent_");
        userKeyId.append(userToken.getDeviceAgent());
        return userKeyId.toString();
    }
}
