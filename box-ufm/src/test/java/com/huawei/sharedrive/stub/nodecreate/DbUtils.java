package com.huawei.sharedrive.stub.nodecreate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DbUtils
{
    
    public static final String url = "jdbc:mysql://10.169.69.228:3306/userdb_0?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&socketTimeout=1800000";
    public static final String name = "com.mysql.jdbc.Driver";
    public static final String user = "root";
    public static final String password = "123456";  
    
    private static Connection conn = null;
    
    public static Connection getConnection()
    {
        if(null == conn)
        {
            synchronized (DbUtils.class)
            {
                if(null == conn)
                {
                    try
                    {
                        Class.forName(name);
                        conn = DriverManager.getConnection(url, user, password);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        conn = null;
                    }
                }
            }
        }
        return conn;
    }
    
    public static void closeConn()
    {
        if(null != conn)
        {
            try
            {
                conn.close();
            }
            catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    
}
