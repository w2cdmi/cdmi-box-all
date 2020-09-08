package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

public class RestNtlmCreateRequest implements Serializable
{
    private static final long serialVersionUID = -8914601970822036330L;
    
    private String usernameHash;
    
    private String key;
    
    private String challenge;
    
    private String appId;
    
    public String getUsernameHash()
    {
        return usernameHash;
    }
    
    public void setUsernameHash(String usernameHash)
    {
        this.usernameHash = usernameHash;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public void setKey(String key)
    {
        this.key = key;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getChallenge()
    {
        return challenge;
    }
    
    public void setChallenge(String challenge)
    {
        this.challenge = challenge;
    }
}
