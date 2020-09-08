package com.huawei.sharedrive.app.files.synchronous;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.utils.Utils;

public final class SQLiteUtils
{
    private SQLiteUtils()
    {
        
    }
    
    public static final String SQL_INSERT = "insert into files_inode values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);";
    
    private static final String CONNECTION_STR = "jdbc:sqlite:";
    
    private static final String CREATE_DB_SQL = new StringBuffer("CREATE TABLE files_inode (").append(" id bigint(20) NOT NULL, ")
        .append(" parentId bigint(20) NOT NULL, ")
        .append(" type tinyint(4) NOT NULL, ")
        .append(" status tinyint(4) NOT NULL, ")
        .append(" name varchar(255) NOT NULL, ")
        .append(" size bigint(20) NOT NULL, ")
        .append(" sha1 varchar(64) default NULL, ")
        .append(" objectId varchar(32) default NULL, ")
        .append(" createdAt datetime default NULL, ")
        .append(" modifiedAt datetime default NULL, ")
        .append(" contentCreatedAt bigint(20) default NULL, ")
        .append(" contentModifiedAt bigint(20) default NULL, ")
        .append(" syncStatus tinyint(4) NOT NULL, ")
        .append(" syncVersion bigint(32) NOT NULL); ")
        .toString();
    
    private static final String FILE_IDX_1 = "CREATE INDEX file_idx1 on files_inode(id)";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteUtils.class);
    
    /**
     * 获取sqlite连接(同时会创建相应的sqlite文件)
     * 
     * @param filepath
     * @return
     * @throws SQLException
     */
    public static Connection getConnection(String filepath) throws SQLException
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
        }
        catch (Exception e)
        {
            LOGGER.warn("sqlite init failed");
            throw new SQLException(e);
        }
        
        return DriverManager.getConnection(CONNECTION_STR + filepath);
    }
    
    /**
     * 创建对应的数据表及索引
     * 
     * @param connection
     * @throws SQLException
     */
    public static void createSQLiteTable(Connection connection) throws SQLException
    {
        Statement statement = null;
        
        try
        {
            statement = connection.createStatement();
            statement.executeUpdate(CREATE_DB_SQL);
            statement.executeUpdate(FILE_IDX_1);
        }
        finally
        {
            Utils.close(statement);
        }
    }
    
    /**
     * 写入sqlite数据
     * 
     * @param connection
     * @param prep
     * @param synDatas
     * @throws SQLException
     */
    public static void insertSQLiteData(Connection connection, PreparedStatement prep,
        List<INodeMetadata> synDatas) throws SQLException
    {
        LinkedList<INodeMetadata> tempsynDatas;
        if (synDatas instanceof LinkedList)
        {
            tempsynDatas = (LinkedList<INodeMetadata>) synDatas;
        }
        else
        {
            return;
        }
        
        if (tempsynDatas.size() <= 0 || connection == null || prep == null)
        {
            return;
        }
        
        INodeMetadata data = tempsynDatas.poll();
        while (data != null)
        {
            prep.setLong(1, data.getId());
            prep.setLong(2, data.getParentId());
            prep.setInt(3, data.getType());
            prep.setInt(4, data.getStatus());
            prep.setString(5, data.getName());
            prep.setLong(6, data.getSize());
            prep.setString(7, data.getSha1());
            prep.setString(8, data.getObjectId());
            
            if (null != data.getCreatedAt())
            {
                prep.setLong(9, data.getCreatedAt());
            }
            if (null != data.getModifiedAt())
            {
                prep.setLong(10, data.getModifiedAt());
            }
            if (null != data.getContentCreatedAt())
            {
                prep.setLong(11, data.getContentCreatedAt());
            }
            if (null != data.getContentModifiedAt())
            {
                prep.setLong(12, data.getContentModifiedAt());
            }
            
            prep.setLong(13, data.getSyncStatus());
            prep.setLong(14, data.getSyncVersion());
            prep.addBatch();
            
            data = tempsynDatas.poll();
        }
        
        commit(connection, prep);
        
    }
    
    private static void commit(Connection connection, PreparedStatement prep) throws SQLException
    {
        prep.executeBatch();
        connection.commit();
    }
}
