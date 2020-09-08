package com.huawei.sharedrive.isystem.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 鐣岄潰琛ㄥ崟鏍￠獙宸ュ叿
 * 
 * 
 */
public final class FormValidateUtil
{
    
    private final static String LOGIN_NAME_RULE = "^[a-zA-Z]{1}[a-zA-Z0-9]+$";
    
    private final static String EMAIL_RULE = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    
    private final static String TIME_NUM_HOUR = "([0-1][0-9])|(2[0-3])";
    
    private final static String TIME_NUM_MM = "[0-5][0-9]";
    
    /** 闈炶礋鏁存暟姝ｅ垯琛ㄨ揪寮�*/
    private static final Pattern PATTERN_NON_NEGATIVE_INTEGER = Pattern.compile("^\\d+$");
    
    private FormValidateUtil()
    {
        
    }
    
    public static boolean isNonNegativeInteger(String num)
    {
        if (num == null)
        {
            return false;
        }
        num = Normalizer.normalize(num, Form.NFKC);
        Matcher m = PATTERN_NON_NEGATIVE_INTEGER.matcher(num);
        if (!m.matches())
        {
            return false;
        }
        return true;
    }
    
    public static boolean isBoolean(String input)
    {
        if (input == null)
        {
            return false;
        }
        String booleanStr = input.toLowerCase(Locale.ENGLISH);
        if ("true".equals(booleanStr) || "false".equals(booleanStr))
        {
            return true;
        }
        return false;
    }
    
    public static boolean isValidEmail(String email)
    {
        if (email == null)
        {
            return false;
        }
        email = email.trim();
        if (email.length() > 255)
        {
            return false;
        }
        email = Normalizer.normalize(email, Form.NFKC);
        Pattern p = Pattern.compile(EMAIL_RULE);
        Matcher m = p.matcher(email);
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
        loginName = Normalizer.normalize(loginName, Form.NFKC);
        Pattern pattern = Pattern.compile(LOGIN_NAME_RULE);
        Matcher matcher = pattern.matcher(loginName);
        return matcher.matches();
    }
    
    public static boolean isValidName(String name)
    {
        if (name == null)
        {
            return false;
        }
        name = name.trim();
        if (name.length() < 2 || name.length() > 60)
        {
            return false;
        }
        return true;
    }
    
    public static boolean isValidAppName(String appName)
    {
        if (appName == null)
        {
            return false;
        }
        appName = appName.trim();
        if (appName.length() < 4 || appName.length() > 20)
        {
            return false;
        }
        appName = Normalizer.normalize(appName, Form.NFKC);
        Pattern pattern = Pattern.compile(LOGIN_NAME_RULE);
        Matcher matcher = pattern.matcher(appName);
        return matcher.matches();
    }
    
    public static boolean isValidIPv4(String ipStr)
    {
        ipStr = ipStr.trim();
        ipStr = Normalizer.normalize(ipStr, Form.NFKC);
        Pattern pattern = Pattern.compile(Validate.REG_IPV4);
        Matcher matcher = pattern.matcher(ipStr);
        return matcher.matches();
    }
    
    public static boolean isValidPort(int port)
    {
        return port > 0 && port <= 65535;
    }
    
    public static boolean isTimeNotNull(String time)
    {
        if (StringUtils.isBlank(time))
        {
            return false;
        }
        String[] temp = time.split(":");
        if (temp.length != 3)
        {
            return false;
        }
        
        if (temp[0].length() != 2 || temp[1].length() != 2 || temp[2].length() != 2
            || !StringUtils.equals(temp[2], "00"))
        {
            return false;
        }
        temp[0] = Normalizer.normalize(temp[0], Form.NFKC);
        Pattern pat = Pattern.compile(TIME_NUM_HOUR);
        Matcher matcher = pat.matcher(temp[0]);
        if (!matcher.matches())
        {
            return false;
        }
        temp[1] = Normalizer.normalize(temp[1], Form.NFKC);
        pat = Pattern.compile(TIME_NUM_MM);
        matcher = pat.matcher(temp[1]);
        return matcher.matches();
    }
}
