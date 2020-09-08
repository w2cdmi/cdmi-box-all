package com.huawei.sharedrive.app.openapi.restv2.file;

import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.service.impl.ACLManager;
import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FileService;
import com.huawei.sharedrive.app.files.service.FileServiceV2;
import com.huawei.sharedrive.app.files.service.RecentBrowseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.*;
import com.huawei.sharedrive.app.openapi.restv2.file.packer.ThumbnailUrlListPacker;
import com.huawei.sharedrive.app.plugins.preview.manager.FilePreviewManager;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityStatus;
import com.huawei.sharedrive.app.plugins.scan.manager.SecurityCheckManager;
import com.huawei.sharedrive.app.plugins.scan.manager.SecurityScanManager;
import com.huawei.sharedrive.app.security.domain.CheckEngine;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.PropertiesUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.box.app.convertservice.domain.ImgObject;
import pw.cdmi.box.app.convertservice.domain.TaskBean;
import pw.cdmi.box.app.ufm.utils.Constants;
import pw.cdmi.box.domain.Limit;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
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
public class FileQueryApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileQueryApi.class);

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
    private SecurityCheckManager securityCheckManager;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private UserService userService;

    @Autowired
    private RecentBrowseService recentBrowseService;

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
    @ApiOperation(value = "下载文件内容")
    @ResponseBody
    public ResponseEntity<String> getDownloadObj(@PathVariable Long ownerId, @PathVariable Long fileId,
                                                 @PathVariable String objectId, @RequestHeader("Authorization") String token, HttpServletRequest request)
            throws BaseRunException {
        if (null == ownerId) {
            throw new InvalidParamException("null ownerId.");
        }
        if (fileId == null) {
            throw new InvalidParamException("null fileId.");
        }
        UserToken userToken = null;
        INode node = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);

            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            // 安全矩阵
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.FILE_DOWNLOAD,
                    headerCustomMap);

            node = fileBaseService.getINodeInfoCheckStatus(ownerId, fileId, INode.STATUS_NORMAL);

            if (!StringUtils.equals(node.getObjectId(), objectId)) {
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
        } catch (RuntimeException tw) {
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
    @ApiOperation(value = "获取文件下载地址")
    @ResponseBody
    public ResponseEntity<DownloadResponse> getDownloadUrl(@PathVariable Long ownerId, @PathVariable Long fileId,
                                                           @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        INode node = null;
        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            } else {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            }

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            node = getAndCheckNode(userToken, ownerId, fileId);

            String xUserToken = request.getHeader("x-usertoken");
            if (StringUtils.isNotEmpty(xUserToken)) {
                UserToken accessUserToken = userTokenHelper.checkTokenAndGetUserForV2(xUserToken, headerCustomMap);
                securityMatrixService.checkSecurityMatrix(accessUserToken, ownerId, fileId,
                        SecurityMethod.FILE_DOWNLOAD, headerCustomMap);
            } else {
                securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.FILE_DOWNLOAD,
                        headerCustomMap);
            }

            HttpHeaders header = new HttpHeaders();
            header.set("Connection", "close");

            // 安全扫描及判断
            // 下载：本人可下载自己上传的有毒文件，非本人不可以
            securityCheckManager.checkSecurityStatus(node, true, (ownerId != node.getOwnedBy()));
            String downloadUrl = fileService.getFileDownloadUrl(userToken, node, header);
            DownloadResponse response = new DownloadResponse(downloadUrl);
            return new ResponseEntity<DownloadResponse>(response, header, HttpStatus.OK);
        } catch (RuntimeException t) {
            sendLogEvent(ownerId, userToken, node);
            throw t;
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
	@RequestMapping(value = "/{fileId}/urlWx", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<DownloadResponse> getWxDownloadUrl(@PathVariable Long ownerId, @PathVariable Long fileId,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		UserToken userToken = null;
		INode node = null;
		Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
		try {
			FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
			if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
				String date = request.getHeader("Date");
				userToken = userTokenHelper.getLinkToken(token, date);
				userTokenHelper.assembleUserToken(ownerId, userToken);
			} else {
				userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			}

			// 用户状态校验
			userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
			userToken.setCloudUserId(ownerId);
			userToken.setAccountId(0L);
			userToken.setId(ownerId);
			node = getAndCheckNode(userToken, ownerId, fileId);

			String xUserToken = request.getHeader("x-usertoken");
			if (StringUtils.isNotEmpty(xUserToken)) {
				UserToken accessUserToken = userTokenHelper.checkTokenAndGetUserForV2(xUserToken, headerCustomMap);
				securityMatrixService.checkSecurityMatrix(accessUserToken, ownerId, fileId,
						SecurityMethod.FILE_DOWNLOAD, headerCustomMap);
			} else {
				securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.FILE_DOWNLOAD,
						headerCustomMap);
			}

			HttpHeaders header = new HttpHeaders();
			header.set("Connection", "close");

            // 安全扫描及判断
            // 下载：本人可下载自己上传的有毒文件，非本人不可以
            securityCheckManager.checkSecurityStatus(node, true, (ownerId != node.getOwnedBy()));
			String downloadUrl = fileService.getFileDownloadUrl(userToken, node, header);
			DownloadResponse response = new DownloadResponse(downloadUrl);
			return new ResponseEntity<DownloadResponse>(response, header, HttpStatus.OK);
		} catch (RuntimeException t) {
			sendLogEvent(ownerId, userToken, node);
			throw t;
		}
	}
	
	
    /**
     * 获取文件下载地址
     */
    @RequestMapping(value = "/{fileId}/UrlAndBrowse", method = RequestMethod.GET)
    @ApiOperation(value = "获取文件下载地址")
    @ResponseBody
    public ResponseEntity<DownloadResponse> getUrlAndBrowse(@PathVariable Long ownerId, @PathVariable Long fileId,
                                                            @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        INode node = null;
        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            } else {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            }

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            node = getAndCheckNode(userToken, ownerId, fileId);

            String xUserToken = request.getHeader("x-usertoken");
            if (StringUtils.isNotEmpty(xUserToken)) {
                UserToken accessUserToken = userTokenHelper.checkTokenAndGetUserForV2(xUserToken, headerCustomMap);
				securityMatrixService.checkSecurityMatrix(accessUserToken, ownerId, fileId, SecurityMethod.FILE_DOWNLOAD, headerCustomMap);
			} else {
				securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.FILE_DOWNLOAD, headerCustomMap);
			}

            HttpHeaders header = new HttpHeaders();
            header.set("Connection", "close");

            // 安全扫描及判断
            // 下载：本人可下载自己上传的有毒文件，非本人不可以
            securityCheckManager.checkSecurityStatus(node, true, (ownerId != node.getOwnedBy()));
            String downloadUrl = fileService.getFileDownloadUrl(userToken, node, header);
            DownloadResponse response = new DownloadResponse(downloadUrl);
            recentBrowseService.createByNode(userToken, node);
			return new ResponseEntity<>(response, header, HttpStatus.OK);
        } catch (RuntimeException t) {
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
    @ApiOperation(value = "获取文件详情")
    @ResponseBody
    public ResponseEntity<?> getFileInfo(@PathVariable Long ownerId, @PathVariable Long fileId,
                                         @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        INode node = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            } else {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            }

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            try {
                securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.NODE_INFO,
                        headerCustomMap);
            } catch (NoSuchItemsException e) {
                throw new NoSuchFileException(e);
            }
            node = fileServiceV2.getFileInfo(userToken, ownerId, fileId);

            if (INode.TYPE_FILE == node.getType()) {
                FilesCommonUtils.setNodeVersionsForV2(node);
                RestFileInfo fileInfo = new RestFileInfo(node, userToken.getDeviceType());
                ThumbnailUrlListPacker.transThumbnailUrlList(fileInfo);
                return new ResponseEntity<RestFileInfo>(fileInfo, HttpStatus.OK);
            }
            RestFileVersionInfo versionInfo = new RestFileVersionInfo(node, userToken.getDeviceType());
            return new ResponseEntity<RestFileVersionInfo>(versionInfo, HttpStatus.OK);
        } catch (RuntimeException t) {
            String[] logParams = new String[]{String.valueOf(ownerId), null};
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_FILE_ERR, logParams,
                    null);
            throw t;
        }
    }

	/**
	 * 暂未使用的接口，待删除
	 */
	@RequestMapping(value = "/{fileId}/swfUrl", method = RequestMethod.GET)
	@ResponseBody
	@Deprecated
	public ResponseEntity<PreviewResponse> getPreviewUrl(@PathVariable Long ownerId, @PathVariable Long fileId,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		UserToken userToken = null;
		INode node = null;
		try {
			FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
				String date = request.getHeader("Date");
				userToken = userTokenHelper.getLinkToken(token, date);
				userTokenHelper.assembleUserToken(ownerId, userToken);
			} else {
				userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			}

			// 用户状态校验
			userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

			node = getAndCheckNode(userToken, ownerId, fileId);

			// 安全矩阵
			securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.FILE_PREVIEW,
					headerCustomMap);

			HttpHeaders header = new HttpHeaders();
			header.set("Connection", "close");

			// KIA扫描
			sendDownloadScanTask(node);

			String downloadUrl = filePreviewManager.getPreviewUrl(userToken, node);
			PreviewResponse response = new PreviewResponse(downloadUrl);
			return new ResponseEntity<>(response, header, HttpStatus.OK);

		} catch (RuntimeException t) {
			sendLogEvent(ownerId, userToken, node);
			throw t;
		}
	}

    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/{fileId}/thumbUrl", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ThumbnailUrl> getThumbnailUrl(@PathVariable Long ownerId, @PathVariable Long fileId,
                                                        @RequestParam Integer width, @RequestParam Integer height, @RequestHeader("Authorization") String token,
                                                        HttpServletRequest request) throws BaseRunException {
        INode node = new INode(ownerId, fileId);
        UserToken userToken = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            Thumbnail thumbnail = new Thumbnail(width, height);
            thumbnail.checkParameter();
            if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            } else {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            }

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            INode file = fileBaseService.getAndCheckNode(ownerId, fileId, INode.TYPE_FILE);

            if (!judgeBySecMatrix(userToken, file, request)) {
                throw new ForbiddenException();
            }

            // 文件类型校验
            if (!FilesCommonUtils.isImage(file.getName())) {
                throw new InvalidFileTypeException();
            }
            ThumbnailUrl thumbnailUrl = fileServiceV2.getThumbnailUrl(userToken, file, thumbnail);

            return new ResponseEntity<ThumbnailUrl>(thumbnailUrl, HttpStatus.OK);
        } catch (RuntimeException t) {
            String keyword = null;
            String parentId = null;

            keyword = StringUtils.trimToEmpty(node.getName());
            parentId = String.valueOf(node.getParentId());

            String[] logParams = new String[]{String.valueOf(ownerId), String.valueOf(parentId)};
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null,
                    UserLogType.GET_THUMBNAIL_URL_FILE_ERR, logParams, keyword);
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
    @ApiOperation(value = "列举文件版本")
    @ResponseBody
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public ResponseEntity<RestVersionLists> listVersion(@PathVariable Long ownerId, @PathVariable Long fileId,
                                                        @RequestParam(required = false) Long offset, @RequestParam(required = false) Integer limit,
                                                        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        INode node = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            Limit limitObj = new Limit(offset, limit);
            limitObj.checkParameter();

            // Token 验证
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.FOLDER_LIST,
                    headerCustomMap);
            INode fileNode = new INode();
            fileNode.setOwnedBy(ownerId);
            fileNode.setId(fileId);
            node = fileBaseService.getINodeInfo(ownerId, fileId);

            FileINodesList versionList = fileService.getFileVersionLists(userToken, fileNode, limitObj);

			RestVersionLists restVersionLists = new RestVersionLists(versionList, userToken.getDeviceType());
			List<RestFileVersionInfo> list = restVersionLists.getVersions();
			for(int i=0;i<list.size();i++ ){
				RestFileVersionInfo restFileVersionInfo = list.get(i);
				User user = userService.get(restFileVersionInfo.getCreatedBy());
				if(user != null){
					restFileVersionInfo.setCreatedByName(user.getName());
				}
			}

			return new ResponseEntity<RestVersionLists>(restVersionLists,HttpStatus.OK);
		} catch (RuntimeException t) {
			String keyword = null;
			String parentId = null;
			if (node != null) {
				keyword = node.getName();
				parentId = String.valueOf(node.getParentId());
			}
			String[] logParams = new String[] { String.valueOf(ownerId), parentId };
			fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.LIST_VERSION_FILE_ERR,
					logParams, keyword);
			throw t;
		}
	}

    /**
     * 获取文件预览元数据接口
     *
     * @param ownerId
     * @param fileId
     * @param token
     * @param request
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{fileId}/previewMeta", method = RequestMethod.GET)
    @ApiOperation(value = "获取文件预览元数据接口")
    @ResponseBody
    public ResponseEntity<PreviewMetaResponse> previewMeta(@PathVariable Long ownerId, @PathVariable Long fileId, @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        PreviewMetaResponse response = new PreviewMetaResponse();

        UserToken userToken = null;
        INode node = null;

        // 检查请求字段格式
        FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);
        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
        // 获取userToken
        userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

        // 用户状态校验
        userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

        // 获取文件信息
        node = getAndCheckNode(userToken, ownerId, fileId);

        // 生成预览响应
        // 获取文件后缀名，判断是否支持预览
        if (isSupportPreview(node)) {
            response.setPreviewSupport(true);
            response.setInodeSize(node.getSize());
            ImgObject imgObject = this.fileService.getImgObject(node);

            if (null != imgObject) {
                response.setRange(imgObject.getPageIndex());
                response.setTotalPages(imgObject.getTotalPages());
            } else {
                // 判断任务是否在任务列表中存在
                TaskBean taskBean = this.fileService.getTaskBean(node);
                if (null != taskBean) {
                    if (99 == taskBean.getStatus()) {
                        response.setResultCode(Constants.ResultCode.FILE_CONVERT_FAIL);
                    } else {
                        response.setResultCode(Constants.ResultCode.FILE_PREVIEW_CONVERTING);
                    }
                } else {
                    response.setResultCode(Constants.ResultCode.FILE_NOT_CONVERT);
                }
            }

        } else {
            response.setPreviewSupport(false);
        }

        HttpHeaders header = new HttpHeaders();
        header.set("Connection", "close");

		return new ResponseEntity<>(response, header, HttpStatus.OK);
    }

    /**
     * 获取文件预览图片地址
     *
     * @param ownerId 所属owner
     * @param fileId  文件唯一标识
     * @param token   token
     * @param request Http请求对象
     * @return
     */
    @RequestMapping(value = "/{fileId}/preview", method = RequestMethod.GET)
    @ApiOperation(value = "获取文件预览图片地址")
    @ResponseBody
    public ResponseEntity<PreviewResponse> preview(@PathVariable Long ownerId, @PathVariable Long fileId, @RequestHeader("Authorization") String token, HttpServletRequest request) {
        UserToken userToken = null;
        INode node = null;

        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);

        try {
            // 检查请求字段格式
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);

            if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
				userToken.setCloudUserId(ownerId);
                userTokenHelper.assembleUserToken(ownerId, userToken);
            } else {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            }

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            // 获取文件信息
            node = getAndCheckNode(userToken, ownerId, fileId);

            String xUserToken = request.getHeader("x-usertoken");
            if (StringUtils.isNotEmpty(xUserToken)) {
                UserToken accessUserToken = userTokenHelper.checkTokenAndGetUserForV2(xUserToken, headerCustomMap);
				securityMatrixService.checkSecurityMatrix(accessUserToken, ownerId, fileId, SecurityMethod.FILE_PREVIEW, headerCustomMap);
            } else {
				securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.FILE_PREVIEW, headerCustomMap);
            }

			String previewUrl = filePreviewManager.getPreviewUrl(userToken,node);

			//非外链访问，保存浏览记录
			if(!token.startsWith(UserTokenHelper.LINK_PREFIX)) {
				recentBrowseService.createByNode(userToken, node);
			}

			return new ResponseEntity<>(new PreviewResponse(previewUrl), HttpStatus.OK);

        } catch (RuntimeException t) {
            sendLogEvent(ownerId, userToken, node, UserLogType.PREVIEW_URL_ERROR);
            throw t;
        }
    }


    private boolean checkThumbnailAcl(UserToken user, INode node) {
        boolean addThumbnailUrl = false;
        if (user.getId() == node.getOwnedBy()) {
            addThumbnailUrl = true;
        } else {
            ACL nodeACL = null;
            if (StringUtils.isNotBlank(user.getLinkCode())) {
                nodeACL = aclManager.getACLForLink(user.getLinkCode(), node);
            } else {
                String enterpriseId = "";
                if (user.getAccountVistor() != null) {
                    enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
                }
				nodeACL = aclManager.getACLForAccessUser(user.getId(), INodeACL.TYPE_USER, node, enterpriseId,user);
            }
            if (nodeACL.isDownload() || nodeACL.isPreview()) {
                addThumbnailUrl = true;
            }
        }
        return addThumbnailUrl;
    }

    private INode getAndCheckNode(UserToken user, Long ownerId, Long fileId) throws BaseRunException {
        if (user == null || ownerId == null || fileId == null) {
            throw new InvalidParamException();
        }
        INode node = fileBaseService.getINodeInfo(ownerId, fileId);
        if (null == node) {
            String message = "File not exist, owner id:" + ownerId + ", file id:" + fileId;
            throw new NoSuchFileException(message);
        }

        if (INode.STATUS_NORMAL != node.getStatus()) {
            String message = "File status abnormal, owner id:" + ownerId + ", file id:" + fileId + ", status:"
                    + node.getStatus();
            throw new NoSuchFileException(message);
        }

        if (node.getType() != INode.TYPE_FILE && node.getType() != INode.TYPE_VERSION) {
            String message = "File type invalid, owner id:" + ownerId + ", file id:" + fileId + ",type:"
                    + node.getType();
            throw new NoSuchFileException(message);
        }
        return node;
    }

    private boolean judgeBySecMatrix(UserToken user, INode node, HttpServletRequest request) {
        boolean addThumbnailUrl = checkThumbnailAcl(user, node);
        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
        try {
            securityMatrixService.checkSecurityMatrix(user, node.getOwnedBy(), node.getId(),
                    SecurityMethod.FILE_DOWNLOAD, headerCustomMap);
        } catch (SecurityMatixException e) {
            try {
                securityMatrixService.checkSecurityMatrix(user, node.getOwnedBy(), node.getId(),
                        SecurityMethod.FILE_PREVIEW, headerCustomMap);
            } catch (SecurityMatixException e1) {
                addThumbnailUrl = false;
            }
        } catch (Exception e) {
            LOGGER.warn("When checkSecurityMatrix download", e);
        }
        return addThumbnailUrl;
    }

    private void sendDownloadScanTask(INode node) {
        CheckEngine engine = securityMatrixService.getCheckEngine();

        LOGGER.info("Security matrix check engine: {}", engine);

        if (CheckEngine.HUAWEI == engine) {
            return;
        }

        int secLabel = securityScanManager.sendScanTask(node, SecurityScanTask.PRIORITY_HIGH);

        SecurityStatus status = SecurityStatus.getSecurityStatus(secLabel);
        LOGGER.info("status: {} ; ignoreScanResult: {}", status, IGNORE_SCAN_RESULT);

        if (status == null || IGNORE_SCAN_RESULT) {
            return;
        }
        switch (status) {
            case KIA_UMCOMPLETED:
                throw new FileScanningException("File is not ready");
            case KIA_COMPLETED_INSECURE:
                throw new ScannedForbiddenException("This file is not allowed to be downloaded");
            default:
                break;
        }
    }

    private void sendLogEvent(Long ownerId, UserToken userToken, INode node) {
        String keyword = null;
        String parentId = null;
        if (node != null) {
            keyword = StringUtils.trimToEmpty(node.getName());
            parentId = String.valueOf(node.getParentId());
        }
        String[] logParams = new String[]{String.valueOf(ownerId), parentId};
        fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.DOWN_URL_FILE_ERR,
                logParams, keyword);
    }

    private void sendLogEvent(Long ownerId, UserToken userToken, INode node, UserLogType userLogType) {
        String keyword = null;
        String parentId = null;
        if (node != null) {
            keyword = StringUtils.trimToEmpty(node.getName());
            parentId = String.valueOf(node.getParentId());
        }
        String[] logParams = new String[]{String.valueOf(ownerId), parentId};
        fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, userLogType, logParams, keyword);
    }

    private boolean isSupportPreview(INode iNode) {
        boolean isSupportPreview = false;
        String[] array = {"doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf"};
        List<String> slist = Arrays.asList(array);

        if (iNode.getType() == INode.TYPE_FILE) {
            String filetype = iNode.getName().substring(iNode.getName().lastIndexOf(".") + 1, iNode.getName().length());

            if (slist.contains(filetype.toLowerCase())) {
                isSupportPreview = true;
            }
        }

        return isSupportPreview;
    }
}
