package com.huawei.sharedrive.app.mirror.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.mirror.dao.UserMirrorStatisticInfoDAO;
import com.huawei.sharedrive.app.mirror.domain.UserMirrorStatisticInfo;
import com.huawei.sharedrive.app.mirror.service.UserMirrorStatisticInfoService;

import pw.cdmi.common.slavedb.manager.SlaveDatabaseManager;
import pw.cdmi.core.utils.MethodLogAble;

@Service("userMirrorStatisticInfo")
public class UserMirrorStatisticInfoServiceImpl implements UserMirrorStatisticInfoService
{
    @Autowired
    private UserMirrorStatisticInfoDAO userMirrorStatisticInfoDAO;
    
    @Autowired
    private SlaveDatabaseManager slaveDatabaseManager;
    
    private final static String SYSDB = "sysdb";
    
    private final static String SQL_SELECT_COUNT_COPY_TASK = "select count(taskId) as total from copy_task";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserMirrorStatisticInfoServiceImpl.class);
    
    private static final int RECORD_NUMBER = 5;
    
    @MethodLogAble
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void create(UserMirrorStatisticInfo info)
    {
        if(null == info)
        {
            return;
        }
        // 只保留最后5次统计
        List<UserMirrorStatisticInfo> lst = userMirrorStatisticInfoDAO.listStatisticByUserId(info);
        if (lst.size() > (RECORD_NUMBER - 1))
        {
            int size = lst.size();
            for (int i = (RECORD_NUMBER - 1); i < size; i++)
            {
                userMirrorStatisticInfoDAO.delete(lst.get(i));
            }
            
        }
        
        userMirrorStatisticInfoDAO.create(info);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int update(UserMirrorStatisticInfo info)
    {
        
        return userMirrorStatisticInfoDAO.update(info);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int delete(UserMirrorStatisticInfo info)
    {
        
        return userMirrorStatisticInfoDAO.delete(info);
    }
    
    @Override
    public UserMirrorStatisticInfo get(UserMirrorStatisticInfo info)
    {
        return userMirrorStatisticInfoDAO.get(info);
    }
    
    @Override
    public List<UserMirrorStatisticInfo> listStatisticByUserId(UserMirrorStatisticInfo info)
    {
        return userMirrorStatisticInfoDAO.listStatisticByUserId(info);
    }
    
    @Override
    public UserMirrorStatisticInfo getLastStatisticInfo(UserMirrorStatisticInfo info)
    {
        
        return userMirrorStatisticInfoDAO.getLastStatisticInfo(info);
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
            LOGGER.warn("Can not close connection", e);
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
            LOGGER.warn("Can not close preparedStatement", e);
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
            LOGGER.warn("Can not close preparedStatement", e);
        }
    }
    
    private long getTaskTotal() throws ClassNotFoundException, SQLException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try
        {
            conn = getConnection(SYSDB);
            ps = conn.prepareStatement(SQL_SELECT_COUNT_COPY_TASK);
            res = ps.executeQuery();
            while (res.next())
            {
                return res.getLong("total");
            }
            return 0L;
        }
        finally
        {
            this.closeResultSet(res);
            this.closePreparedStatment(ps);
            this.closeConnection(conn);
        }
    }
    
    @Override
    public long getCopyTaskTotal()
    {
        try
        {
            return getTaskTotal();
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        
        return 0;
    }
    
}
