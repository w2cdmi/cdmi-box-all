package com.huawei.sharedrive.app.utils.test;

import org.junit.Test;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import pw.cdmi.core.db.LoggedMySQLDriver;


public class LoggedMySQLDriverTest
{
    @Test
    public void connectTest()
    {
        try
        {
            LoggedMySQLDriver ld = new LoggedMySQLDriver();
            ld.connect("jdbc:mysql://localhost:3306/sysdb?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&socketTimeout=1800000",
                PropertiesLoaderUtils.loadAllProperties("application.properties"));
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void connectTest1()
    {
        try
        {
            LoggedMySQLDriver ld = new LoggedMySQLDriver();
            ld.connect("jdbc:mysql://localhost:3306/sysdb?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&socketTimeout=1800000",null);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
