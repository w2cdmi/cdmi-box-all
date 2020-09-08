/*
 * Copyright Huawei Technologies Co.,Ltd. 2013-2014. All rights reserved.
 */
package com.huawei.sharedrive.app.share.domain;

/**
 * 外链提取码类型
 * 
 * 
 */
public final class LinkAccessCodeMode
{
    private LinkAccessCodeMode()
    {
    }
    
    
    public static final byte TYPE_STATIC_VALUE = 1;
    
    /** 邮箱动态提取 */
    public static final byte TYPE_MAIL_VALUE = 2;
    
    /** 手机动态提取 */
    public static final byte TYPE_PHONE_VALUE = 3;
    
    /** 固定提取 兼容老数据，固定提取码必须为1 */
    public static final String TYPE_STATIC_STRING = "static";
    
    /** 邮箱动态提取 */
    public static final String TYPE_MAIL_STRING = "mail";
    
    /** 手机动态提取 */
    public static final String TYPE_PHONE_STRING = "phone";
    
    public static byte transTypeToValue(String input)
    {
        if (TYPE_MAIL_STRING.equals(input))
        {
            return TYPE_MAIL_VALUE;
        }
        
        if (TYPE_PHONE_STRING.equals(input))
        {
            return TYPE_PHONE_VALUE;
        }
        
        return TYPE_STATIC_VALUE;
    }
    
    public static String transTypeToString(byte input)
    {
        if (TYPE_MAIL_VALUE == input)
        {
            return TYPE_MAIL_STRING;
        }
        
        if (TYPE_PHONE_VALUE == input)
        {
            return TYPE_PHONE_STRING;
        }
        
        return TYPE_STATIC_STRING;
    }
    
    public static boolean contains(String input)
    {
        if (TYPE_STATIC_STRING.equals(input))
        {
            return true;
        }
        
        if (TYPE_MAIL_STRING.equals(input))
        {
            return true;
        }
        
        if (TYPE_PHONE_STRING.equals(input))
        {
            return true;
        }
        return false;
    }
}
