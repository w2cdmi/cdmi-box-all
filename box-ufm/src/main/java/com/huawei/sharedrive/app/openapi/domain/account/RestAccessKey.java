package com.huawei.sharedrive.app.openapi.domain.account;

import com.huawei.sharedrive.app.account.domain.AccountAccessKey;

public class RestAccessKey
{
    private String accessKey;
    
    private String secretKey;
    
    public RestAccessKey()
    {
        
    }
    public RestAccessKey(AccountAccessKey key)
    {
        accessKey = key.getId();
        secretKey = key.getSecretKey();
    }
    
    public String getAccessKey()
    {
        return accessKey;
    }
    
    public void setAccessKey(String accessKey)
    {
        this.accessKey = accessKey;
    }
    
    public String getSecretKey()
    {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }
    
}
