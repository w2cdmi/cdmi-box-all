package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

public class RestTokenCreateResponse implements Serializable
{
    private static final long serialVersionUID = -8333104483048276667L;
    
    private String token;
    
    private String refreshToken;
    
    private int timeout;
    
    private String loginName;
    
    private long userId;
    
    private long cloudUserId;
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public void setCloudUserId(long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
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
