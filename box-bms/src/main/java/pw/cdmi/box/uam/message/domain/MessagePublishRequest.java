package pw.cdmi.box.uam.message.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 消息对象
 * 
 * @since
 */
public class MessagePublishRequest implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    // 消息id
    private long id;
    
    // 消息提供者id
    private long providerId;
    
    private String providerName;
    
    private String providerUsername;
    
    // 消息类型
    private String type = MessageType.SYSTEM_ANNOUNCEMENT;
    
    // 消息产生时间
    private Date createdAt;
    
    // 消息过期时间
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
        return createdAt == null ? null : (Date) createdAt.clone();
    }
    
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = (createdAt == null ? null : (Date) createdAt.clone());
    }
    
    public Date getExpiredAt()
    {
        return expiredAt == null ? null : (Date) expiredAt.clone();
    }
    
    public void setExpiredAt(Date expiredAt)
    {
        this.expiredAt = (expiredAt == null ? null : (Date) expiredAt.clone());
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
