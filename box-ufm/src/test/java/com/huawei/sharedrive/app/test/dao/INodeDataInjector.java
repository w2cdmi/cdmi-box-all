package com.huawei.sharedrive.app.test.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;


public class INodeDataInjector
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
        
        String sql = "insert inode_0(id, type, parentId, name, size, status, ownedBy, createdBy, modifiedBy,shareStatus, syncStatus, syncVersion, sha1,resourceGroupId)"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setInt(1, random.nextInt(10000000) + 1);
         ps.setInt(2, random.nextInt(2));
         ps.setInt(3, 0);
         ps.setString(4,  "name3");
         ps.setInt(5, 2545);
         ps.setInt(6, 1);
         int ownedBy = random.nextInt(500) + 1;
         ps.setInt(7, ownedBy);
         ps.setInt(8, ownedBy);
         ps.setInt(9, ownedBy);
         ps.setInt(10, 1);
         ps.setInt(11, 1);
         ps.setInt(12, 1);
         ps.setString(13, "asdfsdfsdfdfd");
         int resourceGroup = random.nextInt(6) + 1;
         ps.setInt(14, resourceGroup);
         ps.executeUpdate();
         
    }



    /**
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static Connection getConnection() throws ClassNotFoundException, SQLException
    {
        String url="jdbc:mysql://10.183.15.132:3306/userdb_0?user=root&password=123456";
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url);
        return conn;
    }
    
}
