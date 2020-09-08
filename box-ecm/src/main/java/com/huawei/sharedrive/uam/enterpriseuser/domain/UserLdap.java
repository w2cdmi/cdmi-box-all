package com.huawei.sharedrive.uam.enterpriseuser.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserLdap implements Serializable
{
    
    private static final long serialVersionUID = 4569517178368349385L;
    
    private String id;
    
    @NotNull
    @Size(min = 1, max = 255)
    private String sessionId;
    
    @NotNull
    @Size(min = 1, max = 2048)
    private String dn;
    
    private String loginName;
    
    private Long authServerId;
    
    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }
    
    public String getSessionId()
    {
        return sessionId;
    }
    
    public void setDn(String dn)
    {
        this.dn = dn;
    }
    
    public String getDn()
    {
        return dn;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public Long getAuthServerId()
    {
        return authServerId;
    }
    
    public void setAuthServerId(Long authServerId)
    {
        this.authServerId = authServerId;
    }
    
}
