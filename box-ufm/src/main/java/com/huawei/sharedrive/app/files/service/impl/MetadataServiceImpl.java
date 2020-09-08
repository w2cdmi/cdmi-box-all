package com.huawei.sharedrive.app.files.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.huawei.sharedrive.app.exception.BadRequestException;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.exception.NoSuchFileException;
import com.huawei.sharedrive.app.files.dao.MetadataDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.MetadataService;
import com.huawei.sharedrive.app.files.service.impl.metadata.BridgeClient;
import com.huawei.sharedrive.app.files.service.impl.metadata.MetadataFileTools;
import com.huawei.sharedrive.app.files.service.impl.metadata.MetadataTempFile;
import com.huawei.sharedrive.app.files.synchronous.INodeMetadata;
import com.huawei.sharedrive.app.files.synchronous.SQLiteUtils;
import com.huawei.sharedrive.app.files.synchronous.SyncFileUtils;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.common.slavedb.domain.DatabaseAddr;
import pw.cdmi.common.slavedb.manager.SlaveDatabaseManager;

@Service("metadataService")
public class MetadataServiceImpl implements MetadataService
{

    private static final int LIST_LIMIT = 10240;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataServiceImpl.class);
    
    @Autowired
    private BridgeClient bridgeClient;
    
    @Autowired
    private MetadataDAO metadataDAO;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private TeamSpaceService teamSpaceService;
    
    @Override
    public MetadataTempFile exportFileToLocal(long userId, INode syncFolder)
    {
        if (null == syncFolder)
        {
            throw new BadRequestException("syncFolder is null");
        }
        if (userId != syncFolder.getOwnedBy())
        {
            boolean hasRight = false;
            // 获取用户类型，如果为团队空间，则获取团队空间的拥有者
            byte type = userTokenHelper.getUserType(syncFolder.getOwnedBy());
            if(type == User.USER_TYPE_TEAMSPACE){
                TeamSpace teamSpace = teamSpaceService.getTeamSpaceNoCheck(syncFolder.getOwnedBy());
                if(null!=teamSpace){
                    if(userId ==teamSpace.getOwnerBy()){
                        hasRight = true;
                    }
                }
            }
            if(!hasRight){
                throw new ForbiddenException("can't access other's metadata");
            }
        }
        String filename = MetadataFileTools.getMedatadaFileName(syncFolder.getOwnedBy());
        try
        {
            long dbNumber = getDbNumber(userId, getTotalDbNumbers());
            DatabaseAddr dbAddress = getDbIp(dbNumber);
            LOGGER.info("EXPORT FILE === START ========="+MetadataFileTools.getMetadataFilePath() + filename);
            metadataDAO.exportINodesToLocal(syncFolder, MetadataFileTools.getMetadataFilePath() + filename);
            LOGGER.info("EXPORT FILE === END ========="+MetadataFileTools.getMetadataFilePath() + filename);
            File file = new File(MetadataFileTools.getMetadataFilePath() + filename);
            MetadataTempFile tempFileObj = new MetadataTempFile();
            tempFileObj.setFile(file);
            tempFileObj.setFileName(filename);
            tempFileObj.setMainDbIP(dbAddress.getMainAddr());
            tempFileObj.setSlaveDbIP(dbAddress.getSlaveAddr());
            return tempFileObj;
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
            return null;
        }
    }
    
    private int getTotalDbNumbers()
    {
        try
        {
            return Integer.parseInt(PropertiesUtils.getProperty("filebridge.dbNumberTotal", "8", PropertiesUtils.BundleName.BRIDGE));
        }
        catch(NumberFormatException e)
        {
            return 8;
        }
        
    }

    @Override
    public String generateSqliteFile(File file, long ownerId, long nodeId)
    {
        FileInputStream fis = null;
        InputStreamReader reader = null;
        BufferedReader bufferReader = null;
        Connection con = null;
        try
        {
            fis = new FileInputStream(file);
            reader = new InputStreamReader(fis, "utf8");
            bufferReader = new BufferedReader(reader);
            List<INodeMetadata> nodeList = readListFromLocalFile(bufferReader, LIST_LIMIT);
            String sqlFilePath = SyncFileUtils.buildSyncPath(ownerId, nodeId);
            try
            {
                // 获取sqlite连接
                con = SQLiteUtils.getConnection(sqlFilePath);
                // 创建files_inode表
                SQLiteUtils.createSQLiteTable(con);
            }
            catch(SQLException e)
            {
                throw new InternalServerErrorException("Can not create sqlite table.", e);
            }
            while(!nodeList.isEmpty())
            {
                writeListToSqliteFile(nodeList, sqlFilePath, con);
                nodeList = readListFromLocalFile(bufferReader, LIST_LIMIT);
            }
            return sqlFilePath;
        }
        catch (FileNotFoundException e)
        {
            throw new InternalServerErrorException("FileNotFoundException " + file.getName(), e);
        }
        catch (IOException e)
        {
            throw new InternalServerErrorException("FileNotFoundException " + file.getName(), e);
        }
        finally
        {
            closeConnection(con);
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(bufferReader);
            deleteTempFile(file);
        }
        
    }

    /**
     * @param file
     */
    private void deleteTempFile(File file)
    {
        try
        {
            FileUtils.forceDelete(file);
        }
        catch (IOException e)
        {
            LOGGER.warn("Error to delete file " + file.getName());
        }
    }

    /**
     * @param con
     */
    private void closeConnection(Connection con)
    {
        if(null != con)
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {
                LOGGER.warn("Can not close Sqliete Exception");
            }
        }
    }
    
    @Override
    public File pullFileFromDbServer(long userId, MetadataTempFile tempFileObj)
    {
        try
        {
            return bridgeClient.getFileByBridge(userId, tempFileObj, true);
        }
        catch(NoSuchFileException e)
        {
            LOGGER.warn("Can not find the file from " + tempFileObj.getMainDbIP());
            return bridgeClient.getFileByBridge(userId, tempFileObj, false);
        }
    }
    
    @Autowired
    private SlaveDatabaseManager slaveDatabaseManager;
    
    private DatabaseAddr getDbIp(long dbNumber)
    {
        long dbSuffix = dbNumber + 1;
        return slaveDatabaseManager.getDbAddress("userdb_" + dbSuffix);
    }
    
    /**
     * @param userId
     * @param totalUserDbNumber
     * @return
     */
    private long getDbNumber(long userId, int totalUserDbNumber)
    {
        long dbNumber = userId % 1024 / (1024 / totalUserDbNumber);
        return dbNumber;
    }
    

    @Override
    public boolean supportBridge()
    {
        String value = PropertiesUtils.getProperty("bridge.support", "true", PropertiesUtils.BundleName.BRIDGE);
        if(StringUtils.equalsIgnoreCase(value, "true"))
        {
            return true;
        }
        return false;
    }

    
    private List<INodeMetadata> readListFromLocalFile(BufferedReader bufferReader, int limit) throws IOException
    {
        String line = null;
        List<INodeMetadata> inodeList = new LinkedList<INodeMetadata>();
        INodeMetadata node = null;
        int i = 0;
        while((line = bufferReader.readLine()) != null)
        {
            node =  INodeMetadata.convertToMetaNode(line);
            inodeList.add(node);
            i++;
            if(i >= limit)
            {
                break;
            }
        }
        return inodeList;
    }

    private void writeListToSqliteFile(List<INodeMetadata> nodeList, String filePath, Connection con)
    {
        PreparedStatement prep = null;
        try
        {
            // 获取并写入sqlite数据, 必须先去掉自动提交
            con.setAutoCommit(false);
            prep = con.prepareStatement(SQLiteUtils.SQL_INSERT);
            SQLiteUtils.insertSQLiteData(con, prep, nodeList);
        }
        catch (SQLException e)
        {
            SyncFileUtils.deleteFile(filePath);
            throw new InternalServerErrorException(e);
        }
        
    }
    
    
}
