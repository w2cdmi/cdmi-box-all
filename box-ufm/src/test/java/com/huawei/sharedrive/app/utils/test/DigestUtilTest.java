package com.huawei.sharedrive.app.utils.test;

import java.nio.charset.Charset;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.utils.ShareLinkExceptionUtil;
import pw.cdmi.core.utils.DigestUtil;

public class DigestUtilTest
{
    @Test
    public void digestPasswordTest() throws Exception
    {
        String digestPassword = DigestUtil.digestPassword("huawei@123");
        System.out.println(digestPassword);
        Assert.assertEquals("771c639ad9ec9acf645aafd8fc35896873f976eeaf872b1cd10fdd1a59686f0c",
            digestPassword);
    }
    
    @Test
    public void digestPasswordTest2() throws Exception
    {
        String digestPassword = DigestUtil.digestPassword("");
        System.out.println(digestPassword);
        Assert.assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            digestPassword);
    }
    
    @Test
    public void digestPasswordTest3()
    {
        try
        {
            String digestPassword = DigestUtil.digestPassword(null);
            System.out.println(digestPassword);
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NullPointerException", className);
        }
    }
    
    @Test
    public void digestPasswordTest4() throws Exception
    {
        String digestPassword = DigestUtil.digestPassword("huawei@123");
        System.out.println(digestPassword);
        Assert.assertEquals("771c639ad9ec9acf645aafd8fc35896873f976eeaf872b1cd10fdd1a59686f0c",
            digestPassword);
    }
    
    @Test
    public void digestPasswordTest5() throws Exception
    {
        String digestPassword = DigestUtil.digestPassword("");
        System.out.println(digestPassword);
        Assert.assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            digestPassword);
    }
    
    @Test
    public void digestPasswordTest6()
    {
        try
        {
            String digestPassword = DigestUtil.digestPassword(null);
            System.out.println(digestPassword);
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NullPointerException", className);
        }
    }
    
    @Test
    public void digestTest() throws Exception
    {
        String digest = DigestUtil.digest("huawei@123".getBytes(Charset.defaultCharset()), "SHA-256");
        System.out.println(digest);
        Assert.assertEquals("771c639ad9ec9acf645aafd8fc35896873f976eeaf872b1cd10fdd1a59686f0c", digest);
    }
    
    @Test
    public void digestTest3()
    {
        try
        {
            String digest = DigestUtil.digest("huawei@123".getBytes(Charset.defaultCharset()), "");
            System.out.println(digest);
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NoSuchAlgorithmException", className);
        }
    }
    
    @Test
    public void digestTest5()
    {
        try
        {
            String digest = DigestUtil.digest("".getBytes(Charset.defaultCharset()), "SHA-256");
            System.out.println(digest);
            Assert.assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", digest);
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
        }
    }
    
    @Test
    public void digestTest2() throws Exception
    {
        String digest = DigestUtil.digest("huawei@123".getBytes(Charset.defaultCharset()),
            "SHA-256");
        System.out.println(digest);
        Assert.assertEquals("771c639ad9ec9acf645aafd8fc35896873f976eeaf872b1cd10fdd1a59686f0c", digest);
    }
    
    @Test
    public void digestTest4()
    {
        try
        {
            String digest = DigestUtil.digest("huawei@123".getBytes(Charset.defaultCharset()),
                "");
            System.out.println(digest);
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NoSuchAlgorithmException", className);
        }
    }
    
    @Test
    public void digestTest6()
    {
        try
        {
            String digest = DigestUtil.digest("".getBytes(Charset.defaultCharset()),
                "SHA-256");
            System.out.println(digest);
            Assert.assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", digest);
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
        }
    }
}
