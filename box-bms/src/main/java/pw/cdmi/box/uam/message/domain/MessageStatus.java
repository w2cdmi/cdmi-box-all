package pw.cdmi.box.uam.message.domain;

import java.io.Serializable;
import java.util.Date;

public class MessageStatus implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    /** 消息状态 - 1.未读 */
    public static final byte STATUS_UNREAD = 1;
    
    /** 消息状态 - 2.已读 */
    public static final byte STATUS_READ = 2;
    
    // 消息id
    private long messageId;
    
    // 消息接收者id;
    private long receiverId;
    
    // 消息状态
    private byte status = STATUS_UNREAD;
    
    // 消息过期时间
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
        return expiredAt == null ? null : (Date) expiredAt.clone();
    }
    
    public void setExpiredAt(Date expiredAt)
    {
        this.expiredAt = (expiredAt == null ? null : (Date) expiredAt.clone());
    }
    
}
