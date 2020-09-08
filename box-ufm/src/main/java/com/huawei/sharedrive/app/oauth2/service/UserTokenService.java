package com.huawei.sharedrive.app.oauth2.service;

import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.oauth2.domain.DataServerToken;

/**
 * 
 * @author q90003805
 * 
 */
public interface UserTokenService
{
    
    void deleteUserToken(String token);
    
    DataServerToken getUserToken(String token);
    
    void saveUserToken(DataServerToken token) throws InternalServerErrorException;
    
    /**
     * 更新临时token,延长临时token 30分钟有效时间
     * 
     * @param token
     * @throws InternalServerErrorException
     */
    void updateTempToken(String token) throws InternalServerErrorException;
    
}