package com.huawei.sharedrive.app.openapi.domain.message;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.huawei.sharedrive.app.files.domain.NodeType;
import com.huawei.sharedrive.app.message.domain.Message;
import com.huawei.sharedrive.app.message.domain.MessageStatus;
import com.huawei.sharedrive.app.message.domain.MessageType;
import com.huawei.sharedrive.app.utils.BusinessConstants;

import pw.cdmi.core.utils.JsonUtils;

public class MessageResponse implements Serializable
{
    private static final long serialVersionUID = 1920786805943039004L;
    
    // 消息id
    private long id;
    
    // 消息提供者id
    private long providerId;
    
    // 消息提供者用户名
    private String providerUsername;
    
    // 消息提供者姓名
    private String providerName;
    
    // 消息接收者id;
    private long receiverId;
    
    // 产生消息的应用id
    private String appId;
    
    // 消息类型
    private String type;
    
    // 消息状态
    private String status;
    
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
        this.appId = message.getAppId();
        this.createdAt = message.getCreatedAt();
        this.expiredAt = message.getExpiredAt();
        this.id = message.getId();
        this.providerId = message.getProviderId();
        this.receiverId = message.getReceiverId();
        this.status = MessageStatus.getStatus(message.getStatus());
        this.type = MessageType.getType(message.getType());
        String paramStr = message.getParams();
        if (StringUtils.isNotBlank(paramStr))
        {
            Map<String, Object> params = JsonUtils.stringToMap(paramStr);
            if (params != null)
            {
                this.providerName = String.valueOf(params.get(MessageParamName.PROVIDER_NAME));
                this.providerUsername = String.valueOf(params.get(MessageParamName.PROVIDER_USERNAME));
                Object nodeId = params.get(MessageParamName.NODE_ID);
                if(nodeId != null)
                {
                    addParam(MessageParamName.NODE_ID, Long.parseLong(String.valueOf(nodeId)));
                }
                addParam(MessageParamName.NODE_NAME, params.get(MessageParamName.NODE_NAME));
                Object nodeType = params.get(MessageParamName.NODE_TYPE);
                if(nodeType != null)
                {
                    addParam(MessageParamName.NODE_TYPE,NodeType.getType(Byte.parseByte(String.valueOf(nodeType))));
                }
                
                addParam(MessageParamName.TEAMSPACE_ID, params.get(MessageParamName.TEAMSPACE_ID));
                addParam(MessageParamName.TEAMSPACE_NAME, params.get(MessageParamName.TEAMSPACE_NAME));
                addParam(MessageParamName.GROUP_ID, params.get(MessageParamName.GROUP_ID));
                addParam(MessageParamName.GROUP_NAME, params.get(MessageParamName.GROUP_NAME));
                addParam(MessageParamName.TITLE, params.get(MessageParamName.TITLE));
                addParam(MessageParamName.CONTENT, params.get(MessageParamName.CONTENT));
                addParam(MessageParamName.ANNOUNCEMENT_ID, params.get(MessageParamName.ANNOUNCEMENT_ID));
                addParam(MessageParamName.CURRENT_ROLE, params.get(MessageParamName.CURRENT_ROLE)); 
            }
        }
    }
    
    public void addParam(String name, Object value)
    {
        if (params == null)
        {
            params = new HashMap<String, Object>(BusinessConstants.INITIAL_CAPACITIES);
        }
        if (value != null)
        {
            params.put(name, value);
        }
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public Date getExpiredAt()
    {
        if (expiredAt == null)
        {
            return null;
        }
        return (Date) expiredAt.clone();
    }
    
    public long getId()
    {
        return id;
    }
    
    public Object getParam(String name)
    {
        if (params == null)
        {
            return null;
        }
        return params.get(name);
    }
    
    public Map<String, Object> getParams()
    {
        return params;
    }
    
    public long getProviderId()
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
    
    public long getReceiverId()
    {
        return receiverId;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
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
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }
    
    public void setProviderId(long providerId)
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
    
    public void setReceiverId(long receiverId)
    {
        this.receiverId = receiverId;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
    
}
