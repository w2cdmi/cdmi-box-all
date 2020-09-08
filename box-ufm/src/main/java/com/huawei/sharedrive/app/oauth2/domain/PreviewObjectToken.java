package com.huawei.sharedrive.app.oauth2.domain;

import java.util.Date;

public class PreviewObjectToken implements DataServerToken
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 3290451945819504708L;
    
    private Date expiredAt;
    
    private String token;
    
    private String tokenType;
    
    private String auth;
    
    private String sourceObjectId;
    
    private long accountId;
    
    private Date convertRealStartTime;
    
    private int resourceGroupId;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public PreviewObjectToken(String tokenType, String auth, String token, Date expiredAt,
        String sourceObjectId, long accountId, Date convertRealStartTime, int resourceGroupId)
    {
        this.tokenType = tokenType;
        this.auth = auth;
        this.token = token;
        if (expiredAt == null)
        {
            this.expiredAt = null;
        }
        else
        {
            this.expiredAt = (Date) expiredAt.clone();
        }
        this.sourceObjectId = sourceObjectId;
        this.accountId = accountId;
        if (convertRealStartTime == null)
        {
            this.convertRealStartTime = null;
        }
        else
        {
            this.convertRealStartTime = (Date) convertRealStartTime.clone();
        }
        this.resourceGroupId = resourceGroupId;
    }
    
    public PreviewObjectToken()
    {
        
    }
    
    @Override
    public String getToken()
    {
        return token;
    }
    
    @Override
    public void setToken(String token)
    {
        this.token = token;
    }
    
    @Override
    public Date getExpiredAt()
    {
        if (expiredAt == null)
        {
            return null;
        }
        return (Date) expiredAt.clone();
    }
    
    @Override
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
    
    @Override
    public String getTokenType()
    {
        return tokenType;
    }
    
    @Override
    public void setTokenType(String tokenType)
    {
        this.tokenType = tokenType;
    }
    
    @Override
    public String getAuth()
    {
        return auth;
    }
    
    @Override
    public void setAuth(String auth)
    {
        this.auth = auth;
    }
    
    public String getSourceObjectId()
    {
        return sourceObjectId;
    }
    
    public void setSourceObjectId(String sourceObjectId)
    {
        this.sourceObjectId = sourceObjectId;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public Date getConvertRealStartTime()
    {
        if (convertRealStartTime == null)
        {
            return null;
        }
        return (Date) convertRealStartTime.clone();
    }
    
    public void setConvertRealStartTime(Date convertRealStartTime)
    {
        if (convertRealStartTime == null)
        {
            this.convertRealStartTime = null;
        }
        else
        {
            this.convertRealStartTime = (Date) convertRealStartTime.clone();
        }
    }
    
    public int getResourceGroupId()
    {
        return resourceGroupId;
    }
    
    public void setResourceGroupId(int resourceGroupId)
    {
        this.resourceGroupId = resourceGroupId;
    }
    
}
