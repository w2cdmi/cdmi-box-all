package com.huawei.sharedrive.app.files.service;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.synchronous.SyncVersionRsp;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

public interface SyncMetadataService
{
    
    SyncVersionRsp buildSyncRsp(long reSyncVersion, String filePath);
    
    
    String checkAndCreateZipSQLiteFile(String filePath, boolean isNeedZip);
    
    /**
     * 增量元数据
     * 
     * @param user
     * @param ownerId
     * @param syncVersion
     * @param fileRootDir
     * @return
     * @throws BaseRunException
     */
    SyncVersionRsp getDeltaSyncMetadataFile(UserToken user, long ownerId, long syncVersion,
        String fileRootDir, HttpServletResponse response) throws BaseRunException;
    
    /**
     * 增量元数据
     * 
     * @param user
     * @param ownerId
     * @param syncVersion
     * @param fileRootDir
     * @param isNeedZip
     * @return
     * @throws BaseRunException
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    SyncVersionRsp getDeltaSyncMetadataFile(UserToken user, long ownerId, long syncVersion,
        String fileRootDir, HttpServletResponse response, boolean isNeedZip) throws BaseRunException;
    
    /**
     * 返回元数据文件
     * 
     * @param user
     * @param syncFolder
     * @param fileRootDir
     * @return
     * @throws BaseRunException
     */
    SyncVersionRsp getFolderMetadataFile(UserToken user, INode syncFolder, String fileRootDir, HttpServletResponse response)
        throws BaseRunException;
    
    
    /**
     * 返回元数据文件
     * 
     * @param user
     * @param syncFolder
     * @param fileRootDir
     * @param isNeedZip
     * @return
     * @throws BaseRunException
     */
    SyncVersionRsp getFolderMetadataFile(UserToken user, INode syncFolder, String fileRootDir, HttpServletResponse response, boolean isNeedZip)
        throws BaseRunException;
    
    /**
     * 获取变化元数据
     * 
     * @param user
     * @param ownerId
     * @param modfiedAt
     * @param fileRootDir
     * @return
     * @throws BaseRunException
     */
    SyncVersionRsp listFolderChangeNode(UserToken user, long ownerId, Date modfiedAt, String fileRootDir)
        throws BaseRunException;
    
    /**
     * 获取变化元数据
     * 
     * @param user
     * @param ownerId
     * @param modfiedAt
     * @param fileRootDir
     * @param isNeedZip
     * @return
     * @throws BaseRunException
     */
    SyncVersionRsp listFolderChangeNode(UserToken user, long ownerId, Date modfiedAt, String fileRootDir, boolean isNeedZip)
        throws BaseRunException;

}
