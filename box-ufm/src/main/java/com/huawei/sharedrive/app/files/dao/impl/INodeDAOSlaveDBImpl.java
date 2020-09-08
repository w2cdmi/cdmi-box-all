package com.huawei.sharedrive.app.files.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.common.systemtask.domain.UserDBInfo;
import com.huawei.sharedrive.app.common.systemtask.service.UserDBInfoService;
import com.huawei.sharedrive.app.exception.SlaveDBSQLException;
import com.huawei.sharedrive.app.files.dao.INodeDAOSlaveDB;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.slavedb.manager.SlaveDatabaseManager;

@Service("iNodeDAOSlaveDB")
public class INodeDAOSlaveDBImpl implements INodeDAOSlaveDB
{
    private static final String SQL_LSTCONTENTNODE = "select * from inode_? where type != 0 order by  ownedBy desc";
    
    private static final String SQL_USERSTATISTICSINFO = "select sum(size) as spaceUsed, max(id) as lastStatisNode,count(*) as FileCount from inode_? where ownedBy=? and status!=4 and status!=1 and type!=0";
    
    private static final String SQL_STATISTICBYRESOURCEGROUPID = "SELECT count(size) as number,SUM(size) as allSize from inode_?  where type = 1 and status = 0 and resourceGroupId=?";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(INodeDAOSlaveDBImpl.class);
    
    private static final String DB_NAME_PRE = "userdb_";
    
    @Autowired
    private SlaveDatabaseManager slaveDatabaseManager;
    
    @Autowired
    private UserDBInfoService userDBInfoService;
    
    private List<UserDBInfo> dbInfos = null;
    
    private int getUserDB(long ownerId)
    {
        if (null == dbInfos || dbInfos.isEmpty())
        {
            dbInfos = userDBInfoService.listAll();
        }
        return (int) ((ownerId % 1024) / (1024 / dbInfos.size())) + 1;
        
    }
    
    @Override
    public List<INode> lstContentNode(int userdbNumber, int tableNumber, Limit limit)
    {
        
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(SQL_LSTCONTENTNODE);
        if (null != limit)
        {
            sqlBuffer.append(" limit ?,?");
        }
        
        List<INode> lstINodes = null;
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(DB_NAME_PRE + userdbNumber);
            ps = connection.prepareStatement(sqlBuffer.toString());
            ps.setInt(1, tableNumber);
            if (null != limit)
            {
                ps.setLong(2, limit.getOffset());
                ps.setInt(3, limit.getLength());
            }
            lstINodes = getINode(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("lstContentNode use slavedb error,userdbNumber is: " + userdbNumber
                + " tableNumber is: " + tableNumber, e);
        }
        catch (Exception e)
        {
            LOGGER.error("lstContentNode use slavedb error,userdbNumber is: " + userdbNumber
                + " tableNumber is: " + tableNumber, e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
        return lstINodes;
    }
    
    @Override
    public UserStatisticsInfo getUserInfoById(long ownerId)
    {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(SQL_USERSTATISTICSINFO);
        
        UserStatisticsInfo info = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        try
        {
            connection = getConnection(DB_NAME_PRE + getUserDB(ownerId));
            ps = connection.prepareStatement(sqlBuffer.toString());
            ps.setInt(1, INodeDAOV2Impl.getTableSuffix(ownerId));
            ps.setLong(2, ownerId);
            rst = ps.executeQuery();
            
            while (rst.next())
            {
                info = new UserStatisticsInfo();
                info.setFileCount(rst.getLong("FileCount"));
                info.setLastStatisNode(rst.getLong("lastStatisNode"));
                info.setSpaceUsed(rst.getLong("spaceUsed"));
            }
            
            if (null != info && info.getSpaceUsed() == null)
            {
                info.setSpaceUsed(0L);
            }
            if (null != info && info.getFileCount() == null)
            {
                info.setFileCount(0L);
            }
            
            if (null != info && info.getLastStatisNode() == null)
            {
                info.setLastStatisNode(0L);
            }
            
        }
        catch (RuntimeException e)
        {
            LOGGER.error("getBystatusAndExeType use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("getBystatusAndExeType use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeResultSet(rst);
            closeConnect(connection);
            closeStatement(ps);
        }
        return info;
    }
    
    private List<INode> getINode(PreparedStatement ps) throws SQLException
    {
        ResultSet rst = null;
        List<INode> lstINodes = null;
        INode iNode = null;
        try
        {
            rst = ps.executeQuery();
            lstINodes = new ArrayList<INode>(3);
            while (rst.next())
            {
                iNode = new INode();
                iNode.setId(rst.getLong("id"));
                iNode.setParentId(rst.getLong("parentId"));
                iNode.setObjectId(rst.getString("objectId"));
                iNode.setName(rst.getString("name"));
                iNode.setSize(rst.getLong("size"));
                iNode.setDescription(rst.getString("description"));
                iNode.setType(rst.getByte("type"));
                iNode.setStatus(rst.getByte("status"));
                iNode.setVersion(rst.getString("version"));
                iNode.setOwnedBy(rst.getLong("ownedBy"));
                iNode.setCreatedAt(rst.getDate("createdAt"));
                iNode.setModifiedAt(rst.getDate("modifiedAt"));
                iNode.setContentCreatedAt(rst.getDate("contentCreatedAt"));
                iNode.setContentModifiedAt(rst.getDate("contentModifiedAt"));
                iNode.setCreatedBy(rst.getLong("createdBy"));
                iNode.setModifiedBy(rst.getLong("modifiedBy"));
                iNode.setShareStatus(rst.getByte("shareStatus"));
                iNode.setSyncStatus(rst.getByte("syncStatus"));
                iNode.setSyncVersion(rst.getLong("syncVersion"));
                iNode.setLinkCode(rst.getString("linkCode"));
                iNode.setEncryptKey(rst.getString("encryptKey"));
                iNode.setSha1(rst.getString("sha1"));
                iNode.setSecurityId(rst.getByte("securityId"));
                iNode.setResourceGroupId(rst.getInt("resourceGroupId"));
                iNode.setKiaLabel(rst.getLong("kiaLabel"));
                lstINodes.add(iNode);
            }
        }
        finally
        {
            closeResultSet(rst);
        }
        return lstINodes;
    }
    
    private Map<Long, Long> getStatistic(PreparedStatement ps) throws SQLException
    {
        ResultSet res = null;
        Map<Long, Long> map = null;
        try
        {
            res = ps.executeQuery();
            map = new HashMap<Long, Long>(1);
            while (res.next())
            {
                map.put(res.getLong("number"), res.getLong("allSize"));
                break;
            }
        }
        finally
        {
            closeResultSet(res);
        }
        return map;
    }
    
    @Override
    public Map<Long, Long> lstFilesNumAndSizesByResourceGroup(int userdbNumber, int tableNumber,
        int resourceGroupId)
    {
        Map<Long, Long> map = null;
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(DB_NAME_PRE + userdbNumber);
            ps = connection.prepareStatement(SQL_STATISTICBYRESOURCEGROUPID);
            ps.setInt(1, tableNumber);
            ps.setInt(2, resourceGroupId);
            map = getStatistic(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("lstFilesNumAndSizesByResourceGroup use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("lstFilesNumAndSizesByResourceGroup use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
        return map;
    }
    
    private void closeResultSet(ResultSet rs)
    {
        if (null != rs)
        {
            try
            {
                rs.close();
            }
            catch (SQLException e)
            {
                LOGGER.debug("Caught while closing statement: " + e, e);
            }
        }
    }
    
    private void closeConnect(Connection connection)
    {
        if (connection != null)
        {
            try
            {
                if (!connection.getAutoCommit())
                {
                    connection.commit();
                }
                connection.close();
            }
            catch (SQLException e)
            {
                LOGGER.error("close connectioin error");
            }
        }
    }
    
    private void closeStatement(PreparedStatement stmt)
    {
        if (null != stmt)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException e)
            {
                LOGGER.debug("Caught while closing statement: " + e, e);
            }
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

}
