package com.huawei.sharedrive.isystem.util.test;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import junit.framework.Assert;
import pw.cdmi.core.utils.DigestUtil;

public class DigestUtilTest
{
    @Test
    public void digestPasswordTest()
    {
        String digestPassword = DigestUtil.digestPassword("huawei@123");
        System.out.println(digestPassword);
        Assert.assertEquals("771c639ad9ec9acf645aafd8fc35896873f976eeaf872b1cd10fdd1a59686f0c",
            digestPassword);
    }
    
    @Test
    public void digestTest()
    {
        String digestPassword;
        try
        {
            digestPassword = DigestUtil.digest("12321sadas!@#$%^&*(QWERTYUIO$RTGHN)".getBytes(), "");
            
            System.out.println(digestPassword);
        }
        catch (NoSuchAlgorithmException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
