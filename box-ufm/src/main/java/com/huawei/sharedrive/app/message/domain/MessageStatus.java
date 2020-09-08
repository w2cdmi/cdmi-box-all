package com.huawei.sharedrive.app.message.domain;

import com.huawei.sharedrive.app.exception.InvalidParamException;

/**
 * 消息状态枚举类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-5-9
 * @see
 * @since
 */
public enum MessageStatus
{
    /** 已读和未读 */
    ALL("all", (byte) 0),
    
    /** 未读 */
    UNREAD("unread", (byte) 1),
    
    /** 已读 */
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
    
    public static String getStatus(byte value)
    {
        for (MessageStatus messageStatus : MessageStatus.values())
        {
            if (messageStatus.getValue() == value)
            {
                return messageStatus.getStatus();
            }
        }
        throw new InvalidParamException("Invalid message status value: " + value);
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
        throw new InvalidParamException("Invalid message status: " + status);
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
