package com.huawei.sharedrive.app.openapi.restv2.folder;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.files.service.FolderServiceV2;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.*;
import com.huawei.sharedrive.app.openapi.rest.FilesCommonApi;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.box.domain.Order;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 专为微信小程序扩展的接口
 */
@Controller
@RequestMapping(value = "/api/wxmp/folders/{ownerId}")
@Api(description = "专为微信小程序扩展的接口")
public class WxMpFolderApi extends FilesCommonApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(WxMpFolderApi.class);

    @Autowired
    private FileBaseService fileBaseService;

    @Autowired
    private FolderService folderService;

    @Autowired
    private FolderServiceV2 folderServiceV2;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private SecurityMatrixService securityMatrixService;
    
    @Autowired
    private MessageSource messageSource;
    

    /**
     * 获取文件夹详情
     *
     * @param ownerId
     * @param folderId
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{folderId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getFolderInfo(@PathVariable Long ownerId, @PathVariable Long folderId,
                                           @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        INode inode = null;
        UserToken userToken = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, folderId);
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            // Token 验证
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
                securityMatrixService.checkSecurityMatrix(userToken,
                        ownerId,
                        folderId,
                        SecurityMethod.NODE_INFO,
                        headerCustomMap);
            } catch (NoSuchItemsException e) {
                throw new NoSuchFolderException(e);
            }
            // 调用service获取目录信息
            inode = folderServiceV2.getFolderInfo(userToken, ownerId, folderId);
            RestFolderInfo folderInfo = new RestFolderInfo(inode);
            return new ResponseEntity<RestFolderInfo>(folderInfo, HttpStatus.OK);
        } catch (BaseRunException t) {
            if (inode != null) {
                String keyword = StringUtils.trimToEmpty(inode.getName());

                String[] logParams = new String[]{String.valueOf(ownerId),
                        String.valueOf(inode.getParentId())};
                fileBaseService.sendINodeEvent(userToken,
                        EventType.OTHERS,
                        null,
                        null,
                        UserLogType.GET_FOLDER_ERR,
                        logParams,
                        keyword);
            }

            throw t;
        }
    }

    /**
     * 列举文件夹
     *
     * @param ownerId           文件夹所有者ID
     * @param folderId          文件夹ID
     * @param lsRequest 列举文件夹请求对象
     * @param token             认证token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{folderId}/items", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestFolderLists> listFolder(@PathVariable Long ownerId, @PathVariable Long folderId,
                                                      @RequestBody(required = false) ListFolderRequest lsRequest,
                                                      @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        INode node = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, folderId);
            if (lsRequest != null) {
                lsRequest.checkParameter();
            } else {
                lsRequest = new ListFolderRequest();
            }

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

            List<Order> orderList = lsRequest.getOrder();
            List<Thumbnail> thumbnailList = lsRequest.getThumbnail();
            int limit = lsRequest.getLimit();
            long offset = lsRequest.getOffset();
            if (folderId == INode.FILES_ROOT) {
                node = new INode(ownerId, folderId);
            } else {
                try {
                    node = folderService.getNodeInfo(userToken, ownerId, folderId);
                } catch (NoSuchItemsException e) {
                    LOGGER.error("Folder not exists [ " + ownerId + "; " + folderId + " ]");
                    throw new NoSuchFolderException(e);
                }
                if (node == null) {
                    LOGGER.error("Folder not exists [ " + ownerId + "; " + folderId + " ]");
                    throw new NoSuchFolderException();
                }
            }
            node.setStatus(INode.STATUS_NORMAL);
            FileINodesList reList = folderServiceV2.wxMpListNodesByFilter(userToken, node, offset, limit, orderList, thumbnailList, lsRequest.getWithExtraType(), headerCustomMap);

            RestFolderLists folderList = new RestFolderLists(reList, userToken.getDeviceType(),messageSource,request.getLocale());
            fillListUserInfo(folderList);
            return new ResponseEntity<>(folderList, HttpStatus.OK);
        } catch (BaseRunException t) {
            if (node != null) {
                String keyword = StringUtils.trimToEmpty(node.getName());
                String[] logParams = new String[]{String.valueOf(ownerId), String.valueOf(folderId != INode.FILES_ROOT ? node.getParentId() : INode.FILES_ROOT)};
                fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.LIST_FOLDER_ERR, logParams, keyword);
            }

            throw t;
        }
    }

}
