package com.huawei.sharedrive.app.openapi.restv2.file;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.FilesNameConflictException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.NoSuchFileException;
import com.huawei.sharedrive.app.exception.NoSuchSourceException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FileService;
import com.huawei.sharedrive.app.files.service.FileServiceV2;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.files.service.NodeService;
import com.huawei.sharedrive.app.files.service.lock.RestoreVersionLock;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadRequest;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponseV1;
import com.huawei.sharedrive.app.openapi.domain.node.NodeCopyRequest;
import com.huawei.sharedrive.app.openapi.domain.node.NodeMoveRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RefreshUploadUrlResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RefreshUploadUrlRquest;
import com.huawei.sharedrive.app.openapi.domain.node.RenameAndSetSyncRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.openapi.restv2.file.packer.ThumbnailUrlListPacker;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

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
public class FileV1Api
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileV1Api.class);
    
    
    @Autowired
    private FileBaseService fileBaseService;
    
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private FileServiceV2 fileServiceV2;
    
    @Autowired
    private FolderService folderService;
    
    @Autowired
    private NodeService nodeService;
    
    @Autowired
    private SecurityMatrixService securityMatrixService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @RequestMapping(value = "/{fileId}/copy", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<RestFileInfo> copyFile(HttpServletRequest request, @PathVariable Long ownerId,
        @PathVariable Long fileId, @RequestBody NodeCopyRequest copyRequest,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        UserToken userToken = null;
        INode srcNodeForLog = null;
        INode destParent = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            copyRequest.checkParameter();
            
            // Token 验证
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            userTokenHelper.checkUserStatus(userToken.getAppId(), copyRequest.getDestOwnerId());
            
            // 空间及文件数校验
            fileBaseService.checkSpaceAndFileCount(copyRequest.getDestOwnerId(), userToken.getAccountId());
            fillLinkUser(request, copyRequest, userToken);
            
            securityMatrixService.checkSecurityMatrix(userToken,
                ownerId,
                fileId,
                copyRequest.getDestOwnerId(),
                SecurityMethod.NODE_COPY,
                null);
            boolean isAutoRename = copyRequest.isAutoRename();
            INode srcNode = null;
            
            destParent = new INode(copyRequest.getDestOwnerId(), copyRequest.getDestParent());
            
            srcNode = fileBaseService.getINodeInfo(ownerId, fileId);
            if (srcNode == null)
            {
                throw new NoSuchSourceException();
            }
            
            String srcName = srcNode.getName();
            INode resultNode = doCopy(isAutoRename, userToken, srcNode, destParent, srcName);
            
            FilesCommonUtils.setNodeVersionsForV2(resultNode);
            
            RestFileInfo fileInfo = new RestFileInfo(resultNode, userToken.getDeviceType());
            ThumbnailUrlListPacker.transThumbnailUrlList(fileInfo);
            return new ResponseEntity<RestFileInfo>(fileInfo, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String keyword = null;
            srcNodeForLog = fileBaseService.getINodeInfo(ownerId, fileId);
            if (srcNodeForLog != null)
            {
                keyword = StringUtils.trimToEmpty(srcNodeForLog.getName());
            }
            String[] logParams = new String[]{String.valueOf(ownerId), String.valueOf(fileId),
                String.valueOf(copyRequest.getDestOwnerId() != null ? copyRequest.getDestOwnerId() : ""),
                String.valueOf(copyRequest.getDestParent() != null ? copyRequest.getDestParent() : "")};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.COPY_FILE_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    /**
     * 删除文件至回收站
     * 
     * @param ownerId 文件所有者ID
     * @param fileId 文件ID
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{fileId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteFile(@PathVariable Long ownerId, @PathVariable Long fileId,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        UserToken userToken = null;
        INode file = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            
            // Token 验证
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            file = new INode();
            file.setId(fileId);
            file.setOwnedBy(ownerId);
            file = fileBaseService.getAndCheckNodeForDeleteFile(file.getOwnedBy(), file.getId());
            
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.NODE_DELETE, null);
            
            nodeService.deleteNode(userToken, file);
            ResponseEntity<String> rsp = new ResponseEntity<String>(HttpStatus.OK);
            return rsp;
        }
        catch (RuntimeException t)
        {
            String keyword = null;
            String parentId = null;
            if (file != null)
            {
                keyword = StringUtils.trimToEmpty(file.getName());
                parentId = String.valueOf(file.getParentId());
            }
            String[] logParams = new String[]{String.valueOf(ownerId), parentId};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.DELETE_FILE_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    @RequestMapping(value = "/{fileId}/move", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<RestFileInfo> moveFile(@PathVariable Long ownerId, @PathVariable Long fileId,
        @RequestBody NodeMoveRequest request, @RequestHeader("Authorization") String token)
        throws BaseRunException
    {
        UserToken userToken = null;
        INode srcNode = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            request.checkParameter();
            
            // Token 验证
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            userTokenHelper.checkUserStatus(userToken.getAppId(), request.getDestOwnerId());
            
            boolean isAutoRename = request.isAutoRename();
            srcNode = new INode(ownerId, fileId);
            INode destNode = new INode(request.getDestOwnerId(), request.getDestParent());
            INode resultNode = null;
            if (isAutoRename)
            {
                srcNode = fileBaseService.getINodeInfo(ownerId, fileId);
                if (srcNode == null)
                {
                    throw new NoSuchSourceException();
                }
                
                String newName = srcNode.getName();
                int renameNumber = 1;
                while (true)
                {
                    try
                    {
                        resultNode = folderService.moveNodeToFolderCheckType(userToken,
                            srcNode,
                            destNode,
                            newName,
                            INode.TYPE_FILE);
                        break;
                    }
                    catch (FilesNameConflictException e)
                    {
                        newName = FilesCommonUtils.getNewName(INode.TYPE_FILE, newName, renameNumber);
                        renameNumber++;
                        continue;
                    }
                }
            }
            else
            {
                resultNode = folderService.moveNodeToFolderCheckType(userToken,
                    srcNode,
                    destNode,
                    null,
                    INode.TYPE_FILE);
            }
            
            FilesCommonUtils.setNodeVersionsForV2(resultNode);
            RestFileInfo fileInfo = new RestFileInfo(resultNode, userToken.getDeviceType());
            ThumbnailUrlListPacker.transThumbnailUrlList(fileInfo);
            return new ResponseEntity<RestFileInfo>(fileInfo, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String keyword = null;
            if (srcNode != null)
            {
                keyword = srcNode.getName();
            }
            String[] logParams = new String[]{String.valueOf(ownerId != null ? ownerId : ""),
                String.valueOf(fileId != null ? fileId : ""),
                String.valueOf(request.getDestOwnerId() != null ? request.getDestOwnerId() : ""),
                String.valueOf(request.getDestParent() != null ? request.getDestParent() : "")};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.MOVE_FILE_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    /**
     * 预上传
     * 
     * @param request
     * @param ownerId
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> preUploadFile(@RequestBody FilePreUploadRequest request,
        @PathVariable Long ownerId, @RequestHeader("Authorization") String token,
        HttpServletRequest requestServlet) throws BaseRunException
    {
        INode fileNode = null;
        UserToken userToken = null;
        try
        {
            // 参数校验
            FilesCommonUtils.checkNonNegativeIntegers(ownerId);
            request.checkParamter();
            
            if (token.startsWith(UserTokenHelper.LINK_PREFIX))
            {
                String date = requestServlet.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            }
            else
            {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            }
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            // 安全矩阵校验
            String xUserToken = requestServlet.getHeader("x-usertoken");
            if (StringUtils.isNotEmpty(xUserToken))
            {
                UserToken accessUserToken = userTokenHelper.checkTokenAndGetUserForV2(xUserToken, null);
                securityMatrixService.checkSecurityMatrix(accessUserToken,
                    ownerId,
                    null,
                    SecurityMethod.FILE_UPLOAD,
                    null);
            }
            else
            {
                securityMatrixService.checkSecurityMatrix(userToken,
                    ownerId,
                    null,
                    SecurityMethod.FILE_UPLOAD,
                    null);
            }
            
            // 空间和文件数校验
            fileBaseService.checkSpaceAndFileCount(ownerId, userToken.getAccountId());
            
            fileNode = request.transToINode();
            fileNode.setOwnedBy(ownerId);
            FilePreUploadResponseV1 rsp = fileService.preUploadFile(userToken, fileNode, request.getTokenTimeout(), false);
            
            ResponseEntity<?> response = null;
            HttpHeaders header = new HttpHeaders();
            header.set("Connection", "close");
            
            LOGGER.info("preUploadFile result [{}, {}]", rsp.getFileId(), rsp.getUrl());
            
            // 闪传
            if (null != rsp.getFile())
            {
                RestFileInfo restFileInfo = rsp.getFile();
                ThumbnailUrlListPacker.transThumbnailUrlList(restFileInfo);
                response = new ResponseEntity<RestFileInfo>(restFileInfo, header, HttpStatus.CREATED);
            }
            else
            {
                response = new ResponseEntity<FilePreUploadResponseV1>(rsp, header, HttpStatus.OK);
            }
            return response;
        }
        catch (RuntimeException t)
        {
            String keyword = null;
            String parentId = null;
            if (fileNode != null)
            {
                keyword = fileNode.getName();
                parentId = String.valueOf(fileNode.getParentId());
            }
            String[] logParams = new String[]{String.valueOf(ownerId), parentId};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.UPLOADURL_PROVIDE_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    @RequestMapping(value = "/{fileId}/refreshurl", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<RefreshUploadUrlResponse> refreshUploadUrl(@PathVariable Long ownerId,
        @PathVariable Long fileId, @RequestBody RefreshUploadUrlRquest request,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        UserToken userToken = null;
        INode node = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            request.checkParameter();
            
            // Token 验证
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.FILE_UPLOAD, null);
            
            String objectId = getObjectIdFromUploadUrl(request.getUploadUrl());
            
            List<INode> nodeList = fileBaseService.getNodeByObjectId(ownerId, objectId);
            if (CollectionUtils.isEmpty(nodeList))
            {
                throw new NoSuchFileException("No such file");
            }
            node = nodeList.get(0);
            
            if (INode.STATUS_CREATING != node.getStatus())
            {
                throw new NoSuchFileException("File status abnormal: " + node.getStatus());
            }
            
            String newUrl = fileServiceV2.refreshUploadUrl(userToken, node, request.getUploadUrl());
            HttpHeaders header = new HttpHeaders();
            header.set("Connection", "close");
            RefreshUploadUrlResponse response = new RefreshUploadUrlResponse(newUrl);
            return new ResponseEntity<RefreshUploadUrlResponse>(response, header, HttpStatus.OK);
        }
        catch (RuntimeException t)
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
                UserLogType.REFRESH_UPLOAD_URL_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    @RequestMapping(value = "/{fileId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<RestFileInfo> renameAndSetSyncStatus(@PathVariable Long ownerId,
        @PathVariable Long fileId, @RequestBody RenameAndSetSyncRequest request,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        UserToken userToken = null;
        INode node = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            request.checkParameter();
            
            // Token 验证
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.NODE_RENAME, null);
            
            node = fileBaseService.getAndCheckNode(ownerId, fileId, INode.TYPE_FILE);
            
            node = nodeService.renameAndSetSyncStatus(userToken,
                ownerId,
                node,
                request.getName(),
                request.getSyncStatus(),
                INode.TYPE_FILE);
            RestFileInfo fileInfo = new RestFileInfo(node, userToken.getDeviceType());
            return new ResponseEntity<RestFileInfo>(fileInfo, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String parentId = node != null ? String.valueOf(node.getParentId()) : null;
            String syncStatus = node != null ? String.valueOf(node.getSyncStatus()) : null;
            String[] logParams = new String[]{String.valueOf(ownerId), parentId, syncStatus};
            String keyword = StringUtils.trimToEmpty(request.getName());
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.UPDATE_FILE_NAME_SYNC_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    @RequestMapping(value = "/{versionId}/restore", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<RestFileInfo> restoreVersion(HttpServletRequest request,
        @PathVariable Long ownerId, @PathVariable Long versionId, @RequestHeader("Authorization") String token)
        throws BaseRunException
    {
        UserToken userToken = null;
        INode srcNodeForLog = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, versionId);
            // Token 验证
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            // 空间及文件数校验
            fileBaseService.checkSpaceAndFileCount(ownerId, userToken.getAccountId());
            
            // 检查安全矩阵
            securityMatrixService.checkSecurityMatrix(userToken,
                ownerId,
                versionId,
                ownerId,
                SecurityMethod.NODE_COPY,
                null);
            
            INode versionNode = fileBaseService.getAndCheckNode(ownerId, versionId, INode.TYPE_VERSION);
            
            INode resultNode = null;
            
            try
            {
                RestoreVersionLock.lock(ownerId);
                resultNode = fileServiceV2.restoreFileVersion(userToken, versionNode);
            }
            finally
            {
                RestoreVersionLock.unlock(ownerId);
            }
            
            FilesCommonUtils.setNodeVersionsForV2(resultNode);
            RestFileInfo fileInfo = new RestFileInfo(resultNode, userToken.getDeviceType());
            ThumbnailUrlListPacker.transThumbnailUrlList(fileInfo);
            return new ResponseEntity<RestFileInfo>(fileInfo, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String keyword = null;
            srcNodeForLog = fileBaseService.getAndCheckNode(ownerId, versionId, INode.TYPE_VERSION);
            if (srcNodeForLog != null)
            {
                keyword = StringUtils.trimToEmpty(srcNodeForLog.getName());
            }
            String[] logParams = new String[]{String.valueOf(ownerId != null ? ownerId : ""),
                String.valueOf(versionId != null ? versionId : "")};
            
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.RESTORE_FILE_VERSION_ERR,
                logParams,
                keyword);
            throw t;
        }
        
    }
    
    private INode doCopy(boolean isAutoRename, UserToken userToken, INode srcNode, INode destParent,
        String srcName) throws FilesNameConflictException
    {
        INode resultNode = null;
        String newName = srcName;
        int renameNumber = 1;
        do
        {
            try
            {
                resultNode = folderService.copyNodeToFolderCheckType(userToken,
                    srcNode,
                    destParent,
                    newName,
                    INode.TYPE_FILE,
                    false);
                return resultNode;
            }
            catch (FilesNameConflictException e)
            {
                if (isAutoRename)
                {
                    newName = FilesCommonUtils.getNewName(INode.TYPE_FILE, srcName, renameNumber);
                    renameNumber++;
                    continue;
                }
                throw e;
            }
        } while (true);
    }
    
    /**
     * 填充外链用户的身份信息
     * 
     * @param restReq
     * @param userToken
     * @throws BaseRunException
     */
    private void fillLinkUser(HttpServletRequest request, NodeCopyRequest restReq, UserToken userToken)
        throws BaseRunException
    {
        if (null != restReq.getLink())
        {
            userToken.setLinkCode(restReq.getLink().getLinkCode());
            if (StringUtils.isNotEmpty(restReq.getLink().getLinkCode()))
            {
                INodeLink nodeLink = null;
                String authorization = UserTokenHelper.LINK_PREFIX + restReq.getLink().getLinkCode() + ','
                    + restReq.getLink().getPlainAccessCode();
                try
                {
                    String dateStr = request.getHeader("Date");
                    nodeLink = userTokenHelper.checkLinkToken(authorization, dateStr);
                    userToken.setPlainAccessCode(nodeLink.getPlainAccessCode());
                    userToken.setDate(request.getHeader("Date"));
                }
                catch (Exception e)
                
                {
                    throw new ForbiddenException(e);
                }
            }
        }
    }
    
    private String getObjectIdFromUploadUrl(String uploadUrl)
    {
        String objectId = uploadUrl.substring(uploadUrl.lastIndexOf('/') + 1);
        return objectId;
    }
    
}
