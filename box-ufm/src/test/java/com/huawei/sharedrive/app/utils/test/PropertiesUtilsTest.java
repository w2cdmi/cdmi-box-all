package com.huawei.sharedrive.app.utils.test;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.huawei.sharedrive.app.utils.PropertiesUtils;

public class PropertiesUtilsTest
{
    @Test
    public void getPropertyTest()
    {        
        String property = PropertiesUtils.getProperty("jdbc.logdb.pool.maxWait", "5000");
        System.out.println(property);
    }
    
    @Test
    public void getPropertyTest2()
    {
        PropertiesUtils.getProperty("jdbc.logdb.pool.maxWait");
    }
    
    @Test
    public void getPropertyTest3()
    {
        String property = PropertiesUtils.getProperty("jdbc.pool.maxWait", "5000");
        System.out.println(property);
        Assert.assertEquals("5000", property);
    }
    
    @Test
    public void getPropertyTest4()
    {
        try
        {
            PropertiesLoaderUtils.loadAllProperties("application.properties");
            String property = PropertiesUtils.getProperty("jdbc.pool.maxWait", "5000");
            System.out.println(property);
            PropertiesUtils.getProperty(null);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    @Test
    public void getPropertyTest5()
    {
        PropertiesUtils.getProperty("jdbc.pool.maxWait");
    }
    
    @Test
    public void getPropertyTest6()
    {
        try
        {
            PropertiesLoaderUtils.loadAllProperties("application.properties");
            PropertiesUtils.getProperty("jdbc.pool.maxWait");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
