package com.huawei.sharedrive.app.openapi.restv2.file;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BadRequestException;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.manager.MetadataManager;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.SyncMetadataService;
import com.huawei.sharedrive.app.files.service.lock.Locks;
import com.huawei.sharedrive.app.files.synchronous.SynConstants;
import com.huawei.sharedrive.app.files.synchronous.SyncFileUtils;
import com.huawei.sharedrive.app.files.synchronous.SyncVersionRsp;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/v2/metadata")
public class FilesMetadataAPI
{
    @Autowired
    private SyncMetadataService syncMetadataService;
    
    @Autowired
    private MetadataManager metadataManager; 
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    /**
     * 获取增量元数据
     * 
     * @param ownerBy
     * @param syncVersion
     * @param token
     * @param outputStream
     * @param response
     * @throws BaseRunException
     * @throws IOException
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/{ownerBy}", method = RequestMethod.GET)
    @ResponseBody
    public void getDeltaSyncMetadata(@PathVariable String ownerBy, Long syncVersion, Boolean zip,
        @RequestHeader("Authorization") String token, OutputStream outputStream, HttpServletResponse response,
        HttpServletRequest request)
        throws BaseRunException, IOException
    {
        UserToken userToken = null;
        String endSyncVersion = null;
        try
        {
            if (StringUtils.isBlank(ownerBy))
            {
                String message = "ownerBy or folderId is blank";
                throw new BadRequestException(message);
            }
            
            boolean isZip = false;
            if (zip != null)
            {
                isZip = zip;
            }
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), Long.parseLong(ownerBy));
            
            SyncVersionRsp rsp = syncMetadataService.getDeltaSyncMetadataFile(userToken,
                Long.parseLong(ownerBy),
                syncVersion,
                SynConstants.SYNC_METADATA_TEMP_FILE_PATH,
                response,
                isZip);
            
            if (null != rsp && StringUtils.isNotBlank(rsp.getMetadataSqlPath()))
            {
                endSyncVersion = String.valueOf(rsp.getCurrentSyncVersion());
                response.addHeader("x-hw-syncVersion", String.valueOf(rsp.getCurrentSyncVersion()));
                downLoadMetadataFile(rsp.getMetadataSqlPath(), outputStream, response);
            }
        }
        catch (RuntimeException t)
        {
            String[] logParams = new String[]{ownerBy, null};
            String keyword = "GET SYNC META DATA FAIL :" + syncVersion + ':' + endSyncVersion;
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_DELTA_SYNC_META_DATA_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    /**
     * 获取文件夹及其子文件夹的元数据
     * 
     * @param ownerBy
     * @param folderId
     * @param token
     * @param outputStream
     * @param response
     * @throws BaseRunException
     * @throws IOException
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/{ownerBy}/{folderId}", method = RequestMethod.GET)
    @ResponseBody
    public void getFolderMetadata(@PathVariable String ownerBy, @PathVariable String folderId, Boolean zip,
        @RequestHeader("Authorization") String token, OutputStream outputStream, HttpServletResponse response,
        HttpServletRequest request)
        throws BaseRunException, IOException
    {
        UserToken userToken = null;
        try
        {
            if (StringUtils.isBlank(ownerBy) || StringUtils.isBlank(folderId))
            {
                String message = "ownerBy or folderId is blank";
                throw new BadRequestException(message);
            }
            
            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), Long.parseLong(ownerBy));
            
            INode syncFolder = new INode();
            
            syncFolder.setOwnedBy(Long.parseLong(ownerBy));
            syncFolder.setId(Long.parseLong(folderId));
            boolean isZip = false;
            if (zip != null)
            {
                isZip = zip;
            }
            SyncVersionRsp rsp = null;
            try
            {
                Locks.SYNCMETADATA_LOCK.tryLock();
                rsp = metadataManager.getMetadataFile(userToken,
                    syncFolder,
                    SynConstants.SYNC_METADATA_TEMP_FILE_PATH,
                    response,
                    isZip);
            }
            finally
            {
                Locks.SYNCMETADATA_LOCK.unlock();
            }
            
            if (null != rsp && StringUtils.isNotBlank(rsp.getMetadataSqlPath()))
            {
                response.addHeader("x-hw-syncVersion", String.valueOf(rsp.getCurrentSyncVersion()));
                downLoadMetadataFile(rsp.getMetadataSqlPath(), outputStream, response);
            }
        }
        catch (RuntimeException t)
        {
            INode node = fileBaseService.getINodeInfo(Long.parseLong(ownerBy), Long.parseLong(folderId));
            String keyword = null;
            String parentId = null;
            if (node != null)
            {
                keyword = StringUtils.trimToEmpty(node.getName());
                parentId = String.valueOf(node.getParentId());
            }
            String[] logParams = new String[]{ownerBy, parentId};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_FOLDER_META_DATA_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    /**
     * 
     * @param filePath
     * @param outputStream
     * @param response
     * @throws IOException
     */
    private void downLoadMetadataFile(String filePath, OutputStream outputStream, HttpServletResponse response)
        throws IOException
    {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream; charset=utf-8");
        
        File file = new File(filePath);
        FileInputStream inputStream = null;
        
        try
        {
            inputStream = new FileInputStream(file);
            byte[] b = new byte[1024 * 64];
            int length = inputStream.read(b);
            while (length > 0)
            {
                outputStream.write(b, 0, length);
                length = inputStream.read(b);
            }
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            // 删除文件
            SyncFileUtils.deleteFile(filePath);
        }
        
    }
    
}