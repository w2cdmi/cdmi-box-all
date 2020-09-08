package com.huawei.sharedrive.app.files.manager.impl;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import com.huawei.sharedrive.app.exception.BadRequestException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.manager.MetadataManager;
import com.huawei.sharedrive.app.files.service.MetadataService;
import com.huawei.sharedrive.app.files.service.SyncMetadataService;
import com.huawei.sharedrive.app.files.service.impl.metadata.MetadataTempFile;
import com.huawei.sharedrive.app.files.synchronous.SynConstants;
import com.huawei.sharedrive.app.files.synchronous.SyncVersionRsp;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.user.service.UserSyncVersionService;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

@Component("metadataManager")
public class MetadataManagerImpl implements MetadataManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataManagerImpl.class);
    
    @Autowired
    private MetadataService metadataService;

    @Autowired
    private UserSyncVersionService userSyncVersionService;
    
    private boolean isShareStorage()
    {
        String isShareStr = PropertiesUtils.getProperty("isShareStorage",
            "false",
            PropertiesUtils.BundleName.BRIDGE);
        if (StringUtils.equalsIgnoreCase(isShareStr, "true"))
        {
            return true;
        }
        return false;
    }
    
    private SyncVersionRsp getMetadataFile(UserToken user, INode syncFolder, boolean isNeedZip)
        throws BaseRunException
    {
        long userId = user.getId();
        long begin = System.currentTimeMillis();
        long lastedSyncVersion = userSyncVersionService.getUserCurrentSyncVersion(syncFolder.getOwnedBy());
        MetadataTempFile tempDbFileObj = metadataService.exportFileToLocal(userId, syncFolder);
        LOGGER.info("[syncLog]exportFileToLocal cost:" + (System.currentTimeMillis() - begin));
        File file;
        if (isShareStorage())
        {
            file = tempDbFileObj.getFile();
        }
        else
        {
            file = metadataService.pullFileFromDbServer(userId, tempDbFileObj);
        }
        
        LOGGER.info("[syncLog]pullFileFromDbServer cost:" + (System.currentTimeMillis() - begin));
        String sqliteFilePath = metadataService.generateSqliteFile(file, userId, syncFolder.getId());
        LOGGER.info("[syncLog]generateSqliteFile cost:" + (System.currentTimeMillis() - begin));
        sqliteFilePath = syncMetadataService.checkAndCreateZipSQLiteFile(sqliteFilePath, isNeedZip);
        return syncMetadataService.buildSyncRsp(lastedSyncVersion, sqliteFilePath);
    }
    
    @Autowired
    private SyncMetadataService syncMetadataService;

    @Override
    public SyncVersionRsp getMetadataFile(UserToken user, INode syncFolder, String fileRootDir,
        HttpServletResponse response, boolean isNeedZip) throws BaseRunException
    {
        if (null == syncFolder)
        {
            throw new BadRequestException("syncFolder is null");
        }
        
//        if(!metadataService.supportBridge() || INode.FILES_ROOT != syncFolder.getId())
//        {
            return syncMetadataService.getFolderMetadataFile(user,
                syncFolder,
                SynConstants.SYNC_METADATA_TEMP_FILE_PATH,
                response,
                isNeedZip);
//        }
//        try
//        {
//            return getMetadataFile(user, syncFolder, isNeedZip);
//        }
//        catch(Exception e)
//        {
//            LOGGER.error("[metadataLog] fail to getLog throw bridge.", e);
//            return syncMetadataService.getFolderMetadataFile(user,
//                syncFolder,
//                SynConstants.SYNC_METADATA_TEMP_FILE_PATH,
//                response,
//                isNeedZip);
//        }
    }
    
}
