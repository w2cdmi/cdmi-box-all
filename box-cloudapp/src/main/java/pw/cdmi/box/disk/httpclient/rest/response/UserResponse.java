package pw.cdmi.box.disk.httpclient.rest.response;

public class UserResponse extends BaseResponse
{
    private long concurrent;
    
    private long downloadQos;
    
    private String refreshToken;
    
    private int regionId;
    
    private long toExpiredAt;
    
    private String token;
    
    private String tokenType;
    
    private long uploadQos;
    
    private String userId;
    
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
    
    public String getUserId()
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
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
}
