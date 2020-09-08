package pw.cdmi.box.uam.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FormValidateUtil
{
    public final static String REG_IPV4 = "^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$";
    
    public final static String REG_MAK = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])){3}$";
    
    private final static String LOGIN_NAME_RULE = "^[a-zA-Z]{1}[a-zA-Z0-9]+$";
    
    private final static String PLUGIN_KEY_RULE = "^[a-zA-Z0-9]+$";
    
    private final static String EMAIL_RULE = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    
    private FormValidateUtil()
    {
    }
    
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
        Pattern pattern = Pattern.compile(LOGIN_NAME_RULE);
        Matcher matcher = pattern.matcher(appName);
        return matcher.matches();
    }
    
    public static boolean isValidPluginKey(String key)
    {
        if (key == null)
        {
            return false;
        }
        key = key.trim();
        if (key.length() > 60)
        {
            return false;
        }
        Pattern pattern = Pattern.compile(PLUGIN_KEY_RULE);
        Matcher matcher = pattern.matcher(key);
        return matcher.matches();
    }
    
    public static boolean isValidIPv4(String ipStr)
    {
        ipStr = ipStr.trim();
        Pattern pattern = Pattern.compile(REG_IPV4);
        Matcher matcher = pattern.matcher(ipStr);
        return matcher.matches();
    }
    
}
