package com.huawei.sharedrive.app.openapi.domain.user;

import java.io.Serializable;

/**
 * 
 * @author h90005572
 * 
 */
public class RestTokenCreateResponse implements Serializable
{
    private static final long serialVersionUID = -8333104483048276667L;

    private String token;
    
    private int timeout;

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
