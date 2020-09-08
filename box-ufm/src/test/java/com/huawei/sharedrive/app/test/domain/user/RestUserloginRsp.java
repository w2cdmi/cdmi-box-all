package com.huawei.sharedrive.app.test.domain.user;

public class RestUserloginRsp
{
    
    private int timeout;
    
    private String objectSid;
    
    private String name;
    
    private String loginName;
    
    
    private long cloudUserId;
    
    private String email;
    
    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public String getObjectSid()
    {
        return objectSid;
    }

    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

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

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public byte getStatus()
    {
        return status;
    }

    public void setStatus(byte status)
    {
        this.status = status;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    private byte status;
    
    
    private String appId;
    
    private long concurrent;
    
    private long downloadQos;
    
    private String refreshToken;
    
    private int regionId;
    
    private long toExpiredAt;
    
    private String token;
    
    private String tokenType;
    
    private long uploadQos;
    
    private long userId;
    
    public long getConcurrent()
    {
        return concurrent;
    }

    public long getDownloadQos()
    {
        return downloadQos;
    }

    public String getRefreshToken()
    {
        return refreshToken;
    }
    
    public int getRegionId()
    {
        return regionId;
    }
    
    public long getToExpiredAt()
    {
        return toExpiredAt;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public String getTokenType()
    {
        return tokenType;
    }
    
    public long getUploadQos()
    {
        return uploadQos;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setConcurrent(long concurrent)
    {
        this.concurrent = concurrent;
    }
    
    
    public void setDownloadQos(long downloadQos)
    {
        this.downloadQos = downloadQos;
    }
    
    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }
    
    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }
    
    public void setToExpiredAt(long toExpiredAt)
    {
        this.toExpiredAt = toExpiredAt;
    }
    
    public void setToken(String token)
    {
        this.token = token;
    }
    
    public void setTokenType(String tokenType)
    {
        this.tokenType = tokenType;
    }
    
    public void setUploadQos(long uploadQos)
    {
        this.uploadQos = uploadQos;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
}
