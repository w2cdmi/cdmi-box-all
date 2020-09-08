package pw.cdmi.box.disk.client.domain.message;

import pw.cdmi.core.exception.InvalidParamException;

/**
 * @version CloudStor CSE Service Platform Subproject, 2015-5-9
 * @see
 * @since
 */
public enum MessageStatus
{
    ALL("all", (byte) 0),
    
    UNREAD("unread", (byte) 1),
    
    READ("read", (byte) 2);
    
    private String status;
    
    private byte value;
    
    private MessageStatus(String status, byte value)
    {
        this.status = status;
        this.value = value;
    }
    
    public static MessageStatus getMessageStatus(byte value)
    {
        for (MessageStatus messageStatus : MessageStatus.values())
        {
            if (messageStatus.getValue() == value)
            {
                return messageStatus;
            }
        }
        return null;
    }
    
    public static MessageStatus getMessageStatus(String status)
    {
        for (MessageStatus messageStatus : MessageStatus.values())
        {
            if (messageStatus.getStatus().equals(status))
            {
                return messageStatus;
            }
        }
        return null;
    }
    
    public static byte getValue(String status)
    {
        for (MessageStatus messageStatus : MessageStatus.values())
        {
            if (messageStatus.getStatus().equals(status))
            {
                return messageStatus.getValue();
            }
        }
        throw new InvalidParamException("Invalid message status " + status);
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public byte getValue()
    {
        return value;
    }
    
}
