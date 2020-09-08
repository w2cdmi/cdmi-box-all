package com.huawei.sharedrive.app.openapi.domain.message;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.message.domain.MessageStatus;

/**
 * 更新消息状态请求对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-3-25
 * @see
 * @since
 */
public class UpdateMessageRequest implements Serializable
{
    
    private static final long serialVersionUID = 6516811537929379261L;
    
    private String status;
    
    public void checkParameters()
    {
        if (StringUtils.isBlank(status))
        {
            throw new InvalidParamException("Invalid status " + status);
        }
        MessageStatus messageStatus = MessageStatus.getMessageStatus(status);
        if(messageStatus == null)
        {
            throw new InvalidParamException("Invalid status " + status);
        }
        switch (messageStatus)
        {
            case READ:
            case UNREAD:
                break;
            default:
                throw new InvalidParamException("Invalid status " + status);
        }
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
}
