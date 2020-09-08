/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package pw.cdmi.box.uam.httpclient.domain;

public enum MessageStatusEnum
{
    ALL((byte) 0, "all"), UNREAD((byte) 1, "unread"), READ((byte) 2, "read");
    
    private byte code;
    
    private String desc;
    
    private MessageStatusEnum(byte code, String desc)
    {
        this.code = code;
        this.desc = desc;
    }
    
    public byte getCode()
    {
        return code;
    }
    
    public String getDesc()
    {
        return desc;
    }
    
    public static MessageStatusEnum parseByDesc(String desc)
    {
        for (MessageStatusEnum m : MessageStatusEnum.values())
        {
            if (m.getDesc().equals(desc))
            {
                return m;
            }
        }
        
        return null;
    }
    
    public static MessageStatusEnum parseByCode(byte code)
    {
        for (MessageStatusEnum m : MessageStatusEnum.values())
        {
            if (m.getCode() == code)
            {
                return m;
            }
        }
        
        return null;
    }
}
