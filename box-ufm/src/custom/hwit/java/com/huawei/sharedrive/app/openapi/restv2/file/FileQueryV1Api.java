package com.huawei.sharedrive.app.openapi.restv2.file;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.service.impl.ACLManager;
import com.huawei.sharedrive.app.core.domain.Limit;
import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.FileScanningException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.InvalidFileTypeException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchFileException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.exception.ScannedForbiddenException;
import com.huawei.sharedrive.app.exception.SecurityMatixException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FileService;
import com.huawei.sharedrive.app.files.service.FileServiceV2;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.PreviewResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileVersionInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestVersionLists;
import com.huawei.sharedrive.app.openapi.restv2.file.packer.ThumbnailUrlListPacker;
import com.huawei.sharedrive.app.plugins.preview.manager.FilePreviewManager;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityStatus;
import com.huawei.sharedrive.app.plugins.scan.manager.SecurityScanManager;
import com.huawei.sharedrive.app.security.domain.CheckEngine;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

/**
 * 文件API Rest接口, 提供文件预上传, 下载, 复制, 移动, 删除等操作
 * 
 * @author t90006461
 * @version V2 CloudStor CSE Service Platform Subproject, 2014-4-30
 * @see
 * @since
 */
@Controller
@RequestMapping(value = "/api/v1/files/{ownerId}")
public class FileQueryV1Api
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileQueryV1Api.class);
    
    @Autowired
    private ACLManager aclManager;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private FilePreviewManager filePreviewManager;
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private FileServiceV2 fileServiceV2;
    
    @Autowired
    private SecurityMatrixService securityMatrixService;
    
    @Autowired
    private SecurityScanManager securityScanManager;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    private static final boolean IGNORE_SCAN_RESULT = Boolean.parseBoolean(PropertiesUtils.getProperty("security.scan.ignore.result",
        "true"));
    
    /**
     * 下载文件内容
     * 
     * @param ownerId
     * @param fileId
     * @param objectId
     * @return
     */
    @RequestMapping(value = "/{fileId}/{objectId}/contents", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getDownloadObj(@PathVariable Long ownerId, @PathVariable Long fileId,
        @PathVariable String objectId, @RequestHeader("Authorization") String token) throws BaseRunException
    {
        if (null == ownerId)
        {
            throw new InvalidParamException("null ownerId.");
        }
        if (fileId == null)
        {
            throw new InvalidParamException("null fileId.");
        }
        UserToken userToken = null;
        INode node = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            
            // Token 验证
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            // 安全矩阵
            securityMatrixService.checkSecurityMatrix(userToken,
                ownerId,
                fileId,
                SecurityMethod.FILE_DOWNLOAD,
                null);
            
            node = fileBaseService.getINodeInfoCheckStatus(ownerId, fileId, INode.STATUS_NORMAL);
            
            if (!StringUtils.equals(node.getObjectId(), objectId))
            {
                LOGGER.error("File object id not match [{}, {}]", node.getObjectId(), objectId);
                throw new NoSuchFileException("The file is not exist");
            }
            
            // KIA扫描
            sendDownloadScanTask(node);
            
            String downLoadUrl = fileService.getFileDownloadUrl(userToken, ownerId, fileId, objectId);
            
            // 设置重定向到数据中心
            HttpHeaders header = new HttpHeaders();
            header.set("Connection", "close");
            header.set("Location", downLoadUrl);
            
            // 返回307响应码
            return new ResponseEntity<String>(null, header, HttpStatus.TEMPORARY_REDIRECT);
        }
        catch (RuntimeException tw)
        {
            sendLogEvent(ownerId, userToken, node);
            throw tw;
        }
    }
    
    /**
     * 获取文件下载地址
     * 
     * @param ownerId
     * @param fileId
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{fileId}/url", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getDownloadUrl(@PathVariable Long ownerId,
        @PathVariable Long fileId, @RequestHeader("Authorization") String token, HttpServletRequest request)
        throws BaseRunException
    {
        UserToken userToken = null;
        INode node = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            if (token.startsWith(UserTokenHelper.LINK_PREFIX))
            {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            }
            else
            {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            }
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            node = getAndCheckNode(userToken, ownerId, fileId);
            
            String xUserToken = request.getHeader("x-usertoken");
            if (StringUtils.isNotEmpty(xUserToken))
            {
                UserToken accessUserToken = userTokenHelper.checkTokenAndGetUserForV2(xUserToken, null);
                securityMatrixService.checkSecurityMatrix(accessUserToken,
                    ownerId,
                    fileId,
                    SecurityMethod.FILE_DOWNLOAD,
                    null);
            }
            else
            {
                securityMatrixService.checkSecurityMatrix(userToken,
                    ownerId,
                    fileId,
                    SecurityMethod.FILE_DOWNLOAD,
                    null);
            }
            
            HttpHeaders header = new HttpHeaders();
            header.set("Connection", "close");
            
            // KIA扫描
            sendDownloadScanTask(node);
            
            String downloadUrl = fileService.getFileDownloadUrl(userToken, node, header);
            return new ResponseEntity<String>(downloadUrl, header, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            sendLogEvent(ownerId, userToken, node);
            throw t;
        }
    }
    
    /**
     * 获取文件详情
     * 
     * @param ownerId
     * @param fileId
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{fileId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getFileInfo(@PathVariable Long ownerId, @PathVariable Long fileId,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        INode node = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            
            if (token.startsWith(UserTokenHelper.LINK_PREFIX))
            {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            }
            else
            {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            }
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            try
            {
                securityMatrixService.checkSecurityMatrix(userToken,
                    ownerId,
                    fileId,
                    SecurityMethod.NODE_INFO,
                    null);
            }
            catch (NoSuchItemsException e)
            {
                throw new NoSuchFileException(e);
            }
            node = fileServiceV2.getFileInfo(userToken, ownerId, fileId);
            
            if (INode.TYPE_FILE == node.getType())
            {
                FilesCommonUtils.setNodeVersionsForV2(node);
                RestFileInfo fileInfo = new RestFileInfo(node, userToken.getDeviceType());
                ThumbnailUrlListPacker.transThumbnailUrlList(fileInfo);
                return new ResponseEntity<RestFileInfo>(fileInfo, HttpStatus.OK);
            }
            RestFileVersionInfo versionInfo = new RestFileVersionInfo(node, userToken.getDeviceType());
            return new ResponseEntity<RestFileVersionInfo>(versionInfo, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String[] logParams = new String[]{String.valueOf(ownerId), null};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_FILE_ERR,
                logParams,
                null);
            throw t;
        }
    }
    
    @RequestMapping(value = "/{fileId}/swfUrl", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<PreviewResponse> getPreviewUrl(@PathVariable Long ownerId,
        @PathVariable Long fileId, @RequestHeader("Authorization") String token, HttpServletRequest request)
        throws BaseRunException
    {
        UserToken userToken = null;
        INode node = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            if (token.startsWith(UserTokenHelper.LINK_PREFIX))
            {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            }
            else
            {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            }
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            node = getAndCheckNode(userToken, ownerId, fileId);
            
            // 安全矩阵
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.FILE_PREVIEW, null);
            
            HttpHeaders header = new HttpHeaders();
            header.set("Connection", "close");
            
            // KIA扫描
            sendDownloadScanTask(node);
            
            String downloadUrl = filePreviewManager.getPreviewObjectDownloadUrl(userToken, node);
            PreviewResponse response = new PreviewResponse(downloadUrl);
            return new ResponseEntity<>(response, header, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            sendLogEvent(ownerId, userToken, node);
            throw t;
        }
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/{fileId}/thumbUrl", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ThumbnailUrl> getThumbnailUrl(@PathVariable Long ownerId,
        @PathVariable Long fileId, @RequestParam Integer width, @RequestParam Integer height,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        INode node = new INode(ownerId, fileId);
        UserToken userToken = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            
            Thumbnail thumbnail = new Thumbnail(width, height);
            thumbnail.checkParameter();
            if (token.startsWith(UserTokenHelper.LINK_PREFIX))
            {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            }
            else
            {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            }
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            INode file = fileBaseService.getAndCheckNode(ownerId, fileId, INode.TYPE_FILE);
            
            if (!judgeBySecMatrix(userToken, file))
            {
                throw new ForbiddenException();
            }
            
            // 文件类型校验
            if (!FilesCommonUtils.isImage(file.getName()))
            {
                throw new InvalidFileTypeException();
            }
            ThumbnailUrl thumbnailUrl = fileServiceV2.getThumbnailUrl(userToken, file, thumbnail);
            
            return new ResponseEntity<ThumbnailUrl>(thumbnailUrl, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String keyword = null;
            String parentId = null;
            
            keyword = StringUtils.trimToEmpty(node.getName());
            parentId = String.valueOf(node.getParentId());
            
            String[] logParams = new String[]{String.valueOf(ownerId), String.valueOf(parentId)};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_THUMBNAIL_URL_FILE_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    /**
     * 列举文件版本
     * 
     * @param ownerId
     * @param fileId
     * @param offset
     * @param limit
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{fileId}/versions", method = RequestMethod.GET)
    @ResponseBody
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public ResponseEntity<RestVersionLists> listVersion(@PathVariable Long ownerId,
        @PathVariable Long fileId, @RequestParam(required = false) Long offset,
        @RequestParam(required = false) Integer limit, @RequestHeader("Authorization") String token)
        throws BaseRunException
    {
        UserToken userToken = null;
        INode node = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            
            Limit limitObj = new Limit(offset, limit);
            limitObj.checkParameter();
            
            // Token 验证
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.FOLDER_LIST, null);
            INode fileNode = new INode();
            fileNode.setOwnedBy(ownerId);
            fileNode.setId(fileId);
            node = fileBaseService.getINodeInfo(ownerId, fileId);
            
            FileINodesList versionList = fileService.getFileVersionLists(userToken, fileNode, limitObj);
            
            return new ResponseEntity<RestVersionLists>(new RestVersionLists(versionList,
                userToken.getDeviceType()), HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String keyword = null;
            String parentId = null;
            if (node != null)
            {
                keyword = node.getName();
                parentId = String.valueOf(node.getParentId());
            }
            String[] logParams = new String[]{String.valueOf(ownerId), parentId};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.LIST_VERSION_FILE_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    private boolean checkThumbnailAcl(UserToken user, INode node)
    {
        boolean addThumbnailUrl = false;
        if (user.getId() == node.getOwnedBy())
        {
            addThumbnailUrl = true;
        }
        else
        {
            ACL nodeACL = null;
            if (StringUtils.isNotBlank(user.getLinkCode()))
            {
                nodeACL = aclManager.getACLForLink(user.getLinkCode(), node);
            }
            else
            {
                nodeACL = aclManager.getACLForAccessUser(user.getId(), INodeACL.TYPE_USER, node);
            }
            if (nodeACL.isDownload() || nodeACL.isPreview())
            {
                addThumbnailUrl = true;
            }
        }
        return addThumbnailUrl;
    }
    
    private INode getAndCheckNode(UserToken user, Long ownerId, Long fileId) throws BaseRunException
    {
        if (user == null || ownerId == null || fileId == null)
        {
            throw new InvalidParamException();
        }
        INode node = fileBaseService.getINodeInfo(ownerId, fileId);
        if (null == node)
        {
            String message = "File not exist, owner id:" + ownerId + ", file id:" + fileId;
            throw new NoSuchFileException(message);
        }
        
        if (INode.STATUS_NORMAL != node.getStatus())
        {
            String message = "File status abnormal, owner id:" + ownerId + ", file id:" + fileId
                + ", status:" + node.getStatus();
            throw new NoSuchFileException(message);
        }
        
        if (node.getType() != INode.TYPE_FILE && node.getType() != INode.TYPE_VERSION)
        {
            String message = "File type invalid, owner id:" + ownerId + ", file id:" + fileId + ",type:"
                + node.getType();
            throw new NoSuchFileException(message);
        }
        return node;
    }
    
    private boolean judgeBySecMatrix(UserToken user, INode node)
    {
        boolean addThumbnailUrl = checkThumbnailAcl(user, node);
        try
        {
            securityMatrixService.checkSecurityMatrix(user,
                node.getOwnedBy(),
                node.getId(),
                SecurityMethod.FILE_DOWNLOAD,
                null);
        }
        catch (SecurityMatixException e)
        {
            try
            {
                securityMatrixService.checkSecurityMatrix(user,
                    node.getOwnedBy(),
                    node.getId(),
                    SecurityMethod.FILE_PREVIEW,
                    null);
            }
            catch (SecurityMatixException e1)
            {
                addThumbnailUrl = false;
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("When checkSecurityMatrix download", e);
        }
        return addThumbnailUrl;
    }
    
    private void sendDownloadScanTask(INode node)
    {
        CheckEngine engine = securityMatrixService.getCheckEngine();
        
        LOGGER.info("Security matrix check engine: {}", engine);
        
        if (CheckEngine.HUAWEI == engine)
        {
            return;
        }
        
        SecurityStatus status = securityScanManager.sendScanTask(node, SecurityScanTask.PRIORITY_HIGH);
        LOGGER.info("status: {} ; ignoreScanResult: {}", status, IGNORE_SCAN_RESULT);
        
        if (status == null || IGNORE_SCAN_RESULT)
        {
            return;
        }
        switch (status)
        {
            case KIA_UMCOMPLETED:
                throw new FileScanningException("File is not ready");
            case KIA_COMPLETED_INSECURE:
                throw new ScannedForbiddenException("This file is not allowed to be downloaded");
            default:
                break;
        }
    }
    
    private void sendLogEvent(Long ownerId, UserToken userToken, INode node)
    {
        String keyword = null;
        String parentId = null;
        if (node != null)
        {
            keyword = StringUtils.trimToEmpty(node.getName());
            parentId = String.valueOf(node.getParentId());
        }
        String[] logParams = new String[]{String.valueOf(ownerId), parentId};
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.DOWN_URL_FILE_ERR,
            logParams,
            keyword);
    }
    
}
