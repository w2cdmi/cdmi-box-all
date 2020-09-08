package pw.cdmi.box.disk.httpclient.rest.response;

public class TokenResponse
{
    private String periodAt;
    
    private String refreshToken;
    
    private String token;
    
    private String tokenType;
    
    private String userId;
    
    public String getPeriodAt()
    {
        return periodAt;
    }
    
    public String getRefreshToken()
    {
        return refreshToken;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public String getTokenType()
    {
        return tokenType;
    }
    
    public void setPeriodAt(String periodAt)
    {
        this.periodAt = periodAt;
    }
    
    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }
    
    public void setToken(String token)
    {
        this.token = token;
    }
    
    public void setTokenType(String tokenType)
    {
        this.tokenType = tokenType;
    }
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
}
