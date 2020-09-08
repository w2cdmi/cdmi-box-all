package com.huawei.sharedrive.app.oauth2.dao;

import java.util.List;

import com.huawei.sharedrive.app.oauth2.domain.AuthToken;

import pw.cdmi.box.dao.BaseDAO;

public interface AuthTokenDAO extends BaseDAO<AuthToken, String>
{
    
    /**
     * 清除过期的Token
     * 
     * @return
     */
    int deleteDeviceSNToken(String deviceSN);
    
    /**
     * 清除过期的Token
     * 
     * @return
     */
    int deleteExpiredToken();
    
    /**
     * 通过Refresh Token 查找Token
     * 
     * @param refreshToken
     * @return
     */
    AuthToken getByRefreshToken(String refreshToken);
    
    /**
     * 每次获取1000条
     * 
     * @param offset
     * @return
     */
    List<AuthToken> getExpiredToken(long offset);
}
