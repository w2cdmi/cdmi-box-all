package com.huawei.sharedrive.app.oauth2.domain;

import java.util.Date;

public class AuthCode
{
    
    /**
     * 用于描述权限信息
     */
    private String auth;
    
    /**
     * 客户端ID
     */
    private String clientId;
    
    /**
     * 授权码
     */
    private String code;
    
    private Date createdAt;
    
    /**
     * 设备编号
     */
    private String deviceId;
    
    /**
     * 授权用户信息
     */
    private Long userId;
    
    /**
     * @return the auth
     */
    public String getAuth()
    {
        return auth;
    }
    
    /**
     * @return the clientId
     */
    public String getClientId()
    {
        return clientId;
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
    
    /**
     * @return the deviceId
     */
    public String getDeviceId()
    {
        return deviceId;
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
     * @param clientId the clientId to set
     */
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
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
    
    /**
     * @param deviceId the deviceId to set
     */
    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }
    
    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    
}