package com.huawei.sharedrive.isystem.util.test;

import java.sql.SQLException;

import org.junit.Test;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.huawei.sharedrive.isystem.util.ConnectionWrapper;

public class ConnectionWrapperTest
{
    @Test
    public void execSQLTest()
    {
        ConnectionWrapper c = null;
        try
        {
            c = new ConnectionWrapper("127.0.0.1", 8080,
                PropertiesLoaderUtils.loadAllProperties("application.properties"), "uam", "127.0.0.1:3306");
            ConnectionWrapper.class.newInstance().execSQL(null,
                "select * from admin",
                2,
                null,
                2,
                2,
                false,
                "",
                null,
                true);
            c.execSQL(null, "select * from admin", 2, null, 2, 2, false, "", null, true);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            try
            {
                
                try
                {
                    ConnectionWrapper.class.newInstance().commit();
                    ConnectionWrapper.class.newInstance().close();
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (null != c)
                {
                    c.close();
                    c.close();
                }
            }
            catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
