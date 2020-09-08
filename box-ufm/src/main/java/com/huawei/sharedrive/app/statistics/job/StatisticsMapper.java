package com.huawei.sharedrive.app.statistics.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.dataserver.domain.Region;

import pw.cdmi.common.slavedb.manager.SlaveDatabaseManager;

public class StatisticsMapper
{
    
    private static Logger logger = LoggerFactory.getLogger(StatisticsMapper.class);
    
    private final static String SQL_SELECT_RESOURCEGROUP = "select id,regionId from resource_group";
    
    private final static String SQL_SELECT_USER = "select id,appId,accountId from [tableName]";
    
    private final static String SYSDB = "sysdb";
    
    private static final int TABLE_USER_NUMBER = 100;
    
    private Map<String, Integer> resourceGroupMap = new HashMap<String, Integer>(10);
    
    private SlaveDatabaseManager slaveDatabaseManager;
    
    private Map<String, String> userMap = new HashMap<String, String>(100);
    
    public StatisticsMapper(SlaveDatabaseManager slaveDatabaseManager)
    {
        this.slaveDatabaseManager = slaveDatabaseManager;
    }
    
    public String getAppId(long userId)
    {
        return userMap.get(String.valueOf(userId));
    }
    
    public int getRegionId(int resourceGroupId)
    {
        try
        {
            return resourceGroupMap.get(String.valueOf(resourceGroupId));
        }
        catch (Exception e)
        {
            return Region.UNKNOWN_REGION_ID;
        }
    }
    
    public void initUserAndAppMap() throws ClassNotFoundException, SQLException
    {
        clearUserAndAppMap();
        initResourceGroupSet();
        initUserSet();
    }
    
    private void clearUserAndAppMap()
    {
        synchronized (this)
        {
            this.userMap.clear();
            this.resourceGroupMap.clear();
        }
    }
    
    /**
     * @param conn
     */
    private void closeConnection(Connection conn)
    {
        if (null == conn)
        {
            return;
        }
        try
        {
            conn.close();
        }
        catch (Exception e)
        {
            logger.warn("Can not close connection", e);
        }
    }
    
    /**
     * @param ps
     */
    private void closePreparedStatment(PreparedStatement ps)
    {
        if (null == ps)
        {
            return;
        }
        try
        {
            ps.close();
        }
        catch (Exception e)
        {
            logger.warn("Can not close preparedStatement", e);
        }
    }
    
    private void closeResultSet(ResultSet rs)
    {
        if (null == rs)
        {
            return;
        }
        try
        {
            rs.close();
        }
        catch (Exception e)
        {
            logger.warn("Can not close preparedStatement", e);
        }
    }
    
    /**
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private Connection getConnection(String dbName) throws ClassNotFoundException, SQLException
    {
        Connection conn = slaveDatabaseManager.getConnection(dbName);
        return conn;
    }
    
    private void initResourceGroupSet() throws ClassNotFoundException, SQLException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try
        {
            conn = getConnection(SYSDB);
            ps = conn.prepareStatement(SQL_SELECT_RESOURCEGROUP);
            res = ps.executeQuery();
            while (res.next())
            {
                resourceGroupMap.put(String.valueOf(res.getInt("id")), res.getInt("regionId"));
            }
        }
        finally
        {
            this.closeResultSet(res);
            this.closePreparedStatment(ps);
            this.closeConnection(conn);
        }
    }
    
    private void initUserSet() throws ClassNotFoundException, SQLException
    {
        Connection conn = getConnection(SYSDB);
        PreparedStatement ps = null;
        ResultSet res = null;
        String sql = null;
        for (int i = 0; i < TABLE_USER_NUMBER; i++)
        {
            sql = SQL_SELECT_USER.replace("[tableName]", "user_" + i);
            try
            {
                ps = conn.prepareStatement(sql);
                res = ps.executeQuery();
                while (res.next())
                {
                    userMap.put(String.valueOf(res.getLong("id")), res.getString("appId"));
                }
            }
            finally
            {
                this.closePreparedStatment(ps);
            }
        }
        this.closeConnection(conn);
    }
    
}
