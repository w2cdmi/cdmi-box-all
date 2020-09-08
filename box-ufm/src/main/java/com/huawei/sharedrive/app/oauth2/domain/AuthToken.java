package com.huawei.sharedrive.app.oauth2.domain;

import java.util.Date;

/**
 * @author c90006080
 * @version 1.0
 * @created 26-十一月-2013 17:24:11
 */
public class AuthToken
{
    
    /**
     * 权限描述信息
     */
    private String auth;
    
    /**
     * 授权码
     */
    private String code;
    
    private Date createdAt;
    
    private String deviceSN;
    
    /**
     * 过期时间
     */
    private Date expiredAt;
    
    /**
     * 刷新Token
     */
    private String refreshToken;
    
    /**
     * 访问 临时Token 信息
     */
    private String token;
    
    /**
     * 授权Token 类型
     */
    private String type;
    
    private Long userId;
    
    /**
     * @return the auth
     */
    public String getAuth()
    {
        return auth;
    }
    
    /**
     * @return the code
     */
    public String getCode()
    {
        return code;
    }
    
    /**
     * @return the createdAt
     */
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public String getDeviceSN()
    {
        return deviceSN;
    }
    
    /**
     * @return the expiredAt
     */
    public Date getExpiredAt()
    {
        if (expiredAt == null)
        {
            return null;
        }
        return (Date) expiredAt.clone();
    }
    
    /**
     * @return the refreshToken
     */
    public String getRefreshToken()
    {
        return refreshToken;
    }
    
    /**
     * @return the token
     */
    public String getToken()
    {
        return token;
    }
    
    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * @return the userId
     */
    public Long getUserId()
    {
        return userId;
    }
    
    /**
     * @param auth the auth to set
     */
    public void setAuth(String auth)
    {
        this.auth = auth;
    }
    
    /**
     * @param code the code to set
     */
    public void setCode(String code)
    {
        this.code = code;
    }
    
    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
    }
    
    public void setDeviceSN(String deviceSN)
    {
        this.deviceSN = deviceSN;
    }
    
    /**
     * @param expiredAt the expiredAt to set
     */
    public void setExpiredAt(Date expiredAt)
    {
        if (expiredAt == null)
        {
            this.expiredAt = null;
        }
        else
        {
            this.expiredAt = (Date) expiredAt.clone();
        }
    }
    
    /**
     * @param refreshToken the refreshToken to set
     */
    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }
    
    /**
     * @param token the token to set
     */
    public void setToken(String token)
    {
        this.token = token;
    }
    
    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }
    
    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    
}