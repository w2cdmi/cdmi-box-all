package pw.cdmi.box.disk.oauth2.domain;

import java.util.Date;

public class AuthToken
{
    
    private String auth;
    
    private String code;
    
    private Date createdAt;
    
    private String deviceSn;
    
    private Date expiredAt;
    
    private String refreshToken;
    
    private String token;
    
    private String type;
    
    private Long userId;
    
    /**
     * @return the auth
     */
    public String getAuth()
    {
        return auth;
    }
    
    /**
     * @return the code
     */
    public String getCode()
    {
        return code;
    }
    
    /**
     * @return the createdAt
     */
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public String getDeviceSn()
    {
        return deviceSn;
    }
    
    /**
     * @return the expiredAt
     */
    public Date getExpiredAt()
    {
        if (expiredAt == null)
        {
            return null;
        }
        return (Date) expiredAt.clone();
    }
    
    /**
     * @return the refreshToken
     */
    public String getRefreshToken()
    {
        return refreshToken;
    }
    
    /**
     * @return the token
     */
    public String getToken()
    {
        return token;
    }
    
    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * @return the userId
     */
    public Long getUserId()
    {
        return userId;
    }
    
    /**
     * @param auth the auth to set
     */
    public void setAuth(String auth)
    {
        this.auth = auth;
    }
    
    /**
     * @param code the code to set
     */
    public void setCode(String code)
    {
        this.code = code;
    }
    
    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
    }
    
    public void setDeviceSn(String deviceSn)
    {
        this.deviceSn = deviceSn;
    }
    
    /**
     * @param expiredAt the expiredAt to set
     */
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
    
    /**
     * @param refreshToken the refreshToken to set
     */
    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }
    
    /**
     * @param token the token to set
     */
    public void setToken(String token)
    {
        this.token = token;
    }
    
    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }
    
    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    
}