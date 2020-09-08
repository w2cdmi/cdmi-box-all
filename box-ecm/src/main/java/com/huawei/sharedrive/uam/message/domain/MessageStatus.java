package com.huawei.sharedrive.uam.message.domain;

import java.io.Serializable;
import java.util.Date;

public class MessageStatus implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    public static final byte STATUS_UNREAD = 1;
    
    public static final byte STATUS_READ = 2;
    
    private long messageId;
    
    private long receiverId;
    
    private byte status = STATUS_UNREAD;
    
    private Date expiredAt;
    
    public long getMessageId()
    {
        return messageId;
    }
    
    public void setMessageId(long messageId)
    {
        this.messageId = messageId;
    }
    
    public long getReceiverId()
    {
        return receiverId;
    }
    
    public void setReceiverId(long receiverId)
    {
        this.receiverId = receiverId;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
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
}
