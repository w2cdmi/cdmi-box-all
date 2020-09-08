package com.huawei.sharedrive.app.files.service.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BadRequestException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.SyncMetadataService;
import com.huawei.sharedrive.app.files.synchronous.INodeRowHandle;
import com.huawei.sharedrive.app.files.synchronous.SQLiteUtils;
import com.huawei.sharedrive.app.files.synchronous.SynConstants;
import com.huawei.sharedrive.app.files.synchronous.SyncFileUtils;
import com.huawei.sharedrive.app.files.synchronous.SyncHistoryFileDeleteTask;
import com.huawei.sharedrive.app.files.synchronous.SyncVersionRsp;
import com.huawei.sharedrive.app.files.synchronous.ZipUtils;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.user.service.UserSyncVersionService;
import com.huawei.sharedrive.app.utils.Utils;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.SystemConfig;

@Component
public class SyncMetadataServiceImpl implements SyncMetadataService
{
    private static Logger logger = LoggerFactory.getLogger(SyncMetadataServiceImpl.class);
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private INodeACLService iNodeACLService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private UserSyncVersionService userSyncVersionService;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private TeamSpaceService teamSpaceService;
    
    // 现网10多万用户未使用信号量也使用正常，先暂时屏蔽
    
    @Override
    public SyncVersionRsp getDeltaSyncMetadataFile(UserToken user, long ownerId, long syncVersion,
        String fileRootDir, HttpServletResponse response) throws BaseRunException
    {
        return getDeltaSyncMetadataFile(user, ownerId, syncVersion, fileRootDir, response, false);
    }
    
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public SyncVersionRsp getDeltaSyncMetadataFile(UserToken user, long ownerId, long syncVersion,
        String fileRootDir, HttpServletResponse response, boolean isNeedZip) throws BaseRunException
    {
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user,
            new INode(ownerId, INode.FILES_ROOT),
            AuthorityMethod.GET_METADATA.name());
        
        long endSyncVersion = userSyncVersionService.getUserCurrentSyncVersion(ownerId);
        
        // 同步版本号无变化, 不生成同步元数据
        if (syncVersion >= endSyncVersion)
        {
            return null;
        }
        
        SystemConfig config = systemConfigDAO.get(SynConstants.SYNC_USER_MAX_NODENUM);
        int userMaxNodeNum = SynConstants.USER_DEFAULT_NODE_MAXNUM;
        if (config != null)
        {
            userMaxNodeNum = Integer.parseInt(config.getValue());
        }
        
        // 当用syncVersion=0获取元数据时，会导致性能低下，需要进行规格限制，当前规格是100万
        if (syncVersion == 0 || (endSyncVersion - syncVersion) >= userMaxNodeNum)
        {
            // 检查用户的元数据数目是否超过规格
            checkUserMaxINodeNum(ownerId, response, userMaxNodeNum);
        }
        String filePath = SyncFileUtils.buildSyncPath(fileRootDir, ownerId, endSyncVersion, null);
        
        Connection con = null;
        PreparedStatement prep = null;
        INodeRowHandle rowHander = null;
        try
        {
            // 获取sqlite连接
            con = SQLiteUtils.getConnection(filePath);
            // 创建files_inode表
            SQLiteUtils.createSQLiteTable(con);
            // 获取并写入sqlite数据, 必须先去掉自动提交
            con.setAutoCommit(false);
            prep = con.prepareStatement(SQLiteUtils.SQL_INSERT);
            rowHander = iNodeDAO.getDeltaINodeMetadatas(ownerId,
                syncVersion,
                endSyncVersion,
                new INodeRowHandle(con, prep));
            
            logger.info("getDeltaSyncMetadataFile=========" + rowHander.getTotal() + ',' + syncVersion + ','
                + endSyncVersion);
            
            SQLiteUtils.insertSQLiteData(con, prep, rowHander.getSynDatas());
        }
        catch (Exception e)
        {
            SyncFileUtils.deleteFile(filePath);
            logger.error(e.getMessage(), e);
            throw new InternalServerErrorException(e);
        }
        finally
        {
            Utils.close(prep);
            Utils.close(con);
        }
        
        filePath = checkAndCreateZipSQLiteFile(filePath, isNeedZip);
        
        logger.info("getDeltaSyncMetadataFile total:" + rowHander.getTotal());
        if (rowHander.getTotal() == 0)
        {
            SyncFileUtils.deleteFile(filePath);
            return null;
        }
        String[] logMsgs = new String[]{String.valueOf(ownerId), null};
        String keyword = "GET META DATA  :" + syncVersion + ':' + endSyncVersion;
        
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_DELTA_SYNC_META_DATA,
            logMsgs,
            keyword);
        return buildSyncRsp(endSyncVersion, filePath);
    }
    
    @Override
    public SyncVersionRsp getFolderMetadataFile(UserToken user, INode syncFolder, String fileRootDir,
        HttpServletResponse response) throws BaseRunException
    {
        return getFolderMetadataFile(user, syncFolder, fileRootDir, response, false);
    }
    
    @Override
    public SyncVersionRsp getFolderMetadataFile(UserToken user, INode syncFolder, String fileRootDir,
        HttpServletResponse response, boolean isNeedZip) throws BaseRunException
    {
        if (null == syncFolder)
        {
            throw new BadRequestException("syncFolder is null");
        }
        
        INode node = fileBaseService.getINodeInfo(syncFolder.getOwnedBy(), syncFolder.getId());
        String keyword = String.valueOf(node != null ? node.getName() : null);
        String[] logMsgs = null;
        if (INode.FILES_ROOT == syncFolder.getId())
        {
            logMsgs = new String[]{String.valueOf(syncFolder.getOwnedBy()), String.valueOf(INode.FILES_ROOT)};
            fileBaseService.sendINodeEvent(user,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_FOLDER_META_DATA,
                logMsgs,
                keyword);
            return getRootFolderMetadataSQLFile(user, syncFolder, fileRootDir, response, isNeedZip);
        }
        logMsgs = new String[]{String.valueOf(syncFolder.getOwnedBy()),
            String.valueOf(node != null ? node.getParentId() : null)};
        fileBaseService.sendINodeEvent(user,
            null,
            null,
            null,
            UserLogType.GET_FOLDER_META_DATA,
            logMsgs,
            keyword);
        return getFolderMetadataSQLFile(user, syncFolder, fileRootDir, isNeedZip);
        
    }
    
    @Override
    public SyncVersionRsp listFolderChangeNode(UserToken user, long ownerId, Date modifiedAt,
        String fileRootDir) throws BaseRunException
    {
        return listFolderChangeNode(user, ownerId, modifiedAt, fileRootDir, false);
        
    }
    
    @Override
    public SyncVersionRsp listFolderChangeNode(UserToken user, long ownerId, Date modifiedAt,
        String fileRootDir, boolean isNeedZip) throws BaseRunException
    {
        // 只有自己才能会取自己的元数据
        if (null == user)
        {
            throw new ForbiddenException("user is null ");
        }
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user,
            new INode(ownerId, INode.FILES_ROOT),
            AuthorityMethod.GET_METADATA.name());
        
        String filePath = SyncFileUtils.buildSyncPath(fileRootDir, ownerId, modifiedAt.getTime(), null);
        Connection con = null;
        PreparedStatement prep = null;
        INodeRowHandle rowHander = null;
        
        try
        {
            // 获取sqlite连接
            con = SQLiteUtils.getConnection(filePath);
            // 创建files_inode表
            SQLiteUtils.createSQLiteTable(con);
            // 获取并写入sqlite数据, 必须先去掉自动提交
            con.setAutoCommit(false);
            prep = con.prepareStatement(SQLiteUtils.SQL_INSERT);
            rowHander = iNodeDAO.getChangeMetadatas(ownerId, modifiedAt, new INodeRowHandle(con, prep));
            
            SQLiteUtils.insertSQLiteData(con, prep, rowHander.getSynDatas());
        }
        catch (Exception e)
        {
            SyncFileUtils.deleteFile(filePath);
            logger.error(e.getMessage(), e);
            throw new InternalServerErrorException(e);
        }
        finally
        {
            Utils.close(prep);
            Utils.close(con);
        }
        
        filePath = checkAndCreateZipSQLiteFile(filePath, isNeedZip);
        
        logger.info("listFolderChangeNode total:" + rowHander.getTotal());
        
        if (rowHander.getTotal() == 0)
        {
            SyncFileUtils.deleteFile(filePath);
            return null;
        }
        
        return buildSyncRsp(0, filePath);
    }
    
 
    
    @Override
    public SyncVersionRsp buildSyncRsp(long reSyncVersion, String filePath)
    {
        SyncVersionRsp rsp = new SyncVersionRsp();
        if (new File(filePath).exists())
        {
            rsp.setMetadataSqlPath(filePath);
            rsp.setCurrentSyncVersion(reSyncVersion);
        }
        return rsp;
    }
    
    @Override
    public String checkAndCreateZipSQLiteFile(String filePath, boolean isNeedZip)
        throws InternalServerErrorException
    {
        if (isNeedZip)
        {
            File newZipTempFile = new File(filePath + "#z");
            File plainTempFile = new File(filePath);
            try
            {
                ZipUtils.writeFileToZip(new File[]{plainTempFile},
                    new String[]{"synchronous_file.db"},
                    newZipTempFile);
            }
            catch (Exception e1)
            {
                String message = "zip temp file [ " + plainTempFile + "] to zipfile failed. ";
                throw new InternalServerErrorException(message, e1);
            }
            
            SyncFileUtils.deleteFile(filePath);
            filePath = filePath + "#z";
        }
        return filePath;
    }
    
    private void copyMetadataToTempTable(INode srcNode, long destTableSuffix)
    {
        logger.info("copyMetadataToTempTable destTableSuffix:" + destTableSuffix);
        iNodeDAO.copyTempINodeTableNoBackup(srcNode, destTableSuffix);
    }
    
    private void dropTempTable(long ownerId, Long destTableSuffix)
    {
        iNodeDAO.dropTempINodeTable(ownerId, destTableSuffix);
    }
    
    /**
     * 
     * @param user
     * @param syncFolder
     * @param fileRootDir
     * @return
     * @throws BaseRunException
     */
    private SyncVersionRsp getFolderMetadataSQLFile(UserToken user, INode syncFolder, String fileRootDir,
        boolean isNeedZip) throws BaseRunException
    {
        INode inode = fileBaseService.getINodeInfo(syncFolder.getOwnedBy(), syncFolder.getId());
        
        if (null == inode)
        {
            String msg = "inode is null,ower_id:" + syncFolder.getOwnedBy() + ",id:" + syncFolder.getId();
            throw new NoSuchItemsException(msg);
        }
        else if (inode.getType() != INode.TYPE_FOLDER)
        {
            String msg = "inode not folder,ower_id:" + syncFolder.getOwnedBy() + ",id:" + syncFolder.getId()
                + ",type:" + inode.getType();
            throw new BadRequestException(msg);
        }
        
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user, inode, AuthorityMethod.GET_METADATA.name());
        
        long reSyncVersion = userSyncVersionService.getUserCurrentSyncVersion(syncFolder.getOwnedBy());
        
        String filePath = SyncFileUtils.buildSyncPath(fileRootDir, inode.getOwnedBy(), reSyncVersion, inode);
        
        Connection con = null;
        PreparedStatement prep = null;
        long total = 0L;
        try
        {
            // 获取sqlite连接
            con = SQLiteUtils.getConnection(filePath);
            // 创建files_inode表
            SQLiteUtils.createSQLiteTable(con);
            // 获取并写入sqlite数据, 必须先去掉自动提交
            con.setAutoCommit(false);
            prep = con.prepareStatement(SQLiteUtils.SQL_INSERT);
            total = selectNodeMetadataByRecursive(inode, new INodeRowHandle(con, prep));
        }
        catch (Exception e)
        {
            SyncFileUtils.deleteFile(filePath);
            new Thread(new SyncHistoryFileDeleteTask()).start();
            logger.error(e.getMessage(), e);
            throw new InternalServerErrorException(e);
        }
        finally
        {
            // manager.returnFolderSyncSemaphore();
            Utils.close(prep);
            Utils.close(con);
        }
        
        filePath = checkAndCreateZipSQLiteFile(filePath, isNeedZip);
        
        logger.info("getFolderMetadataSQLFile total:" + total);
        
        if (total == 0)
        {
            SyncFileUtils.deleteFile(filePath);
            return null;
        }
        String[] logMsgs = new String[]{inode.getName(), String.valueOf(inode.getParentId())};
        String keyword = inode.getName();
        
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_FOLDER_META_DATA,
            logMsgs,
            keyword);
        return buildSyncRsp(reSyncVersion, filePath);
    }
    
    private long getMetadata(long ownerId, Long destTableSuffix, INodeRowHandle rowHander)
        throws SQLException
    {
        Limit limit = new Limit();
        long offset = 0;
        int length = 100000;
        long total = 0L;
        for (;;)
        {
            limit.setOffset(offset);
            limit.setLength(length);
            rowHander.setTotal(0L);
            rowHander = iNodeDAO.getTempINodes(ownerId, destTableSuffix, limit, rowHander);
            
            if (null == rowHander || rowHander.getTotal() == 0)
            {
                break;
            }
            total += rowHander.getTotal();
            
            SQLiteUtils.insertSQLiteData(rowHander.getCon(), rowHander.getPrep(), rowHander.getSynDatas());
            
            if (rowHander.getTotal() < length)
            {
                break;
            }
            offset = offset + length;
        }
        return total;
    }
    
    /**
     * 
     * @param user
     * @param ownerId
     * @param fileRootDir
     * @return
     * @throws BaseRunException
     */
    private SyncVersionRsp getRootFolderMetadataSQLFile(UserToken user, INode syncFolder, String fileRootDir,
        HttpServletResponse response, boolean isNeedZip) throws BaseRunException
    {
        if (user.getId() != syncFolder.getOwnedBy())
        {
            boolean hasRight = false;
            // 获取用户类型，如果为团队空间，则获取团队空间的拥有者
            byte type = userTokenHelper.getUserType(syncFolder.getOwnedBy());
            if(type == User.USER_TYPE_TEAMSPACE){
                TeamSpace teamSpace = teamSpaceService.getTeamSpaceNoCheck(syncFolder.getOwnedBy());
                if(null!=teamSpace){
                    if(user.getId() ==teamSpace.getOwnerBy()){
                        hasRight = true;
                    }
                }
            }
            if(!hasRight){
                throw new ForbiddenException("can't access other's metadata");
            }
        }
        
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user, syncFolder, AuthorityMethod.GET_METADATA.name());
        
        // 检查用户的元数据数目是否超过规格
        checkUserMaxINodeNum(syncFolder.getOwnedBy(), response);
        
        long reSyncVersion = userSyncVersionService.getUserCurrentSyncVersion(syncFolder.getOwnedBy());
        
        long destTableSuffix = System.currentTimeMillis();
        
        // 如果出现数据库异常需要删除临时表
        try
        {
            copyMetadataToTempTable(syncFolder, destTableSuffix);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            dropTempTable(syncFolder.getOwnedBy(), destTableSuffix);
            throw new InternalServerErrorException(e);
        }
        
        String filePath = SyncFileUtils.buildSyncPath(fileRootDir,
            syncFolder.getOwnedBy(),
            reSyncVersion,
            syncFolder);
        
        Connection con = null;
        PreparedStatement prep = null;
        long total = 0L;
        try
        {
            // 获取sqlite连接
            con = SQLiteUtils.getConnection(filePath);
            // 创建files_inode表
            SQLiteUtils.createSQLiteTable(con);
            // 获取并写入sqlite数据, 必须先去掉自动提交
            con.setAutoCommit(false);
            prep = con.prepareStatement(SQLiteUtils.SQL_INSERT);
            total = getMetadata(syncFolder.getOwnedBy(), destTableSuffix, new INodeRowHandle(con, prep));
        }
        catch (Exception e)
        {
            SyncFileUtils.deleteFile(filePath);
            new Thread(new SyncHistoryFileDeleteTask()).start();
            logger.error(e.getMessage(), e);
            throw new InternalServerErrorException(e);
        }
        finally
        {
            Utils.close(prep);
            Utils.close(con);
            dropTempTable(syncFolder.getOwnedBy(), destTableSuffix);
        }
        
        filePath = checkAndCreateZipSQLiteFile(filePath, isNeedZip);
        
        logger.info("getRootFolderMetadataSQLFile total:" + total);
        
        if (total == 0)
        {
            SyncFileUtils.deleteFile(filePath);
            return null;
        }
        INode node = fileBaseService.getINodeInfo(syncFolder.getOwnedBy(), syncFolder.getId());
        String[] logMsgs = new String[]{};
        String keyword = "";
        if (node != null)
        {
            logMsgs = new String[]{node.getName(), String.valueOf(node.getParentId())};
            keyword = node.getName();
        }
        
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_FOLDER_META_DATA,
            logMsgs,
            keyword);
        return buildSyncRsp(reSyncVersion, filePath);
    }
    
    private void checkUserMaxINodeNum(long ownerBy, HttpServletResponse response)
        throws ExceedUserMaxNodeNumException
    {
        SystemConfig config = systemConfigDAO.get(SynConstants.SYNC_USER_MAX_NODENUM);
        int userMaxNodeNum = SynConstants.USER_DEFAULT_NODE_MAXNUM;
        if (config != null)
        {
            userMaxNodeNum = Integer.parseInt(config.getValue());
        }
        checkUserMaxINodeNum(ownerBy, response, userMaxNodeNum);
    }
    
    private void checkUserMaxINodeNum(long ownerBy, HttpServletResponse response, int userMaxNodeNum)
        throws ExceedUserMaxNodeNumException
    {
        INode filter = new INode();
        filter.setOwnedBy(ownerBy);
        filter.setStatus(INode.STATUS_NORMAL);
        long userTotalNodeNum = iNodeDAO.getINodeCountByStatusIgnoreVersion(filter);
        
        response.addHeader(SynConstants.HEAD_SYNC_USER_CURR_NODENUM, String.valueOf(userTotalNodeNum));
        response.addHeader(SynConstants.HEAD_SYNC_USER_MAX_NODENUM, String.valueOf(userMaxNodeNum));
        
        if (userTotalNodeNum > userMaxNodeNum)
        {
            logger.error("userTotalNodeNum exceed userMaxNodeNum , userTotalNodeNum:" + userTotalNodeNum
                + ", userMaxNodeNum:" + userMaxNodeNum);
            throw new ExceedUserMaxNodeNumException(
                "userTotalNodeNum exceed userMaxNodeNum , userTotalNodeNum:" + userTotalNodeNum
                    + ", userMaxNodeNum:" + userMaxNodeNum);
        }
    }
    
    private long selectNodeMetadataByRecursive(INode srcNode, INodeRowHandle rowHandle) throws SQLException
    {
        long total = 0L;
        // 批量获取节点
        rowHandle.setTotal(0L);
        INodeRowHandle rowHander = iNodeDAO.getFolderMetadatas(srcNode, rowHandle);
        
        SQLiteUtils.insertSQLiteData(rowHandle.getCon(), rowHandle.getPrep(), rowHander.getSynDatas());
        total += rowHander.getTotal();
        LinkedList<INode> tempList;
        List<INode> temp = rowHander.getFolderLst();
        if (temp instanceof LinkedList)
        {
            tempList = (LinkedList<INode>) temp;
        }
        else
        {
            return total;
        }
        INode tmpNode = tempList.poll();
        while (tmpNode != null)
        {
            total += selectNodeMetadataByRecursive(tmpNode, rowHandle);
            tmpNode = tempList.poll();
        }
        return total;
    }
}
