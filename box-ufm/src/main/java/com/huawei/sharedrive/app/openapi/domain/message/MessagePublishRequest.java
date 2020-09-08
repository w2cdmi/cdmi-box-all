package com.huawei.sharedrive.app.openapi.domain.message;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.message.domain.MessageType;

/**
 * 发布消息请求对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-5-11
 * @see
 * @since
 */
public class MessagePublishRequest implements Serializable
{
    
    private static final int MAX_PROVIDER_NAME_LENGTH = 127;
    
    private static final int MAX_TITLE_LENGTH = 255;
    
    private static final int MAX_CONTENT_LENGTH = 2047;
    
    private static final long serialVersionUID = -4910624429910822486L;
    
    // 消息id
    private Long id;
    
    // 消息提供者id
    private Long providerId;
    
    // 消息提供者登录名
    private String providerUsername;
    
    // 消息提供者用户名
    private String providerName;
    
    // 消息类型
    private String type;
    
    // 消息产生时间
    private Long createdAt;
    
    // 消息过期时间
    private Long expiredAt;
    
    private Map<String, Object> params;
    
    
    public void checkParameter()
    {
        if (id == null)
        {
            throw new InvalidParamException("Message id can not be null");
        }
        if (providerId == null)
        {
            throw new InvalidParamException("Message id can not be null");
        }
        checkProviderUsername();
        checkProviderName();
        if (createdAt == null)
        {
            throw new InvalidParamException("Message create time can not be null");
        }
        if (expiredAt == null)
        {
            throw new InvalidParamException("Message expire time can not be null");
        }
        if (expiredAt < createdAt)
        {
            throw new InvalidParamException("Message create time  less than expire time");
        }
        if (type == null)
        {
            throw new InvalidParamException("Message type can not be null");
        }
        
        checkMessageType();
    }

    private void checkProviderName()
    {
        if (providerName == null || providerName.length() == 0
            || providerName.length() > MAX_PROVIDER_NAME_LENGTH)
        {
            throw new InvalidParamException("Invalid provider: " + providerName);
        }
    }

    private void checkProviderUsername()
    {
        if (providerUsername == null || providerUsername.length() == 0
            || providerUsername.length() > MAX_PROVIDER_NAME_LENGTH)
        {
            throw new InvalidParamException("Invalid provider: " + providerUsername);
        }
    }
    
    public Long getCreatedAt()
    {
        return createdAt;
    }
    
    public Long getExpiredAt()
    {
        return expiredAt;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public Map<String, Object> getParams()
    {
        return params;
    }
    
    public Long getProviderId()
    {
        return providerId;
    }
    
    public String getProviderName()
    {
        return providerName;
    }
    
    public String getProviderUsername()
    {
        return providerUsername;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setCreatedAt(Long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public void setExpiredAt(Long expiredAt)
    {
        this.expiredAt = expiredAt;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }
    
    public void setProviderId(Long providerId)
    {
        this.providerId = providerId;
    }
    
    public void setProviderName(String providerName)
    {
        this.providerName = providerName;
    }
    
    public void setProviderUsername(String providerUsername)
    {
        this.providerUsername = providerUsername;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    private void checkMessageType()
    {
        if (params == null || params.isEmpty())
        {
            throw new InvalidParamException("Message params can not be null");
        }
        
        MessageType messageType = MessageType.getMessageType(type);
        if (messageType == null)
        {
            throw new InvalidParamException("Invalid message type: " + type);
        }
        if (messageType == MessageType.SYSTEM)
        {
            String title = String.valueOf(params.get("title"));
            if (StringUtils.isBlank(title) || title.length() > MAX_TITLE_LENGTH)
            {
                throw new InvalidParamException("Invalid message parameter title: " + title);
            }
            String content = String.valueOf(params.get("content"));
            if (StringUtils.isBlank(content) || content.length() > MAX_CONTENT_LENGTH)
            {
                throw new InvalidParamException("Invalid message parameter content: " + content);
            }
            Long announcementId = null;
            try
            {
                announcementId = Long.parseLong(String.valueOf(params.get("announcementId")));
            }
            catch (NumberFormatException e)
            {
                throw new InvalidParamException("announcementId parse to Long type failed", e);
            }
            if (announcementId == null)
            {
                throw new InvalidParamException("Message parameter announcementId can not be null");
            }
        }
        else 
        {
            throw new InvalidParamException("Invalid message type: " + type);
        }
    }
}
