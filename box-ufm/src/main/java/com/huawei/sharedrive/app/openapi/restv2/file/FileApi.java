package com.huawei.sharedrive.app.openapi.restv2.file;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
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
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.*;
import com.huawei.sharedrive.app.files.service.lock.RestoreVersionLock;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.*;
import com.huawei.sharedrive.app.openapi.restv2.file.packer.ThumbnailUrlListPacker;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.service.INodeLinkApproveService;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.PropertiesUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文件API Rest接口, 提供文件预上传, 下载, 复制, 移动, 删除等操作
 * 
 * @author t90006461
 * @version V2 CloudStor CSE Service Platform Subproject, 2014-4-30
 * @see
 * @since
 */
@Controller
@RequestMapping(value = "/api/v2/files/{ownerId}")
@Api(description = "文件API Rest接口, 提供文件预上传, 下载, 复制, 移动, 删除等操作")
public class FileApi
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileApi.class);
    
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private RecentBrowseService recentBrowseService;
    
    @Autowired
    private INodeLinkApproveService linkApproveService;

    @Autowired
    private NodeUpdateService nodeUpdateService;

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
    
    @Autowired
    UserStatisticsService userStatisticsService;
    
    @RequestMapping(value = "/{fileId}/copy", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> copyFile(HttpServletRequest request, @PathVariable Long ownerId,
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
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            }else {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            }

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            userTokenHelper.checkUserStatus(userToken.getAppId(), copyRequest.getDestOwnerId());
            
            // 空间及文件数校验
            //fileBaseService.checkSpaceAndFileCount(copyRequest.getDestOwnerId(), userToken.getAccountId());
            long fileSize = fileServiceV2.getFileInfo(userToken, ownerId, fileId).getSize();
            fileBaseService.checkSpaceAndFileCount(ownerId, userToken.getAccountId(), fileSize);
            fillLinkUser(request, copyRequest, userToken);
            
            securityMatrixService.checkSecurityMatrix(userToken,
                ownerId,
                fileId,
                copyRequest.getDestOwnerId(),
                SecurityMethod.NODE_COPY,
                headerCustomMap);
            boolean isAutoRename = copyRequest.isAutoRename();
            INode srcNode = null;
            
            destParent = new INode(copyRequest.getDestOwnerId(), copyRequest.getDestParent());
            
            srcNode = fileBaseService.getINodeInfo(ownerId, fileId);
            if (srcNode == null)
            {
                throw new NoSuchSourceException();
            }
            INode resultNode;
            String srcName = srcNode.getName();
            if(copyRequest.getLink()!=null){
            	INodeLink link=new INodeLink();
				link.setId(copyRequest.getLink().getLinkCode());
				link.setPlainAccessCode(copyRequest.getLink().getPlainAccessCode());
                resultNode = doCopy(isAutoRename, userToken, srcNode, destParent, srcName,link);
            }else{
            	resultNode = doCopy(isAutoRename, userToken, srcNode, destParent, srcName);
            }
            
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
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        INode file = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            
            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            file = new INode();
            file.setId(fileId);
            file.setOwnedBy(ownerId);
            file = fileBaseService.getAndCheckNodeForDeleteFile(file.getOwnedBy(), file.getId());
            
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.NODE_DELETE, headerCustomMap);
            
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
        @RequestBody NodeMoveRequest theRequest, @RequestHeader("Authorization") String token,
        HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        INode srcNode = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            theRequest.checkParameter();
            
            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            userTokenHelper.checkUserStatus(userToken.getAppId(), theRequest.getDestOwnerId());
            
            boolean isAutoRename = theRequest.isAutoRename();
            srcNode = new INode(ownerId, fileId);
            INode destNode = new INode(theRequest.getDestOwnerId(), theRequest.getDestParent());
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
            recentBrowseService.createByNode(userToken, resultNode);
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
                String.valueOf(theRequest.getDestOwnerId() != null ? theRequest.getDestOwnerId() : ""),
                String.valueOf(theRequest.getDestParent() != null ? theRequest.getDestParent() : "")};
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
	public ResponseEntity<?> preUploadFile(@RequestBody FilePreUploadRequest request, @PathVariable Long ownerId, @RequestHeader("Authorization") String token, HttpServletRequest requestServlet) throws BaseRunException {
		INode fileNode = null;
		UserToken userToken = null;
		try {
			// 参数校验
			FilesCommonUtils.checkNonNegativeIntegers(ownerId);
			request.checkParamter();

			if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
				String date = requestServlet.getHeader("Date");
				userToken = userTokenHelper.getLinkToken(token, date);
				userTokenHelper.assembleUserToken(ownerId, userToken);
			} else {
				Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
				userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			}

			// 用户状态校验
			userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

			// 安全矩阵校验
			String xUserToken = requestServlet.getHeader("x-usertoken");
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
			if (StringUtils.isNotEmpty(xUserToken)) {
				UserToken accessUserToken = userTokenHelper.checkTokenAndGetUserForV2(xUserToken, headerCustomMap);
				securityMatrixService.checkSecurityMatrix(accessUserToken, ownerId, null, SecurityMethod.FILE_UPLOAD, headerCustomMap);
			} else {
				securityMatrixService.checkSecurityMatrix(userToken, ownerId, null, SecurityMethod.FILE_UPLOAD, headerCustomMap);
			}

			// 空间和文件数校验
			fileBaseService.checkSpaceAndFileCount(ownerId, userToken.getAccountId());

			fileBaseService.checkSpaceAndFileCount(ownerId, userToken.getAccountId(), request.getSize());

			fileNode = request.transToINode();
			fileNode.setOwnedBy(ownerId);
			boolean doUploadNearest = enalbeUploadNearest(requestServlet);
			LOGGER.info("preUploadFile path  {}", request.getPath());
			if (request.getPath() != null && !"".equals(request.getPath())) {
				try {
					String[] filePaths = request.getPath().substring(0, request.getPath().lastIndexOf("/")).split("/");
					INode tempParantNode = fileNode;
					tempParantNode.setId(fileNode.getParentId());
					for (int i = 0; i < filePaths.length; i++) {
						if ("".equals(filePaths[i])) {
							continue;
						}
						INode subiNode = folderService.getSubFolderByName(tempParantNode, filePaths[i]);
						if (subiNode != null) {
							if (subiNode.getStatus() != INode.STATUS_NORMAL) {
								subiNode.setStatus(INode.STATUS_NORMAL);
								nodeUpdateService.updateINodeStatus(subiNode);
							}
							tempParantNode = subiNode;
						} else {
							INode iNode = new INode();
							iNode.setParentId(tempParantNode.getId());
							if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
								if (request.getCreatedBy() != null) {
									iNode.setCreatedBy(request.getCreatedBy());
									iNode.setModifiedBy(request.getCreatedBy());
								}
							} else {
								iNode.setCreatedBy(userToken.getId());
								iNode.setModifiedBy(userToken.getId());
							}
							iNode.setName(filePaths[i]);
							iNode.setOwnedBy(ownerId);
							iNode.setCreatedAt(new Date());
							iNode.setModifiedAt(new Date());
							iNode.setType(INode.TYPE_FOLDER);
							INode newinode = folderService.createFolder(userToken, iNode);
							tempParantNode = newinode;
						}
					}
					fileNode.setParentId(tempParantNode.getId());
				} catch (Exception e) {
					LOGGER.error("preUploadFile createPath fail {}", e.getMessage());
				}
			}

			FilePreUploadResponseV1 rsp = fileService.preUploadFile(userToken, fileNode, request, doUploadNearest, ownerId);
			ResponseEntity<?> response = null;
			HttpHeaders header = new HttpHeaders();
			header.set("Connection", "close");

			LOGGER.info("preUploadFile result [{}, {}]", rsp.getFileId(), rsp.getUrl());

			// 闪传
			if (null != rsp.getFile()) {
				RestFileInfo restFileInfo = rsp.getFile();
				ThumbnailUrlListPacker.transThumbnailUrlList(restFileInfo);
				response = new ResponseEntity<RestFileInfo>(restFileInfo, header, HttpStatus.CREATED);
			} else {
				FilePreUploadResponse responseV2 = transToV2(rsp);
				response = new ResponseEntity<FilePreUploadResponse>(responseV2, header, HttpStatus.OK);
			}
			// 刷新数据库
			userStatisticsService.RefreshStatisticsInfoAndCache(ownerId, request.getSize(), 1);
			return response;
		} catch (RuntimeException t) {
			String keyword = null;
			String parentId = null;
			if (fileNode != null) {
				keyword = fileNode.getName();
				parentId = String.valueOf(fileNode.getParentId());
			}
			String[] logParams = new String[] { String.valueOf(ownerId), parentId };
			fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.UPLOADURL_PROVIDE_ERR, logParams, keyword);
			throw t;
		}
	}
    
    @RequestMapping(value = "/{fileId}/refreshurl", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<RefreshUploadUrlResponse> refreshUploadUrl(@PathVariable Long ownerId,
        @PathVariable Long fileId, @RequestBody RefreshUploadUrlRquest request,
        @RequestHeader("Authorization") String token, HttpServletRequest requestServlet) throws BaseRunException
    {
        UserToken userToken = null;
        INode node = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            request.checkParameter();
            
            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            securityMatrixService.checkSecurityMatrix(userToken, ownerId,
                fileId, SecurityMethod.FILE_UPLOAD, headerCustomMap);
            
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
            recentBrowseService.createByNode(userToken, node);
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
        @RequestHeader("Authorization") String token, HttpServletRequest requestServlet) throws BaseRunException
    {
        UserToken userToken = null;
        INode node = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            request.checkParameter();
            
            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId,
                SecurityMethod.NODE_RENAME, headerCustomMap);
            
            node = fileBaseService.getAndCheckNode(ownerId, fileId, INode.TYPE_FILE);
            
            node = nodeService.renameAndSetSyncStatus(userToken,
                ownerId,
                node,
                request.getName(),
                request.getSyncStatus(),
                INode.TYPE_FILE);
            RestFileInfo fileInfo = new RestFileInfo(node, userToken.getDeviceType());
            recentBrowseService.createByNode(userToken, node);
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
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
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
                headerCustomMap);
            
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
            recentBrowseService.createByNode(userToken, resultNode);
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
    
    private INode doCopy(boolean isAutoRename, UserToken userToken, INode srcNode, INode destParent,
            String srcName,INodeLink link) throws FilesNameConflictException
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
                        false,link);
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


    private boolean enalbeUploadNearest(HttpServletRequest request)
    {
        if (StringUtils.equalsIgnoreCase(PropertiesUtils.getProperty("upload.nearest.header.support", "false", PropertiesUtils.BundleName.HWIT)
            , "true"))
        {
            if(StringUtils.equalsIgnoreCase(request.getHeader("x-uplaod-nearest"), "true"))
            {
                return true;
            }
            if(StringUtils.equalsIgnoreCase(request.getHeader("x-upload-nearest"), "true"))
            {
                return true;
            }
        }
        return false;
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
    
    
    private FilePreUploadResponse transToV2(FilePreUploadResponseV1 responseV1)
    {
        FilePreUploadResponse response = new FilePreUploadResponse();
        response.setFileId(responseV1.getFileId());
        response.setUploadUrl(responseV1.getUrl());
        return response;
    }
    
}
