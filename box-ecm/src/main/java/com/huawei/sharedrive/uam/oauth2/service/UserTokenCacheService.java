package com.huawei.sharedrive.uam.oauth2.service;

import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;

import java.util.Date;

public interface UserTokenCacheService {
    /**
     * 根据accessToken获取UserToken
     */
    UserToken getUserToken(String token);

    void saveUserToken(UserToken userToken);

    /**
     * 使用refreshToken获取UserToken（包括accessToken）
     */
    UserToken getUserTokenByRefreshToken(String refreshToken);

    /**
     * 删除token，用于logout
     */
    void deleteToken(String token);

    //清除某用户下所有的token
    void deleteUserToken(long userId);
}
