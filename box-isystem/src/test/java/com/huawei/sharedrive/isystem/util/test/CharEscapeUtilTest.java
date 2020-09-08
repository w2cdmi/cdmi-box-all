package com.huawei.sharedrive.isystem.util.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.isystem.util.CharEscapeUtil;

public class CharEscapeUtilTest
{
    @Test
    public void transferStringTest()
    {
        String transferString = CharEscapeUtil.transferString("<script>alert('123');</script>");
        System.out.println(transferString);
    }
    @Test
    public void transferStringTest1()
    {
        String transferString = CharEscapeUtil.transferString("");
        System.out.println(transferString);
        Assert.assertEquals("", transferString);
    }
    @Test
    public void transferStringTest2()
    {
        String transferString = CharEscapeUtil.transferString(null);
        System.out.println(transferString);
        Assert.assertEquals("", transferString);
    }
}
