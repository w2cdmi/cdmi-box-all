package com.huawei.sharedrive.app.utils.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.utils.HtmlStringUtils;

public class HtmlStringUtilsTest
{
    @Test
    public void getParaStringTest()
    {
        String paraString = HtmlStringUtils.getParaString("getParaString");
        System.out.println(paraString);
        Assert.assertEquals("<p>getParaString", paraString);
    }
    
    @Test
    public void getParaStringTest2()
    {
        String paraString = HtmlStringUtils.getParaString(null);
        System.out.println(paraString);
        Assert.assertEquals("", paraString);
    }
}
