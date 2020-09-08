package pw.cdmi.box.disk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FormValidateUtil
{
    private FormValidateUtil()
    {
        
    }
    
    private final static String LOGIN_NAME_RULE = "^[a-zA-Z]{1}[a-zA-Z0-9]+$";
    
    private final static String EMAIL_RULE = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    
    private final static String MOBILE_RULE = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
    
    public static boolean isValidEmail(String email)
    {
        if (email == null)
        {
            return false;
        }
        email = email.trim();
        if (email.length() > 127 || email.length() < 5)
        {
            return false;
        }
        Pattern p = Pattern.compile(EMAIL_RULE);
        Matcher m = p.matcher(email);
        return m.matches();
    }
    
    public static boolean isValidMobile(String mobile)
    {
        if (mobile == null)
        {
            return false;
        }
        mobile = mobile.trim();
        if (mobile.length() !=11)
        {
            return false;
        }
        Pattern p = Pattern.compile(MOBILE_RULE);
        Matcher m = p.matcher(mobile);
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
