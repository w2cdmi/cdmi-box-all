package com.huawei.sharedrive.isystem.util.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.isystem.util.PasswordGenerateUtil;

public class PasswordGenerateUtilTest
{
    @Test
    public void getRandomPasswordTest()
    {
        String password = PasswordGenerateUtil.getRandomPassword();
        System.out.println(password);
        Assert.assertEquals(true, password.length()>7 && password.length() <21);
    }
}
