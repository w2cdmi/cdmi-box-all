package com.huawei.sharedrive.uam.sso.api.domain;

import java.io.Serializable;

public class RestSSOLoginRequest implements Serializable
{
    private static final long serialVersionUID = 1097679602380696071L;
    
    private String appId;
    
    private String ssoToken;
    
    private String domain;
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getSsoToken()
    {
        return ssoToken;
    }
    
    public void setSsoToken(String ssoToken)
    {
        this.ssoToken = ssoToken;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
}
