package com.huawei.sharedrive.uam.core.dao.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.exception.InternalServerErrorException;

public final class HashTool
{
    
    private HashTool()
    {
    }
    
    /**
     * 
     * @param id
     * @return
     */
    public static long apply(String id)
    {
        byte[] digest = computeMd5(id);
        long hashCode = hashPrefix(digest);
        return hashCode;
    }
    
    /**
     * 
     * @param id
     * @return
     */
    public static long apply(long id)
    {
        byte[] digest = computeMd5(String.valueOf(id));
        long hashCode = hashPrefix(digest);
        return hashCode;
    }
    
    /**
     * 
     * @param id
     * @return
     */
    public static long applySuffux(String id)
    {
        byte[] digest = computeMd5(id);
        long hashCode = hashSuffix(digest);
        return hashCode;
    }
    
    private static byte[] computeMd5(String k)
    {
        if (StringUtils.isBlank(k))
        {
            throw new IllegalArgumentException("param k is not null");
        }
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new InternalServerErrorException("MD5 not supported", e);
        }
        byte[] keyBytes = null;
        try
        {
            keyBytes = k.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new InternalServerErrorException("Unknown string :" + k, e);
        }
        md5.update(keyBytes);
        return md5.digest();
    }
    
    private static long hashPrefix(byte[] digest)
    {
        if (digest == null)
        {
            return 0;
        }
        long rv = ((long) (digest[3] & 0xFF) << 24) | ((long) (digest[2] & 0xFF) << 16)
            | ((long) (digest[1] & 0xFF) << 8) | (digest[0] & 0xFF);
        
        return rv & 0xffffffffL;
    }
    
    private static long hashSuffix(byte[] digest)
    {
        if (digest == null)
        {
            return 0;
        }
        int length = digest.length;
        long rv = ((long) (digest[length - 4] & 0xFF) << 24) | ((long) (digest[length - 3] & 0xFF) << 16)
            | ((long) (digest[length - 2] & 0xFF) << 8) | (digest[length - 1] & 0xFF);
        
        return rv & 0xffffffffL;
    }
}
