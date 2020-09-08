package com.huawei.sharedrive.uam.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pw.cdmi.common.slavedb.domain.ConnectionInfo;
import pw.cdmi.common.slavedb.manager.SlaveDatabaseManager;

public class SlaveDatabaseTest extends AbstractSpringTest
{
    
    @Autowired
    private SlaveDatabaseManager service;
    
    @Test
    public void testGetConnectionInfo()
    {
        ConnectionInfo info = service.getConnectionInfo("uamdb");
        System.out.println(info.getURL());
        System.out.println(info.getUsername());
        System.out.println(info.getPassword());
    }
    
    @Test
    public void testGetConnection() throws SQLException
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = service.getConnection("uamdb");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from db_addr");
            while (rs.next())
            {
                System.out.println(rs.getString(1));
            }
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException e)
                {
                    // ignore
                }
            }
            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (SQLException e)
                {
                    // ignore
                }
            }
            if (conn != null)
            {
                try
                {
                    conn.close();
                }
                catch (SQLException e)
                {
                    // ignore
                }
            }
        }
        System.exit(0);
    }
}
