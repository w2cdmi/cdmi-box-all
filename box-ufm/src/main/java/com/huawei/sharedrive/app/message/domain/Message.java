package com.huawei.sharedrive.app.message.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.huawei.sharedrive.app.files.domain.NodeType;
import com.huawei.sharedrive.app.openapi.domain.message.MessageParamName;
import com.huawei.sharedrive.app.openapi.domain.message.MessagePublishRequest;
import com.huawei.sharedrive.app.openapi.domain.message.MessageResponse;

import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;

/**
 * 消息对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-3-11
 * @see
 * @since
 */
public class Message implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    /** 消息状态 - 1.未读 */
    public static final byte STATUS_UNREAD = 1;
    
    /** 消息状态 - 2.已读 */
    public static final byte STATUS_READ = 2;
    
    // 消息id
    private long id;
    
    // 消息提供者id
    private long providerId;
    
    // 消息接收者id;
    private long receiverId;
    
    // 产生消息的应用id
    private String appId;
    
    // 消息类型
    private byte type;
    
    // 消息状态
    private byte status;
    
    // 消息产生时间
    private Date createdAt;
    
    // 消息过期时间
    private Date expiredAt;
    
    // 消息参数
    private String params;
    
    // 该消息对象
    private int tableSuffix;
    
    public Message()
    {
        
    }
    
    public Message(MessagePublishRequest request)
    {
        this.setId(request.getId());
        this.setProviderId(request.getProviderId());
        this.setType(MessageType.getValue(request.getType()));
        this.setCreatedAt(new Date(request.getCreatedAt()));
        this.setExpiredAt(new Date(request.getExpiredAt()));
        this.setParams(JsonUtils.toJson((request.getParams())));
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public Message(String appId, long providerId, long receiverId, byte type, byte status, int expiredDays, String params)
    {
        this.setAppId(appId);
        this.setProviderId(providerId);
        this.setReceiverId(receiverId);
        this.setType(type);
        this.setStatus(status);
        Date createdAt = new Date();
        Date expiredAt = DateUtils.getDateAfter(createdAt, expiredDays);
        this.setCreatedAt(createdAt);
        this.setExpiredAt(expiredAt);
        this.setParams(params);
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
    
    public String getParams()
    {
        return params;
    }
    
    public long getProviderId()
    {
        return providerId;
    }
    
    public long getReceiverId()
    {
        return receiverId;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public byte getType()
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
    
    public void setParams(String params)
    {
        this.params = params;
    }
    
    public void setProviderId(long providerId)
    {
        this.providerId = providerId;
    }
    
    public void setReceiverId(long receiverId)
    {
        this.receiverId = receiverId;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public MessageResponse toMessageResponse()
    {
        MessageResponse response = new MessageResponse();
        response.setId(this.getId());
        response.setProviderId(this.getProviderId());
        response.setReceiverId(this.getReceiverId());
        response.setAppId(this.getAppId());
        response.setType(MessageType.getType(this.getType()));
        response.setStatus(MessageStatus.getStatus(this.getStatus()));
        response.setCreatedAt(this.getCreatedAt());
        response.setExpiredAt(this.getExpiredAt());
        
        Map<String, Object> params = null;
        String paramStr = this.getParams();
        
        // 接口变更, providerUsername和providerName从消息参数移出
        if (StringUtils.isNotBlank(paramStr))
        {
            params = JsonUtils.stringToMap(paramStr);
            if (params != null)
            {
                if (params.get(MessageParamName.PROVIDER_USERNAME) != null)
                {
                    response.setProviderUsername(String.valueOf(params.get(MessageParamName.PROVIDER_USERNAME)));
                    params.remove(MessageParamName.PROVIDER_USERNAME);
                }
                if (params.get(MessageParamName.PROVIDER_NAME) != null)
                {
                    response.setProviderName(String.valueOf(params.get(MessageParamName.PROVIDER_NAME)));
                    params.remove(MessageParamName.PROVIDER_NAME);
                }
                
                // java.lang.Integer cannot be cast to java.lang.Byte
                if (params.get(MessageParamName.NODE_TYPE) != null)
                {
                    Byte nodeType = Byte.parseByte(String.valueOf(params.get(MessageParamName.NODE_TYPE)));
                    params.put(MessageParamName.NODE_TYPE, NodeType.getType(nodeType));
                }
                response.setParams(params);
            }

        }
        return response;
    }
    
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
