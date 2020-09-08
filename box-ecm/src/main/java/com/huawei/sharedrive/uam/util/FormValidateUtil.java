package com.huawei.sharedrive.uam.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FormValidateUtil
{
    private FormValidateUtil()
    {
        
    }
    
    private final static String LOGIN_NAME_RULE = "^[a-zA-Z]{1}[a-zA-Z0-9]+$";
    
    private final static String EMAIL_RULE = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    
    public static boolean isValidEmail(String email)
    {
        if (email == null)
        {
            return false;
        }
        if (email.trim().length() > 255)
        {
            return false;
        }
        Pattern p = Pattern.compile(EMAIL_RULE);
        Matcher m = p.matcher(email.trim());
        return m.matches();
    }
    
    public static boolean isValidLoginName(String loginName)
    {
        if (loginName == null)
        {
            return false;
        }
        loginName = loginName.trim();
        if (loginName.length() < 4 || loginName.length() > 60)
        {
            return false;
        }
        Pattern pattern = Pattern.compile(LOGIN_NAME_RULE);
        Matcher matcher = pattern.matcher(loginName);
        return matcher.matches();
    }
    
}
