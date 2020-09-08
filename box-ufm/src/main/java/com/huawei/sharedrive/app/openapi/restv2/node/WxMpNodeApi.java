package com.huawei.sharedrive.app.openapi.restv2.node;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FileServiceV2;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.rest.FilesCommonApi;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 专为微信小程序使用的Node相关的接口，取消了安全检查
 */
@Controller
@RequestMapping(value = "/api/wxmp/nodes/{ownerId}")
@Api(description = "专为微信小程序使用的Node相关的接口")
public class WxMpNodeApi extends FilesCommonApi {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(WxMpNodeApi.class);

    @Autowired
    private FileBaseService fileBaseService;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private UserService userService;

    @Autowired
    private FileServiceV2 fileServiceV2;

    /**
     * 获取节点信息
     */
    @RequestMapping(value = "/{nodeId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取节点信息")
    @ResponseBody
    public ResponseEntity<?> getNodeInfo(@PathVariable Long ownerId,
                                         @PathVariable Long nodeId,
                                         @RequestHeader("Authorization") String token,
                                         @RequestParam(required = false, defaultValue = "0") int width,
                                         @RequestParam(required = false, defaultValue = "0") int height,
                                         HttpServletRequest request) throws BaseRunException {
        Thumbnail thumbnail = null;
        try {
            thumbnail = new Thumbnail(width, height);
            thumbnail.checkParameter();
        } catch (Exception e) {
            thumbnail = null;
        }

        UserToken userToken = null;
        INode node = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);
            Map<String, String> headerCustomMap = HeaderPacker
                    .getCustomHeaderMap(request);
            if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                assembleUserToken(ownerId, userToken);
            } else {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token,
                        headerCustomMap);
            }

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            node = fileBaseService.getAndCheckNode(userToken, ownerId, nodeId,
                    INode.TYPE_ALL);
            if (node.getType() == INode.TYPE_FILE && thumbnail != null) {
                ThumbnailUrl thumbnailUrl = fileServiceV2.getThumbnailUrl(
                        userToken, node, thumbnail);
                if (thumbnailUrl != null) {
                    node.setThumbnailUrl(thumbnailUrl.getThumbnailUrl());
                }
            }

            if (INode.TYPE_FILE == node.getType()) {
                FilesCommonUtils.setNodeVersionsForV2(node);
                RestFileInfo fileInfo = new RestFileInfo(node,
                        userToken.getDeviceType());
                transThumbnailUrlList(fileInfo);
                return new ResponseEntity<>(fileInfo, HttpStatus.OK);
            }
            if (FilesCommonUtils.isFolderType(node.getType())) {
                RestFolderInfo folderInfo = new RestFolderInfo(node);
                return new ResponseEntity<>(folderInfo, HttpStatus.OK);
            }
            throw new NoSuchItemsException();
        } catch (RuntimeException t) {
            String keyword = null;
            String parentId = null;
            if (node != null) {
                keyword = StringUtils.trimToEmpty(node.getName());
                parentId = String.valueOf(node.getParentId());
            }
            String[] logParams = new String[]{String.valueOf(ownerId),
                    parentId};
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null,
                    null, UserLogType.GET_NODEINFO_ERR, logParams, keyword);
            throw t;
        }
    }

    /**
     * 外链鉴权，导致用户业务日志查询不完全
     */
    private void assembleUserToken(Long ownerId, UserToken userToken) {
        if (userToken == null) {
            return;
        }
        try {
            User user = userService.get(null, ownerId);
            if (user != null) {
                userToken.setAppId(user.getAppId());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private void transThumbnailUrlList(RestFileInfo restFileInfo) {
        if (restFileInfo == null || restFileInfo.getThumbnailUrlList() == null) {
            return;
        }
        if (restFileInfo.getThumbnailUrlList().isEmpty()) {
            restFileInfo.setThumbnailUrlList(null);
        }
    }
}
