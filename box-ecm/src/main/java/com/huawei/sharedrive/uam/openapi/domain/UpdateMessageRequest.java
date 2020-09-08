package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;

public class UpdateMessageRequest implements Serializable
{
    
    private static final long serialVersionUID = 6516811537929379261L;
    
    private String status;
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public byte getStatusCode()
    {
        MessageStatusEnum statusEnum = MessageStatusEnum.parseByDesc(status);
        if (null == statusEnum || statusEnum == MessageStatusEnum.ALL)
        {
            throw new InvalidParamterException("Invalid status " + status);
        }
        
        return statusEnum.getCode();
    }
    
    public void checkParameters()
    {
        MessageStatusEnum statusEnum = MessageStatusEnum.parseByDesc(status);
        if (null == statusEnum || statusEnum == MessageStatusEnum.ALL)
        {
            throw new InvalidParamterException("Invalid status " + status);
        }
    }
    
}
