package com.huawei.sharedrive.app.utils.test;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.huawei.sharedrive.app.utils.ShareLinkExceptionUtil;
import pw.cdmi.core.utils.BundleUtil;

public class BundleUtilTest
{
    @Test
    public void getTextTest()
    {
        Assert.assertEquals("keyName", BundleUtil.getText("keyName"));
    }
    
    @Test
    public void addBundleTest()
    {
        try
        {
            BundleUtil.addBundle(null, new Locale[]{Locale.ENGLISH});
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NullPointerException", className);
        }
    }
    
    @Test
    public void getTextTest1()
    {
        Assert.assertEquals("keyName", BundleUtil.getText("keyName"));
    }
    
    @Test
    public void addBundleTest1()
    {
        try
        {
            BundleUtil.addBundle("zh_cn", new Locale[]{new Locale("zh_CN")});
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void getTextTest2()
    {
        BundleUtil.getText(Locale.ENGLISH, "keyName");
    }
    
    @Test
    public void getTextTest3()
    {
        BundleUtil.getText(Locale.ENGLISH, "keyName", new String[]{"test"});
    }
    
    @Test
    public void getTextTest4()
    {
        BundleUtil.getText("bundleName", Locale.ENGLISH, "keyName");
    }
    
    @Test
    public void getTextTest5()
    {
        BundleUtil.getText("bundleName", Locale.ENGLISH, "keyName", new String[]{"test"});
    }
    
    @Test
    public void getTextTest6()
    {
        BundleUtil.getText("bundleName", "keyName");
    }
    
    @Test
    public void getTextTest7()
    {
        BundleUtil.getText("bundleName", "keyName", new String[]{"test"});
    }
    
    @Test
    public void getTextTest8()
    {
        BundleUtil.getText("keyName", new String[]{"test"});
    }
    
    @Test
    public void setDefaultBundleTest()
    {
        BundleUtil.setDefaultBundle("bundleName");
    }
    
    @Test
    public void setDefaultLocaleTest()
    {
        BundleUtil.setDefaultLocale(Locale.ENGLISH);
    }
    
}
