package com.huawei.sharedrive.isystem.util.test;

import org.junit.Assert;
import org.junit.Test;

import com.huawei.sharedrive.isystem.util.PasswordValidateUtil;

public class PasswordValidateUtilTest
{
    @Test
    public void isValidPasswordTest()
    {
        boolean validPassword = PasswordValidateUtil.isValidPassword("huawei@123");
        Assert.assertEquals(true, validPassword);
        boolean inValidPassword = PasswordValidateUtil.isValidPassword("huawei123");
        Assert.assertEquals(false, inValidPassword);
        boolean inValidPassword1 = PasswordValidateUtil.isValidPassword("ei1");
        Assert.assertEquals(false, inValidPassword1);
        boolean inValidPassword2 = PasswordValidateUtil.isValidPassword("huawei123huhuawei123awehuawei123i123ei1");
        Assert.assertEquals(false, inValidPassword2);
    }
    @Test
    public void nullPasswordTest()
    {
        boolean validPassword = PasswordValidateUtil.isValidPassword("");
        Assert.assertEquals(false, validPassword);
    }
    @Test
    public void invalidPasswordTest()
    {
        boolean validPassword = PasswordValidateUtil.isValidPassword("huaweitest");
        Assert.assertEquals(false, validPassword);
        boolean validPassword1 = PasswordValidateUtil.isValidPassword("123456789");
        Assert.assertEquals(false, validPassword1);
        boolean validPassword3 = PasswordValidateUtil.isValidPassword("-!@#$^&*+.+");
        Assert.assertEquals(false, validPassword3);
    }
    @Test
    public void nullPasswordTest1()
    {
        boolean validPassword = PasswordValidateUtil.isValidPassword(null);
        Assert.assertEquals(false, validPassword);
    }
}
