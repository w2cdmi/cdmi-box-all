package com.huawei.sharedrive.isystem.util.signature.test;

import org.junit.Test;

import junit.framework.Assert;
import pw.cdmi.common.util.signature.HSCloudKey;

public class HSCloudKeyTest
{
    @Test
    public void bytes2HexTest()
    {
        String bytes2Hex = HSCloudKey.bytes2Hex("huawei@123".getBytes());
        System.out.println(bytes2Hex);
        Assert.assertEquals("68756177656940313233", bytes2Hex);
    }
    
    @Test
    public void bytes2HexTest1()
    {
        String bytes2Hex = HSCloudKey.bytes2Hex("".getBytes());
        System.out.println(bytes2Hex);
        Assert.assertEquals("", bytes2Hex);
    }
    
    @Test
    public void bytes2HexTest2()
    {
        try
        {
            String bytes2Hex = HSCloudKey.bytes2Hex(null);
            System.out.println(bytes2Hex);
        }
        catch (Exception e)
        {
            
        }
    }
    
    @Test
    public void sha256EncodedTest()
    {
        String sha256Encoded = HSCloudKey.sha256Encoded("huawei@123".getBytes());
        System.out.println(sha256Encoded);
        Assert.assertEquals("771c639ad9ec9acf645aafd8fc35896873f976eeaf872b1cd10fdd1a59686f0c", sha256Encoded);
    }
    
    @Test
    public void sha256EncodedTest1()
    {
        String sha256Encoded = HSCloudKey.sha256Encoded("".getBytes());
        System.out.println(sha256Encoded);
        Assert.assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", sha256Encoded);
    }
    
    @Test
    public void sha256EncodedTest2()
    {
        String sha256Encoded = HSCloudKey.sha256Encoded(null);
        System.out.println(sha256Encoded);
        Assert.assertEquals("", sha256Encoded);
    }
    
    @Test
    public void signTest()
    {
        HSCloudKey hsCloudKey = new HSCloudKey();
        String sign = hsCloudKey.sign("huawei@123");
        System.out.println(sign);
        Assert.assertEquals("NzcxYzYzOWFkOWVjOWFjZjY0NWFhZmQ4ZmMzNTg5Njg3M2Y5NzZlZWFmODcyYjFjZDEwZmRkMWE1OTY4NmYwYw==",
            sign);
    }
    
    @Test
    public void signTest1()
    {
        HSCloudKey hsCloudKey = new HSCloudKey();
        String sign = hsCloudKey.sign("");
        System.out.println(sign);
        Assert.assertEquals("ZTNiMGM0NDI5OGZjMWMxNDlhZmJmNGM4OTk2ZmI5MjQyN2FlNDFlNDY0OWI5MzRjYTQ5NTk5MWI3ODUyYjg1NQ==",
            sign);
    }
    
    @Test
    public void signTest2()
    {
        try
        {
            HSCloudKey hsCloudKey = new HSCloudKey();
            String sign = hsCloudKey.sign(null);
            System.out.println(sign);
        }
        catch (Exception e)
        {
            
        }
    }
}
