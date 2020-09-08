package com.huawei.sharedrive.uam.message.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 
 * @since
 */
public class MessagePublishRequest implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    private long id;
    
    private long providerId;
    
    private String providerName;
    
    private String providerUsername;
    
    private String type = MessageType.SYSTEM_ANNOUNCEMENT;
    
    private Date createdAt;
    
    private Date expiredAt;
    
    private Map<String, Object> params;
    
    public MessagePublishRequest()
    {
    }
    
    public MessagePublishRequest(Message message)
    {
        this.id = message.getId();
        this.providerId = message.getProviderId();
        this.type = message.getType();
        this.createdAt = message.getCreatedAt();
        this.expiredAt = message.getExpiredAt();
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public long getProviderId()
    {
        return providerId;
    }
    
    public void setProviderId(long providerId)
    {
        this.providerId = providerId;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (null != createdAt)
        {
            this.createdAt = (Date) createdAt.clone();
        }
        else
        {
            this.createdAt = null;
        }
    }
    
    public Date getExpiredAt()
    {
        if (null != expiredAt)
        {
            return (Date) expiredAt.clone();
        }
        return null;
    }
    
    public void setExpiredAt(Date expiredAt)
    {
        if (null != expiredAt)
        {
            this.expiredAt = (Date) expiredAt.clone();
        }
        else
        {
            this.expiredAt = null;
        }
    }
    
    public Map<String, Object> getParams()
    {
        return params;
    }
    
    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }
    
    public String getProviderName()
    {
        return providerName;
    }
    
    public void setProviderName(String providerName)
    {
        this.providerName = providerName;
    }
    
    public String getProviderUsername()
    {
        return providerUsername;
    }
    
    public void setProviderUsername(String providerUsername)
    {
        this.providerUsername = providerUsername;
    }
}
