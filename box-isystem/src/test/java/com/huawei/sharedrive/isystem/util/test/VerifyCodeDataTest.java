package com.huawei.sharedrive.isystem.util.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.isystem.util.VerifyCodeData;

public class VerifyCodeDataTest
{
    @Test
    public void verifyCodeTest()
    {
        VerifyCodeData d = new VerifyCodeData();
        
        d.getbIsSetBackground();
        
        d.setbIsSetBackground("bIsSetInterferon");
        
        d.getbIsSetInterferon();
        
        d.setbIsSetInterferon("bIsSetInterferon");
        
        d.getDictionary();
        
        d.setDictionary("dictionary");
        
        d.getbVariableFont();
        
        d.setbVariableFont("bVariableFont");
        
        d.getbVariableFontSize();
        
        d.setbVariableFontSize("bVariableFontSize");
        
        d.getbIsRotate();
        
        d.setbIsRotate("isRotate");
        
        System.out.println(d);
    }
    
    @Test
    public void getbIsSetBackgroundTest()
    {
        VerifyCodeData d = new VerifyCodeData();
        d.setbIsSetBackground("test");
        Assert.assertEquals("test", d.getbIsSetBackground());
    }
    
    @Test
    public void getbIsSetInterferonTest()
    {
        VerifyCodeData d = new VerifyCodeData();
        d.setbIsSetInterferon("test");
        Assert.assertEquals("test", d.getbIsSetInterferon());
    }
    
    @Test
    public void getDictionaryTest()
    {
        VerifyCodeData d = new VerifyCodeData();
        d.setDictionary("test");
        Assert.assertEquals("test", d.getDictionary());
    }
    
    @Test
    public void getbVariableFontTest()
    {
        VerifyCodeData d = new VerifyCodeData();
        d.setbVariableFont("test");
        Assert.assertEquals("test", d.getbVariableFont());
    }
    
    @Test
    public void getbVariableFontSizeTest()
    {
        VerifyCodeData d = new VerifyCodeData();
        d.setbVariableFontSize("test");
        Assert.assertEquals("test", d.getbVariableFontSize());
    }
    
    @Test
    public void getbIsRotateTest()
    {
        VerifyCodeData d = new VerifyCodeData();
        d.setbIsRotate("test");
        Assert.assertEquals("test", d.getbIsRotate());
    }
}
