package com.huawei.sharedrive.app.utils.test;

import junit.framework.Assert;

import org.junit.Test;
import pw.cdmi.core.utils.IpUtils;


public class IpUtilsTest
{
    @Test
    public void toLongTest()
    {
        long long1 = IpUtils.toLong("10.169.49.211");
        System.out.println(long1);
        Assert.assertEquals(178860499, long1);
    }
    
    @Test
    public void toLongTest2()
    {
        long long1 = IpUtils.toLong(null);
        System.out.println(long1);
        Assert.assertEquals(-1, long1);
    }
    
    @Test
    public void toLongTest3()
    {
        long long1 = IpUtils.toLong("10.449.49.211");
        System.out.println(long1);
        Assert.assertEquals(-1, long1);
    }
    
    @Test
    public void toLongTest4()
    {
        long long1 = IpUtils.toLong("huaweitest");
        System.out.println(long1);
        Assert.assertEquals(-1, long1);
    }
    
    @Test
    public void toLongTest1()
    {
        long long1 = IpUtils.toLong("110.10.169.49.211");
        System.out.println(long1);
    }
    
    @Test
    public void toLongTest5()
    {
        long long1 = IpUtils.toLong("1710.169.49.211");
        System.out.println(long1);
    }
    
    @Test
    public void toLongTest6()
    {
        long long1 = IpUtils.toLong("171sdfsdwe23423411");
        System.out.println(long1);
    }
    
    @Test
    public void toLongTest7()
    {
        try
        {
            long long1 = IpUtils.toLong("-9922222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222.169.49.211");
            System.out.println(long1);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void toLongTest8()
    {
        long long1 = IpUtils.toLong("10.169.49.211");
        System.out.println(long1);
    }
}
