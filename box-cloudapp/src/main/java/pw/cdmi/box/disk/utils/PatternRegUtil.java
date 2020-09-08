package pw.cdmi.box.disk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.exception.BusinessException;

public final class PatternRegUtil
{
    private PatternRegUtil()
    {
        
    }
    
    private final static String SELF_RULE1 = System.getProperty("line.separator");
    
    private final static String SELF_RULE2 = "\t\f;,；，";
    
    private final static String EMAIL_RULE = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    
    private final static int EMAIL_RULE_MAX_LENGTH = 255;
    
    private final static int EMAIL_RULE_MIN_LENGTH = 5;
    
    private final static int PHONE_RULE_LENGTH = 11;
    
    private final static int LENGTH_OF_COMPLEX_LINK_ACCESSCODE = 8;
    
    private final static String LINK_ACCESSCODE_PATTERN = "^[a-z0-9A-Z!@#$^&*\\-+.]{1,8}$";
    
    private final static String LINK_ACCESSCODE_WITH_CHAR = "[a-zA-Z]+";
    
    private final static String LINK_ACCESSCODE_WITH_NUMBER = "[0-9]+";
    
    private final static String LINK_ACCESSCODE_WITH_SCHAR = "[!@#$^&*\\-+.]+";
    
    private final static int MAX_LENGTH_OF_SIMPLE_LINK_ACCESSCODE = 20;
    
    private final static Pattern PATTERN_ACCESSCODE = Pattern.compile(LINK_ACCESSCODE_PATTERN);
    
    private final static Pattern PATTERN_CHAR = Pattern.compile(LINK_ACCESSCODE_WITH_CHAR);
    
    private final static Pattern PATTERN_NUMBER = Pattern.compile(LINK_ACCESSCODE_WITH_NUMBER);
    
    private final static Pattern PATTERN_SCHAR = Pattern.compile(LINK_ACCESSCODE_WITH_SCHAR);
    
    private final static Pattern PATTERN_SIMPLE_ACCESSCODE = Pattern.compile("^[a-zA-Z0-9]+$");
    
    public static void checkComplexLinkAccessCodeLegal(String accessCode)
    {
        Matcher matcher = PATTERN_ACCESSCODE.matcher(accessCode);
        if (!matcher.find())
        {
            throw new IllegalArgumentException("inlegal accessCode rule");
        }
        if (accessCode.length() != LENGTH_OF_COMPLEX_LINK_ACCESSCODE)
        {
            throw new IllegalArgumentException("inlegal accessCode rule");
        }
        matcher = PATTERN_NUMBER.matcher(accessCode);
        if (!matcher.find())
        {
            throw new IllegalArgumentException("inlegal accessCode rule");
        }
        
        matcher = PATTERN_CHAR.matcher(accessCode);
        if (!matcher.find())
        {
            throw new IllegalArgumentException("inlegal accessCode rule");
        }
        
        matcher = PATTERN_SCHAR.matcher(accessCode);
        if (!matcher.find())
        {
            throw new IllegalArgumentException("inlegal accessCode rule");
        }
    }
    
    public static boolean checkMailLegal(String mail)
    {
        if (mail==null||mail.length() > PatternRegUtil.EMAIL_RULE_MAX_LENGTH
            || mail.length() < PatternRegUtil.EMAIL_RULE_MIN_LENGTH)
        {
        	return false;
        }
        try{
        	validateParameter(mail, PatternRegUtil.EMAIL_RULE, true);
        }catch (BusinessException e) {
			return false;
		}
        return true;
    }
    
    public static void checkPhoneLegal(String phone)
    {
        phone = phone.trim();
        if (phone.length() != PatternRegUtil.PHONE_RULE_LENGTH)
        {
            throw new BusinessException("inlegal phone rule");
        }
        validatePhoneParameter(phone);
    }
    
    public static boolean isPhoneLegal(String phone)
    {
        phone = phone.trim();
        if (phone.length() != PatternRegUtil.PHONE_RULE_LENGTH)
        {
            return false;
        }
        if (StringUtils.isBlank(phone))
        {
            return false;
        }
        for (int i = 0; i < phone.length(); i++)
        {
            if (!Character.isDigit(phone.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }
    
    public static void checkSimpleLinkAccessCodeLegal(String accessCode)
    {
        if (accessCode.length() > MAX_LENGTH_OF_SIMPLE_LINK_ACCESSCODE)
        {
            throw new IllegalArgumentException("Illegal accessCode length");
        }
        Matcher matcher = PATTERN_SIMPLE_ACCESSCODE.matcher(accessCode);
        if (!matcher.find())
        {
            throw new IllegalArgumentException("Illegal accessCode rule");
        }
    }
    
    public static boolean isParameterLegal(String str, String rule)
    {
        if (StringUtils.isNotBlank(str))
        {
            Pattern pattern = Pattern.compile(rule);
            Matcher matcher = pattern.matcher(str.trim());
            return matcher.matches();
        }
        return true;
        
    }
    
    public static boolean isParameterLegal(String str, String rule, boolean isMust)
    {
        if (isMust && StringUtils.isBlank(str))
        {
            return false;
        }
        if (StringUtils.isNotBlank(str))
        {
            Pattern pattern = Pattern.compile(rule);
            Matcher matcher = pattern.matcher(str);
            return matcher.matches();
        }
        return true;
        
    }
    
    public static void validateParameter(String str, String rule, boolean isMust)
    {
        if (!isParameterLegal(str, rule, isMust))
        {
            throw new BusinessException("invalid email rule");
        }
    }
    
    public static void validatePhoneParameter(String str)
    {
        if (StringUtils.isBlank(str))
        {
            throw new BusinessException("phone is null");
        }
        for (int i = 0; i < str.length(); i++)
        {
            if (!Character.isDigit(str.charAt(i)))
            {
                throw new BusinessException("invalid phone rule phone:" + str);
            }
        }
    }
    
    public static String getSelfRule()
    {
        return SELF_RULE1 + SELF_RULE2;
    }
}
