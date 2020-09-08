package com.huawei.sharedrive.isystem.util.test;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.isystem.util.DateTimeUtils;

public class DateTimeUtilsTest
{
    @Test
    public void formatTest()
    {
        String format = DateTimeUtils.format(1100000000000L);
        System.out.println(format);
        Assert.assertEquals("2004-11-09 19:33:20", format);
    }
    
    @Test
    public void formatTest2()
    {
        String format = DateTimeUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        System.out.println(format);
    }
    
    @Test
    public void formatTest3()
    {
        String format = DateTimeUtils.format(null, "yyyy-MM-dd HH:mm:ss");
        System.out.println(format);
    }
}
