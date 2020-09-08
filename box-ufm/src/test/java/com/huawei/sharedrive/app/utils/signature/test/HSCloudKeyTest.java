package com.huawei.sharedrive.app.utils.signature.test;

import junit.framework.Assert;

import org.junit.Test;
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
    public void sha256EncodedTest() throws Exception
    {
        String sha256Encoded = HSCloudKey.sha256Encoded("huawei@123".getBytes());
        System.out.println(sha256Encoded);
        Assert.assertEquals("771c639ad9ec9acf645aafd8fc35896873f976eeaf872b1cd10fdd1a59686f0c", sha256Encoded);
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
    
}
