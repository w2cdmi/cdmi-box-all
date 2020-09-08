package com.huawei.sharedrive.app.oauth2.service;

import com.huawei.sharedrive.app.oauth2.domain.UserToken;

/**
 * 机机接口用户身份构造器
 * @author pWX231110
 *
 */
public final class UserLogTokenBuilder
{
    private UserLogTokenBuilder()
    {
        
    }
    
    public static final long DEFAULT_CLOUDUSERID = -2;
    
    public static final String DEFAULT_LOGIN_NAME = "_ADMIN";
    
    /**
     * 构造机机接口token，供记录日志使用
     * @param appId
     * @return
     */
    public static UserToken buildAppToken(String appId)
    {
        UserToken token = new UserToken();
        token.setAppId(appId);
        token.setCloudUserId(DEFAULT_CLOUDUSERID);
        token.setLoginName(appId + DEFAULT_LOGIN_NAME);
        return token;
    }
    
}
