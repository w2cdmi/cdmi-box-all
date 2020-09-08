package pw.cdmi.box.uam.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pw.cdmi.box.uam.exception.BaseRunException;
import pw.cdmi.box.uam.exception.InvalidParamterException;

public final class ParamCommonUtils
{
    /** 非负整数正则表达式 */
    private static final Pattern PATTERN_NON_NEGATIVE_INTEGER = Pattern.compile("^\\d+$");
    
    private static final int MINVALUE = 1;
    
    private static final int MAXVALUE = 1000000;
    
    private static final int LIMITLESS = -1;
    
    private ParamCommonUtils()
    {
    
    }
    
    /**
     * 校验是否为非负整数
     * 
     * @param number
     * @throws BadRquestException
     */
    public static void checkNonNegativeDigital(Number... numbers)
    {
        String errorMsg = null;
        
        Matcher m = null;
        for (Number temp : numbers)
        {
            errorMsg = temp + " is not a non-negative digital";
            if (temp == null)
            {
                throw new InvalidParamterException(errorMsg);
            }
            
            m = PATTERN_NON_NEGATIVE_INTEGER.matcher(String.valueOf(temp));
            if (!m.matches())
            {
                throw new InvalidParamterException(errorMsg);
            }
        }
    }
    
    public static void checkMaxSpace(Long maxSpace)
    {
        if (maxSpace == null)
        {
            return;
        }
        if (maxSpace.intValue() != LIMITLESS && maxSpace.intValue() < MINVALUE)
        {
            throw new InvalidParamterException("maxSpace<" + MINVALUE);
        }
        if (maxSpace.longValue() > MAXVALUE)
        {
            throw new InvalidParamterException("maxSpace>" + MAXVALUE);
        }
    }
    
    public static void checkMaxTeamspace(Integer maxTeamspace)
    {
        if (maxTeamspace == null)
        {
            return;
        }
        if (maxTeamspace.intValue() != LIMITLESS && maxTeamspace.intValue() < MINVALUE)
        {
            throw new InvalidParamterException("maxTeamspace<" + MINVALUE);
        }
        if (maxTeamspace.intValue() > MAXVALUE)
        {
            throw new InvalidParamterException("maxTeamspace>" + MAXVALUE);
        }
    }
    
    /**
     * 
     */
    public static void checkMaxMember(Integer maxMember)
    {
        if (maxMember == null)
        {
            return;
        }
        if (maxMember.intValue() != LIMITLESS && maxMember.intValue() < MINVALUE)
        {
            throw new InvalidParamterException("maxMember<" + MINVALUE);
        }
        if (maxMember.intValue() > MAXVALUE)
        {
            throw new InvalidParamterException("maxMember>" + MAXVALUE);
        }
    }
    
    public static void checkModifyParam(Long accountId, Integer maxMember, Integer maxTeamspace,
        Long maxSpace) throws BaseRunException
    {
        checkNonNegativeDigital(accountId);
        checkMaxMember(maxMember);
        checkMaxSpace(maxSpace);
        checkMaxTeamspace(maxTeamspace);
    }
    
    public static void checkBindAccountParam(Integer maxMember, Integer maxTeamspace, Long maxSpace)
    {
        checkMaxMember(maxMember);
        checkMaxSpace(maxSpace);
        checkMaxTeamspace(maxTeamspace);
    }
}
