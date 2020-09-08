package com.huawei.sharedrive.app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;


/**
 * 规则校验工具类
 * 
 * @author l90005448
 * 
 */
public final class PatternRegUtil
{
    
    /** 邮箱地址 **/
    private static final String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
    
    private static final String DOMAIN = '(' + ATOM + "+(\\." + ATOM + "+)*";
    
    private static final String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

    private final static String EMAIL_RULE = '^' + ATOM + "+(\\." + ATOM + "+)*@" + DOMAIN + '|' + IP_DOMAIN
        + ")$";
    
    /** 用户邮箱地址，最大36位 **/
    private final static int EMAIL_RULE_MAX_LENGTH = 255;
    
    /** 用户邮箱地址，最小5位 **/
    private final static int EMAIL_RULE_MIN_LENGTH = 5;
    
    
    private final static int MAX_LENGTH_OF_SIMPLE_LINK_ACCESSCODE = 20;
    
    
    private PatternRegUtil()
    {
        
    }
    
    /**
     * 提取码校验，接口层不区分提取码规则，只限制最大长度为20
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
     * 校验普通用户邮箱是否合法
     * 
     * @param mail
     */
    public static void checkMailLegal(String mail) throws InvalidParamException
    {
        if (mail.length() > PatternRegUtil.EMAIL_RULE_MAX_LENGTH
            || mail.length() < PatternRegUtil.EMAIL_RULE_MIN_LENGTH)
        {
            throw new InvalidParamException("invalid mail rule");
        }
        validateParameter(mail, PatternRegUtil.EMAIL_RULE, true);
    }
    
    /**
     * 简单提取码校验
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
    
    /**
     * 判断参数是否符合校验规则
     * 
     * @param str 校验字符串
     * @param rule 校验规则
     * @return
     */
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
    
    /**
     * 判断参数是否合法
     * 
     * @param str 校验字符串
     * @param rule 校验规则
     * @param isMust 是否非空
     * @return
     */
    public static boolean isParameterLegal(String str, String rule, boolean isMust)
    {
        if (isMust && StringUtils.isBlank(str))
        {
            return false;
        }
        if (StringUtils.isNotBlank(str))
        {
            Pattern pattern = Pattern.compile(rule, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(str);
            return matcher.matches();
        }
        return true;
        
    }
    
    /**
     * 校验参数
     * 
     * @param str
     * @param rule
     * @param isMust
     */
    public static void validateParameter(String str, String rule, boolean isMust) throws InvalidParamException
    {
        if (!isParameterLegal(str, rule, isMust))
        {
            throw new InvalidParamException("invalid email rule");
        }
    }
    
}
