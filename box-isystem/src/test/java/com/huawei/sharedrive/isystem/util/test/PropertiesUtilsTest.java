package com.huawei.sharedrive.isystem.util.test;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.huawei.sharedrive.isystem.util.PropertiesUtils;

public class PropertiesUtilsTest
{
    @Test
    public void getPropertyTest()
    {
        String property = PropertiesUtils.getProperty("jdbc.pool.maxWait", "5000");
        System.out.println(property);
        Assert.assertEquals("5000", property);
    }
    
    @Test
    public void getPropertyTest1()
    {
        try
        {
            PropertiesLoaderUtils.loadAllProperties("application.properties");
            String property = PropertiesUtils.getProperty("jdbc.pool.maxWait", "5000");
            System.out.println(property);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    @Test
    public void getPropertyTest2()
    {
        PropertiesUtils.getProperty("jdbc.pool.maxWait");
    }
    
    @Test
    public void getPropertyTest3()
    {
        try
        {
            PropertiesLoaderUtils.loadAllProperties("application.properties");
            PropertiesUtils.getProperty("jdbc.pool.maxWait");
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void getServiceUrlTest()
    {
        String url = PropertiesUtils.getServiceUrl();
        System.out.println(url);
    }
    
    @Test
    public void getServiceUrlTest1()
    {
        try
        {
            PropertiesLoaderUtils.loadAllProperties("application.properties");
            String url = PropertiesUtils.getServiceUrl();
            System.out.println(url);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
