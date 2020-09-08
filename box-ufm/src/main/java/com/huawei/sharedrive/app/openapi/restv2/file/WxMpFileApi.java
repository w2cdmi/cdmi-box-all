package com.huawei.sharedrive.app.openapi.restv2.file;

import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchFileException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.*;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.DownloadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileVersionInfo;
import com.huawei.sharedrive.app.openapi.restv2.file.packer.ThumbnailUrlListPacker;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 文件API Rest接口, 提供文件预上传, 下载, 复制, 移动, 删除等操作
 * 专为微信小程序使用的Node相关的接口，取消了安全检查
 */
@Controller
@RequestMapping(value = "/api/wxmp/files/{ownerId}")
public class WxMpFileApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(WxMpFileApi.class);


    @Autowired
    private FileBaseService fileBaseService;

    @Autowired
    private RecentBrowseService recentBrowseService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileServiceV2 fileServiceV2;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    UserStatisticsService userStatisticsService;

    /**
     * 获取文件详情
     *
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
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_FILE_ERR, logParams, null);
            throw t;
        }
    }

    /**
     * 获取文件下载地址
     *
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

            HttpHeaders header = new HttpHeaders();
            header.set("Connection", "close");

            DataAccessURLInfo dataAccessUrlInfo = fileBaseService.getDownURLByNearAccess(userToken.getLoginRegion(), node.getOwnedBy(), node);

            sendLogEvent(userToken, node);
            header.add("objectId", node.getObjectId());

            recentBrowseService.createByNode(userToken, node);
            DownloadResponse response = new DownloadResponse(dataAccessUrlInfo.getDownloadUrl());
            return new ResponseEntity<>(response, header, HttpStatus.OK);
        } catch (RuntimeException t) {
            sendLogEvent(ownerId, userToken, node);
            throw t;
        }
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
            String message = "File status abnormal, owner id:" + ownerId + ", file id:" + fileId + ", status:" + node.getStatus();
            throw new NoSuchFileException(message);
        }

        if (node.getType() != INode.TYPE_FILE && node.getType() != INode.TYPE_VERSION) {
            String message = "File type invalid, owner id:" + ownerId + ", file id:" + fileId + ",type:" + node.getType();
            throw new NoSuchFileException(message);
        }
        return node;
    }

    private void sendLogEvent(Long ownerId, UserToken userToken, INode node) {
        String keyword = null;
        String parentId = null;
        if (node != null) {
            keyword = StringUtils.trimToEmpty(node.getName());
            parentId = String.valueOf(node.getParentId());
        }
        String[] logParams = new String[]{String.valueOf(ownerId), parentId};
        fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.DOWN_URL_FILE_ERR, logParams, keyword);
    }

    private void sendLogEvent(UserToken user, INode node) {
        String[] logMsgs = new String[]{String.valueOf(node.getOwnedBy()), String.valueOf(node.getParentId())};
        String keyword = StringUtils.trimToEmpty(node.getName());
        fileBaseService.sendINodeEvent(user, EventType.INODE_DOWNLOAD, node, null, UserLogType.DOWN_URL_FILE, logMsgs, keyword);
    }
}
