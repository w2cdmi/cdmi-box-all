package com.huawei.sharedrive.app.utils.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.PatternRegUtil;
import com.huawei.sharedrive.app.utils.ShareLinkExceptionUtil;

public class PatternRegUtilTest
{
    /** 邮箱地址 **/
    private static final String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
    
    private static final String DOMAIN = '(' + ATOM + "+(\\." + ATOM + "+)*";
    
    private static final String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";
    
    private final static String EMAIL_RULE = '^' + ATOM + "+(\\." + ATOM + "+)*@" + DOMAIN + '|' + IP_DOMAIN
        + ")$";
    
    @Test
    public void checkLinkAccessCodeLegalTest()
    {
        try
        {
            PatternRegUtil.checkLinkAccessCodeLegal("e04e81588ad5");
            PatternRegUtil.checkLinkAccessCodeLegal("");
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("IllegalArgumentException", className);
        }
    }
    @Test
    public void checkLinkAccessCodeLegalTest2()
    {
        try
        {
            PatternRegUtil.checkLinkAccessCodeLegal("e04e81588ad5");
            PatternRegUtil.checkLinkAccessCodeLegal("qazwsrfvtgqazwsrfvtgqazwsrfvtgqazwsrfvtg");
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("IllegalArgumentException", className);
        }
    }
    @Test
    public void checkMailLegalTest0()
    {
        PatternRegUtil.checkMailLegal("testuser@huawei.com");
    }
    @Test
    public void checkMailLegalTest()
    {
        try
        {
            PatternRegUtil.checkMailLegal("testasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasusertestasuser@huawei.com");
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkMailLegalTest1()
    {
        try
        {
            PatternRegUtil.checkMailLegal("a@m");
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkSimpleLinkAccessCodeLegalTest()
    {
        PatternRegUtil.checkSimpleLinkAccessCodeLegal("e04e81588ad5");
    }
    
    @Test
    public void checkSimpleLinkAccessCodeLegalTest1()
    {
        try
        {
            PatternRegUtil.checkSimpleLinkAccessCodeLegal("e04e81588ad5e04e81588ad5e04e81588ad5");
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("IllegalArgumentException", className);
        }
    }
    
    @Test
    public void isParameterLegalTest()
    {
        boolean legal = PatternRegUtil.isParameterLegal("usertest@huawei.com", EMAIL_RULE);
        Assert.assertEquals(true, legal);
    }
    @Test
    public void isParameterLegalTest1()
    {
        PatternRegUtil.isParameterLegal(null, EMAIL_RULE);
        boolean legal = PatternRegUtil.isParameterLegal("", EMAIL_RULE);
        Assert.assertEquals(true, legal);
    }
    @Test
    public void isParameterLegalTest2()
    {
        boolean legal = PatternRegUtil.isParameterLegal("usertest@huawei.com", EMAIL_RULE, true);
        Assert.assertEquals(true, legal);
    }
    
    @Test
    public void isParameterLegalTest3()
    {
        boolean legal = PatternRegUtil.isParameterLegal(null, EMAIL_RULE, true);
        Assert.assertEquals(false, legal);
    }
    @Test
    public void isParameterLegalTest4()
    {
        boolean legal = PatternRegUtil.isParameterLegal("usertest@huawei.com", EMAIL_RULE, false);
        Assert.assertEquals(true, legal);
    }
    
    @Test
    public void validateParameterTest()
    {
        try
        {
            PatternRegUtil.validateParameter("usertesthuawei.com", EMAIL_RULE, true);
            PatternRegUtil.checkLinkAccessCodeLegal("qazwsrfvtgqazwsrfvtgqazwsrfvtgqazwsrfvtg");
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    @Test
    public void validateParameterTest1()
    {
        try
        {
            PatternRegUtil.validateParameter("usertesthuawei.com", EMAIL_RULE, false);
            PatternRegUtil.checkLinkAccessCodeLegal("qazwsrfvtgqazwsrfvtgqazwsrfvtgqazwsrfvtg");
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    @Test
    public void validateParameterTest2()
    {
        try
        {
            PatternRegUtil.validateParameter(null, EMAIL_RULE, true);
            PatternRegUtil.checkLinkAccessCodeLegal("qazwsrfvtgqazwsrfvtgqazwsrfvtgqazwsrfvtg");
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
}
