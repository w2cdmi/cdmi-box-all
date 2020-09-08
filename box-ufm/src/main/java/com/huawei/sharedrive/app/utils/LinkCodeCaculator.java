/*
 * Copyright Huawei Technologies Co.,Ltd. 2013-2014. All rights reserved.
 */
package com.huawei.sharedrive.app.utils;

import java.security.SecureRandom;

/**
 * 外链码计算工具
 * 
 * @author l90003768
 * 
 */
public final class LinkCodeCaculator
{
    /**
     * 模值
     */
    private static final int MOD = 62;
    
    private static final int LINKCODE_LEN = 8;
    
    private static final int ACCESSCODE_LEN = 6;
    
    private static String linkChars = "0123456789abcdefghijklmnopqrstuvwxyz";
    
    private static int linkCharsLen = linkChars.length();
    
    private static String accesscodeChars = "0123456789";
    
    private static int accesscodeCharsLen = accesscodeChars.length();
    
    private LinkCodeCaculator()
    {
        
    }
    
    /**
     * 生成外链动态提取码
     * 
     * @return
     */
    public static String buildAccessCode()
    {
        // 固定产生8位外链码，每位外链码从36个字母数字中随机获得
        StringBuffer linkId = new StringBuffer(ACCESSCODE_LEN);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < ACCESSCODE_LEN; i++)
        {
            linkId.append(accesscodeChars.charAt(random.nextInt(accesscodeCharsLen)));
        }
        return linkId.toString();
        
    }
    
    /**
     * 生成外链码
     * 
     * @return
     */
    public static String buildLinkID()
    {
        // 固定产生8位外链码，每位外链码从36个字母数字中随机获得
        StringBuffer linkId = new StringBuffer(LINKCODE_LEN);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < LINKCODE_LEN; i++)
        {
            linkId.append(linkChars.charAt(random.nextInt(linkCharsLen)));
        }
        return linkId.toString();
        
    }
    
    /**
     * 获取节点ID
     * 
     * @param linkCode
     * @return
     */
    public static long getINodeId(String linkCode)
    {
        String ownerId62 = linkCode.substring(4);
        long value = 0;
        int size = ownerId62.length();
        for (int i = 0; i < size; i++)
        {
            value = value * MOD + convertToDigital(ownerId62.charAt(i));
        }
        return value;
    }
    
    /**
     * 获取OwnerID
     * 
     * @param linkCode
     * @return
     */
    public static long getOwnerId(String linkCode)
    {
        String ownerId62 = linkCode.substring(0, 4);
        long value = 0;
        int length = ownerId62.length();
        for (int i = 0; i < length; i++)
        {
            value = value * MOD + convertToDigital(ownerId62.charAt(i));
        }
        return value;
    }
    
    /**
     * 将字符转换为数字
     * 
     * 0-'0'...10-'A'.....,35-'Z',36-'a'....61-'z'
     * 
     * @param data
     * @return
     */
    private static int convertToDigital(char ch)
    {
        byte by = (byte) ch;
        if (by < 58)
        {
            return by - 48;
        }
        if (by < 91)
        {
            return (char) (by - 55);
        }
        return by - 61;
    }
    
}
