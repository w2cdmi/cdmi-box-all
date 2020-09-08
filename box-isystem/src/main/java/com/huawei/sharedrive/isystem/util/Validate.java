package com.huawei.sharedrive.isystem.util;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public final class Validate
{
    public final static String REG_IPV4 = "^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$";
    
    public final static String REG_MAK = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])){3}$";
    
    public final static String REG_DOMAIN = "\\b([a-z0-9]+(-[a-z0-9]+)*\\.)+[a-z]{2,}\\b";
    
    public final static String LOGIN_NAME = "^[a-zA-Z]{1}[a-zA-Z0-9]+$";
    
    private Validate()
    {
        
    }
    
    public static boolean validateFull(String value, String regx)
    {
        Pattern pat = Pattern.compile(regx);
        Matcher matcher = pat.matcher(value);
        return matcher.matches();
    }
    
    public static boolean valiDateUserName(String value)
    {
        if (StringUtils.isBlank(value))
        {
            throw new InvalidParameterException("Name is blank");
        }
        int len = value.length();
        if (len < 2 || len > 60)
        {
            throw new InvalidParameterException("Name length exception");
        }
        return true;
    }
}
