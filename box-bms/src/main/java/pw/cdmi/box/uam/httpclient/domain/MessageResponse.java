package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import pw.cdmi.box.uam.message.domain.Message;
import pw.cdmi.box.uam.message.domain.MessageType;

/**
 * 消息对象
 * 
 * @version CloudStor CSE Service Platform Subproject, 2015-3-11
 * @see
 * @since
 */
public class MessageResponse implements Serializable
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
    
    // 消息接收者id;
    private long receiverId;
    
    // 消息状态
    private String status = MessageStatusEnum.UNREAD.getDesc();
    
    // 消息产生时间
    private Date createdAt;
    
    // 消息过期时间
    private Date expiredAt;
    
    // 消息参数
    private Map<String, Object> params;
    
    public MessageResponse()
    {
    }
    
    public MessageResponse(Message message)
    {
        this.id = message.getId();
        this.providerId = message.getProviderId();
        this.receiverId = message.getReceiverId();
        this.type = message.getType();
        MessageStatusEnum statusEnum = MessageStatusEnum.parseByCode(message.getStatus());
        if (null != statusEnum)
        {
            this.status = statusEnum.getDesc();
        }
        this.createdAt = message.getCreatedAt();
        this.expiredAt = message.getExpiredAt();
        this.params = message.getParams();
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
    
    public long getReceiverId()
    {
        return receiverId;
    }
    
    public void setReceiverId(long receiverId)
    {
        this.receiverId = receiverId;
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
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public Map<String, Object> getParams()
    {
        return params;
    }
    
    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }
}
