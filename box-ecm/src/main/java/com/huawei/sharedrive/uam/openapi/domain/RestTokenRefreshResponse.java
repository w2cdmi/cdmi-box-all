package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

public class RestTokenRefreshResponse implements Serializable
{
    private static final long serialVersionUID = -8434440505773006119L;
    
    private String token;
    
    private String refreshToken;
    
    private int timeout;
    
    public String getRefreshToken()
    {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public void setToken(String token)
    {
        this.token = token;
    }
    
    public int getTimeout()
    {
        return timeout;
    }
    
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }
}
