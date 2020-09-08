package com.huawei.sharedrive.app.test.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;


public class TempUserNodeStatisticsDataInjector
{
    
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        Connection conn = getConnection();
        Random random =new Random();
        for(int i = 0; i < 10000; i++)
        {
            injectData(conn, random);
        }
        if(null != conn)
        {
            conn.close();
        }
        System.out.println("end");
    }
    
    
    
    public static void injectData(Connection conn, Random random) throws Exception
    {
        
        String sql = "insert into temp_node_user_statistics_day(day, appId, accountId, regionId, ownedBy, resourceGroupId, fileCount,trashFileCount,deletedFileCount,spaceUsed,trashSpaceUsed,deletedSpaceUsed)"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?)";
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setInt(1, 20150407);
         if(random.nextInt(2) == 0)
         {
             ps.setString(2, "OneBox");
         }
         else
         {
             ps.setString(2, "espace");
         }
         ps.setInt(3, 0);
         ps.setInt(4,  random.nextInt(5));
         ps.setInt(5, random.nextInt(50000000));
         ps.setInt(6, 1);
         ps.setInt(7, random.nextInt(10000));
         ps.setInt(8, random.nextInt(10000));
         ps.setInt(9, random.nextInt(10000));
         ps.setInt(10, random.nextInt(10000));
         ps.setInt(11, random.nextInt(10000));
         ps.setInt(12, random.nextInt(10000));
         ps.executeUpdate();
         
    }



    /**
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static Connection getConnection() throws ClassNotFoundException, SQLException
    {
        String url="jdbc:mysql://10.183.15.132:3306/sysdb?user=root&password=123456";
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url);
        return conn;
    }
    
}
