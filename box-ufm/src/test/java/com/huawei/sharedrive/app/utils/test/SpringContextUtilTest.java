package com.huawei.sharedrive.app.utils.test;

import java.util.Date;

import org.junit.Test;
import pw.cdmi.core.utils.SpringContextUtil;


public class SpringContextUtilTest
{
    @Test
    public void setApplicationContextTest()
    {
        SpringContextUtil util = new SpringContextUtil();
        util.setApplicationContext(null);
    }
    
    @Test
    public void getBeanTest()
    {
        try
        {
            SpringContextUtil.getBean("");
            SpringContextUtil.getBean("234234");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void getBeanTest1()
    {
        try
        {
            SpringContextUtil.getBean(Date.class);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
