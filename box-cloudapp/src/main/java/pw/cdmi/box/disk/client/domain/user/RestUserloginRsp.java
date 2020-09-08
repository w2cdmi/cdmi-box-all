package pw.cdmi.box.disk.client.domain.user;

import pw.cdmi.box.disk.client.api.RestTerminalRsp;

public class RestUserloginRsp
{
    private long concurrent;
    
    private long downloadQos;
    
    private String refreshToken;
    
    private int regionId;
    
    private long toExpiredAt;
    
    private String token;
    
    private String tokenType;
    
    private long uploadQos;
    
    private long userId;
    
    private long cloudUserId;
    
    private String username;
    
    private long timeout;
    
    private RestTerminalRsp lastAccessTerminal;
    
    public long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public void setCloudUserId(long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    public long getTimeout()
    {
        return timeout;
    }
    
    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
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

    public RestTerminalRsp getLastAccessTerminal()
    {
        return lastAccessTerminal;
    }

    public void setLastAccessTerminal(RestTerminalRsp lastAccessTerminal)
    {
        this.lastAccessTerminal = lastAccessTerminal;
    }
    
}
