package com.huawei.sharedrive.uam.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.exception.BusinessException;

public final class PatternRegUtil
{
    private PatternRegUtil()
    {
        
    }
    
    private final static String EMAIL_RULE = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    
    final static String[] LDAP_ERROR_LOGINNAME = new String[]{"[", "]", ":", ";", "|", "=", ",", "+", "*",
        "?", "<", ">", "@", "\"", "/", "\\"};
    
    private static final String DOMAIN_REGEX = "[^/\\\\]{1,}";
    
    private final static int EMAIL_RULE_MAX_LENGTH = 127;
    
    private final static int EMAIL_RULE_MIN_LENGTH = 5;
    
    private final static int MAX_LENGTH_OF_SIMPLE_LINK_ACCESSCODE = 20;
    
    /**
     * 
     * @param accessCode
     */
    public static void checkLinkAccessCodeLegal(String accessCode)
    {
        String code = accessCode.trim();
        if (StringUtils.isBlank(code) || code.length() > MAX_LENGTH_OF_SIMPLE_LINK_ACCESSCODE)
        {
            throw new IllegalArgumentException("inlegal accessCode rule");
        }
    }
    
    /**
     * 
     * @param mail
     */
    public static void checkMailLegal(String mail)
    {
        if (mail.length() > PatternRegUtil.EMAIL_RULE_MAX_LENGTH
            || mail.length() < PatternRegUtil.EMAIL_RULE_MIN_LENGTH)
        {
            throw new BusinessException("inlegal mail rule");
        }
        validateParameter(mail, PatternRegUtil.EMAIL_RULE, true);
    }
    
    /**
     * 
     * @param mail
     */
    public static boolean checkLdapLoginName(String loginName)
    {
        boolean isLdapLoginName = true;
        if (StringUtils.isNotBlank(loginName))
        {
            for (int i = 0; i < LDAP_ERROR_LOGINNAME.length; i++)
            {
                if (loginName.contains(LDAP_ERROR_LOGINNAME[i]))
                {
                    isLdapLoginName = false;
                    break;
                }
            }
        }
        return isLdapLoginName;
    }
    
    /**
     * 
     * @param accessCode
     */
    public static void checkSimpleLinkAccessCodeLegal(String accessCode)
    {
        if (accessCode.length() > MAX_LENGTH_OF_SIMPLE_LINK_ACCESSCODE)
        {
            throw new IllegalArgumentException("inlegal accessCode rule");
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
        Pattern pattern = Pattern.compile(rule);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    public static boolean checkDomainRegex(String domain)
    {
        if (domain.length() > 64)
        {
            return false;
        }
        Pattern p = Pattern.compile(DOMAIN_REGEX);
        Matcher m = p.matcher(domain);
        return m.matches();
    }
    
    /**
     * 
     * @param str
     * @param rule
     * @param isMust
     */
    public static void validateParameter(String str, String rule, boolean isMust)
    {
        if (!isParameterLegal(str, rule, isMust))
        {
            throw new BusinessException("invalid email rule");
        }
    }
    
}
