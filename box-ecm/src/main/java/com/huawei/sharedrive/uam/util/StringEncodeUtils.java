package com.huawei.sharedrive.uam.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.uam.exception.BadRequestException;
import com.huawei.sharedrive.uam.exception.BaseRunException;

public final class StringEncodeUtils
{
    private StringEncodeUtils()
    {
        
    }
    public static final int MAX_LENGTH_NODENAME = 255;
    
    public static final String SUSPENSION_POINTS = "...";
    
    private static Logger logger = LoggerFactory.getLogger(StringEncodeUtils.class);
    
    public static String decodeUft8Value(String name) throws BaseRunException
    {
        if (StringUtils.isBlank(name))
        {
            return name;
        }
        
        try
        {
            return URLDecoder.decode(name, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            String msg = "name is invaild,name:" + name;
            logger.error(msg, e);
            throw new BadRequestException(e);
        }
        
    }
    
    public static String decodeUft8ValueNoException(String name)
    {
        if (StringUtils.isBlank(name))
        {
            return name;
        }
        
        try
        {
            return URLDecoder.decode(name, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            String msg = "name is invaild,name:" + name;
            logger.error(msg, e);
            return name;
        }
        
    }
    
    public static String isoToUtf8Value(String name)
    {
        if (StringUtils.isBlank(name))
        {
            return name;
        }
        try
        {
            name = new String(name.getBytes("iso8859-1"), "utf8");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("decode name failed name:" + name);
            return name;
        }
        return name;
    }
    
    public static String encodeUft8Value(String name) throws BaseRunException
    {
        if (StringUtils.isBlank(name))
        {
            return name;
        }
        
        try
        {
            return URLEncoder.encode(name, "utf-8").replaceAll("\\+", "%20");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("name is invaild,name:" + name, e);
            throw new BadRequestException(e);
        }
        
    }
    
    public static String transferString(String source)
    {
        if (StringUtils.isBlank(source))
        {
            return "";
        }
        return source.replaceAll("\"", "\\\\\"").replaceAll("'", "\\\\'");
    }
    
    /**
     * 
     * @param source
     * @return
     */
    public static String transferStringForSql(String source)
    {
        if (StringUtils.isBlank(source))
        {
            return "";
        }
        
        return source.replaceAll("\\\\", "\\\\\\\\\\\\\\\\")
            .replaceAll("'", "\\\\'")
            .replaceAll("%", "\\\\%")
            .replaceAll("\"", "\\\\\"")
            .replaceAll("_", "\\\\_");
    }
    
}
