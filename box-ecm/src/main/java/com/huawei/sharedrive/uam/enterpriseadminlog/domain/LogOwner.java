package com.huawei.sharedrive.uam.enterpriseadminlog.domain;

import java.io.Serializable;

public class LogOwner implements Serializable
{
    
    private static final long serialVersionUID = 5543545181087709037L;
    
    private Long enterpriseId;
    
    private String loginName;
    
    private String ip;
    
    private String appId;
    
    private Long authServerId;
    
    private String operatType;

    private String description;

    public Long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public String getIp()
    {
        return ip;
    }
    
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public Long getAuthServerId()
    {
        return authServerId;
    }
    
    public void setAuthServerId(Long authServerId)
    {
        this.authServerId = authServerId;
    }
    
    public String getOperatType()
    {
        return operatType;
    }
    
    public void setOperatType(String operatType)
    {
        this.operatType = operatType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
