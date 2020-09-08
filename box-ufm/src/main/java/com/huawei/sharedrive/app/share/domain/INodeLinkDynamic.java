package com.huawei.sharedrive.app.share.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class INodeLinkDynamic
{
    private String id;
    
    private String identity;
    
    @JsonIgnore
    private String password;
    
    @JsonIgnore
    private String encryptedPassword;
    
    @JsonIgnore
    private String passwordKey;
    
    private Date createdAt;
    
    private Date expiredAt;
    
    public INodeLinkDynamic()
    {
    
    }
    
    public INodeLinkDynamic(String id)
    {
        this.id = id;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getIdentity()
    {
        return identity;
    }
    
    public void setIdentity(String identity)
    {
        this.identity = identity;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
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
    
    public Date getExpiredAt()
    {
        if (expiredAt == null)
        {
            return null;
        }
        return (Date) expiredAt.clone();
    }
    
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
    
    public String getEncryptedPassword()
    {
        return encryptedPassword;
    }
    
    public void setEncryptedPassword(String encryptedPassword)
    {
        this.encryptedPassword = encryptedPassword;
    }
    
    public String getPasswordKey()
    {
        return passwordKey;
    }
    
    public void setPasswordKey(String passwordKey)
    {
        this.passwordKey = passwordKey;
    }
    
}