package com.huawei.sharedrive.app.utils.test;

import junit.framework.Assert;

import org.junit.Test;
import pw.cdmi.core.utils.FileSizeUtil;


public class FileSizeUtilTest
{
    @Test
    public void byteToMBStringTest()
    {
        String mbString = FileSizeUtil.byteToMBString(10240);
        System.out.println(mbString);
        Assert.assertEquals("0.01 MB", mbString);
    }
    
    @Test
    public void byteToMBStringTest2()
    {
        String format = FileSizeUtil.byteToMBString(0);
        System.out.println(format);
        Assert.assertEquals("", format);
    }
}
