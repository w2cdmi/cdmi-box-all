package com.huawei.sharedrive.isystem.util.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.isystem.util.FormValidateUtil;

public class FormValidateUtilTest
{
    @Test
    public void isNonNegativeIntegerTest()
    {
        boolean negativeInteger = FormValidateUtil.isNonNegativeInteger("11");
        System.out.println(negativeInteger);
        Assert.assertEquals(true, negativeInteger);
    }
    
    @Test
    public void isNonNegativeIntegerTest1()
    {
        boolean negativeInteger = FormValidateUtil.isNonNegativeInteger("");
        System.out.println(negativeInteger);
        Assert.assertEquals(false, negativeInteger);
    }
    
    @Test
    public void isNonNegativeIntegerTest2()
    {
        boolean negativeInteger = FormValidateUtil.isNonNegativeInteger(null);
        System.out.println(negativeInteger);
        Assert.assertEquals(false, negativeInteger);
    }
    
    @Test
    public void isBooleanTest()
    {
        boolean boolean1 = FormValidateUtil.isBoolean("true");
        Assert.assertEquals(true, boolean1);
    }
    
    @Test
    public void isBooleanTest1()
    {
        boolean boolean1 = FormValidateUtil.isBoolean("false");
        Assert.assertEquals(true, boolean1);
    }
    
    @Test
    public void isBooleanTest2()
    {
        boolean boolean1 = FormValidateUtil.isBoolean("1");
        Assert.assertEquals(false, boolean1);
    }
    
    @Test
    public void isBooleanTest3()
    {
        boolean boolean1 = FormValidateUtil.isBoolean("");
        Assert.assertEquals(false, boolean1);
    }
    
    @Test
    public void isBooleanTest4()
    {
        boolean boolean1 = FormValidateUtil.isBoolean(null);
        Assert.assertEquals(false, boolean1);
    }
    
    @Test
    public void isValidEmailTest0()
    {
        boolean validEmail = FormValidateUtil.isValidEmail("husfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawsfsdfawei@qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq.com");
        Assert.assertEquals(false, validEmail);
    }
    
    @Test
    public void isValidEmailTest()
    {
        boolean validEmail = FormValidateUtil.isValidEmail("huawei@qq.com");
        Assert.assertEquals(true, validEmail);
    }
    
    @Test
    public void isValidEmailTest1()
    {
        boolean validEmail = FormValidateUtil.isValidEmail("huaweiqq.com");
        Assert.assertEquals(false, validEmail);
    }
    
    @Test
    public void isValidEmailTest2()
    {
        boolean validEmail = FormValidateUtil.isValidEmail("");
        Assert.assertEquals(false, validEmail);
    }
    
    @Test
    public void isValidEmailTest3()
    {
        boolean validEmail = FormValidateUtil.isValidEmail(null);
        Assert.assertEquals(false, validEmail);
    }
    
    @Test
    public void isValidLoginNameTest()
    {
        boolean loginName = FormValidateUtil.isValidLoginName("loginName");
        Assert.assertEquals(true, loginName);
    }
    
    @Test
    public void isValidLoginNameTest1()
    {
        boolean loginName = FormValidateUtil.isValidLoginName("");
        Assert.assertEquals(false, loginName);
    }
    
    @Test
    public void isValidLoginNameTest2()
    {
        boolean loginName = FormValidateUtil.isValidLoginName(null);
        Assert.assertEquals(false, loginName);
    }
    
    @Test
    public void isValidLoginNameTest3()
    {
        boolean loginName = FormValidateUtil.isValidLoginName("tom");
        Assert.assertEquals(false, loginName);
    }
    @Test
    public void isValidLoginNameTest4()
    {
        boolean loginName = FormValidateUtil.isValidLoginName("tdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfom");
        Assert.assertEquals(false, loginName);
    }
    @Test
    public void isValidNameTest()
    {
        boolean validName = FormValidateUtil.isValidName("tom");
        Assert.assertEquals(true, validName);
    }
    
    @Test
    public void isValidNameTest1()
    {
        boolean validName = FormValidateUtil.isValidName("m");
        Assert.assertEquals(false, validName);
    }
    @Test
    public void isValidNameTest4()
    {
        boolean validName = FormValidateUtil.isValidName("tdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfom");
        Assert.assertEquals(false, validName);
    }
    @Test
    public void isValidNameTest2()
    {
        boolean validName = FormValidateUtil.isValidName("");
        Assert.assertEquals(false, validName);
    }
    
    @Test
    public void isValidNameTest3()
    {
        boolean validName = FormValidateUtil.isValidName(null);
        Assert.assertEquals(false, validName);
    }
    
    @Test
    public void isValidAppNameTest()
    {
        boolean appName = FormValidateUtil.isValidAppName("OneBox");
        Assert.assertEquals(true, appName);
    }
    @Test
    public void isValidAppNameTest0()
    {
        boolean appName = FormValidateUtil.isValidAppName("fomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfomtdfom");
        Assert.assertEquals(false, appName);
    }
    @Test
    public void isValidAppNameTest1()
    {
        boolean appName = FormValidateUtil.isValidAppName("ox");
        Assert.assertEquals(false, appName);
    }
    
    @Test
    public void isValidAppNameTest2()
    {
        boolean appName = FormValidateUtil.isValidAppName("");
        Assert.assertEquals(false, appName);
    }
    
    @Test
    public void isValidAppNameTest3()
    {
        boolean appName = FormValidateUtil.isValidAppName(null);
        Assert.assertEquals(false, appName);
    }
    
    @Test
    public void isValidAppNameTest4()
    {
        boolean appName = FormValidateUtil.isValidAppName("Onebox!@#$%^");
        Assert.assertEquals(false, appName);
    }
    
    @Test
    public void isValidIPv4Test()
    {
        boolean iPv4 = FormValidateUtil.isValidIPv4("10.169.45.211");
        Assert.assertEquals(true, iPv4);
    }
    
    @Test
    public void isValidIPv4Test1()
    {
        boolean iPv4 = FormValidateUtil.isValidIPv4("260.169.45.211");
        Assert.assertEquals(false, iPv4);
    }
    
    @Test
    public void isValidIPv4Test2()
    {
        boolean iPv4 = FormValidateUtil.isValidIPv4("");
        Assert.assertEquals(false, iPv4);
    }
    
    @Test
    public void isValidIPv4Test3()
    {
        try
        {
            boolean iPv4 = FormValidateUtil.isValidIPv4(null);
            Assert.assertEquals(false, iPv4);
        }
        catch (Exception e)
        {
            
        }
    }
    
    @Test
    public void isValidIPv4Test4()
    {
        boolean iPv4 = FormValidateUtil.isValidIPv4("260.sds.45.211");
        Assert.assertEquals(false, iPv4);
    }
    
    @Test
    public void isValidPortTest()
    {
        boolean validPort = FormValidateUtil.isValidPort(8080);
        Assert.assertEquals(true, validPort);
    }
    
    @Test
    public void isValidPortTest1()
    {
        boolean validPort = FormValidateUtil.isValidPort(65539);
        Assert.assertEquals(false, validPort);
    }
    
    @Test
    public void isValidPortTest2()
    {
        boolean validPort = FormValidateUtil.isValidPort(0);
        Assert.assertEquals(false, validPort);
    }
    @Test
    public void isTimeNotNullTest0()
    {
        boolean timeNotNull = FormValidateUtil.isTimeNotNull("11:12:00:00");
        Assert.assertEquals(false, timeNotNull);
    }
    @Test
    public void isTimeNotNullTest4()
    {
        boolean timeNotNull = FormValidateUtil.isTimeNotNull("12:00:09");
        Assert.assertEquals(false, timeNotNull);
    }
    @Test
    public void isTimeNotNullTest()
    {
        boolean timeNotNull = FormValidateUtil.isTimeNotNull("12:00:00");
        Assert.assertEquals(true, timeNotNull);
    }
    @Test
    public void isTimeNotNullTest5()
    {
        boolean timeNotNull = FormValidateUtil.isTimeNotNull("12:d0:00");
        Assert.assertEquals(false, timeNotNull);
    }
    @Test
    public void isTimeNotNullTest1()
    {
        boolean timeNotNull = FormValidateUtil.isTimeNotNull("");
        Assert.assertEquals(false, timeNotNull);
    }
    
    @Test
    public void isTimeNotNullTest2()
    {
        boolean timeNotNull = FormValidateUtil.isTimeNotNull(null);
        Assert.assertEquals(false, timeNotNull);
    }
    
    @Test
    public void isTimeNotNullTest3()
    {
        boolean timeNotNull = FormValidateUtil.isTimeNotNull("12:77:00");
        Assert.assertEquals(false, timeNotNull);
    }
}
