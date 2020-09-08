package com.huawei.sharedrive.app.openapi.restv2.folder;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.SecurityMatixException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.files.service.FolderServiceV2;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.CreateFolderRequest;
import com.huawei.sharedrive.app.openapi.domain.node.ListFolderRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderLists;
import com.huawei.sharedrive.app.openapi.rest.FilesCommonApi;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceMembershipsDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.box.domain.Order;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 收集箱文件夹API Rest接口
 */
@Controller
@RequestMapping(value = "/api/v2/inbox")
public class InboxApi extends FilesCommonApi {
    private static final Logger logger = LoggerFactory.getLogger(InboxApi.class);

    @Autowired
    private TeamSpaceMembershipsDAO teamSpaceMembershipsDAO;

    @Autowired
    private FileBaseService fileBaseService;

    @Autowired
    private FolderServiceV2 folderServiceV2;

    @Autowired
    private FolderService folderService;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private SecurityMatrixService securityMatrixService;
//    /**
//     * 列举文件夹
//     */
//    @RequestMapping(value = "/{ownerId}/{folderId}/items", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity<RestFolderLists> listInboxFolder(@PathVariable long ownerId, @PathVariable long folderId, @RequestBody(required = false) ListFolderRequest listRequest,
//                                                           @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
//        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
//        UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
//        try {/*
//            FilesCommonUtils.checkNonNegativeIntegers(ownerId);
//            if (listRequest != null) {
//                listRequest.checkParameter();
//            } else {
//                listRequest = new ListFolderRequest();
//            }
//
//            if (ownerId != userToken.getCloudUserId()) {
//                //用户只能查询自己的收集箱
//                throw new SecurityMatixException("Security check failed: user only can check his inbox files.");
//            }
//
//            // 用户状态校验
//            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
//
//            //每个人的收集箱文件夹在特定的团队空间下（每个企业只有1个,type=5）。该团队空间中只有一个成员：userId=enterpriseId，userType=5
//            long enterpriseId = userToken.getAccountVistor().getEnterpriseId();
//            long teamId = getTeamIdOfInbox(enterpriseId);
//            if (teamId > 0) {
//                //teamspace下的根目录
//                INode node = new INode(teamId, folderId);
//                node.setCreatedBy(ownerId); //当前用户创建的
//                node.setStatus(INode.STATUS_NORMAL);
//
//                //按修改时间倒序
//                List<Order> orderList = listRequest.getOrder();
//
//                List<Thumbnail> thumbnailList = listRequest.getThumbnail();
//
//                FileINodesList reList = folderServiceV2.listNodesByFilter(userToken, node, 0, 100, orderList, thumbnailList, listRequest.getWithExtraType(), headerCustomMap);
//                RestFolderLists folderList = new RestFolderLists(reList, userToken.getDeviceType());
//                fillListUserInfo(folderList);
//                return new ResponseEntity<>(folderList, HttpStatus.OK);
//            }
//
//            return new ResponseEntity<>(new RestFolderLists(), HttpStatus.OK);
//        */} catch (BaseRunException t) {
//            String[] logParams = new String[]{String.valueOf(ownerId), String.valueOf(INode.FILES_ROOT)};
//            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.LIST_FOLDER_ERR, logParams, "inbox");
//
//            throw t;
//        }
//    }


    /**
     * 创建文件夹
     *
     */
    @RequestMapping(value = "/{ownerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestFolderInfo> createFolder(@PathVariable Long ownerId, @RequestBody CreateFolderRequest createRequest,
                                                       @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId);
            //只能在根目录下创建
            createRequest.setParent(0L);
            createRequest.checkParameter();
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, createRequest.getParent(), SecurityMethod.FOLDER_CREATE, headerCustomMap);

            INode newFolder = createRequest.transToINode();

            //创建人设为用户
            newFolder.setCreatedBy(ownerId);

            long enterpriseId = userToken.getAccountVistor().getEnterpriseId();
            long teamId = getTeamIdOfInbox(enterpriseId);
            //ownerId为团队空间teamId
            newFolder.setOwnedBy(teamId);

            if (teamId > 0) {
                INode node = folderService.createFolder(userToken, newFolder, createRequest.getMergeValue());

                RestFolderInfo folderInfo = new RestFolderInfo(node);
                return new ResponseEntity<>(folderInfo, HttpStatus.CREATED);
            }


            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (BaseRunException t) {
            String[] logParams = new String[]{String.valueOf(ownerId), String.valueOf(createRequest.getParent() != null ? createRequest.getParent() : "")};
            String keyword = StringUtils.trimToEmpty(createRequest.getName());
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.CREATE_FOLDER_ERR, logParams, keyword);
            throw t;
        }
    }

    private long getTeamIdOfInbox(long enterpriseId) {
        List<TeamSpaceMemberships> systemList = teamSpaceMembershipsDAO.listUserTeamSpaceMemberships("" + enterpriseId, TeamSpace.TYPE_RECEIVE_FOLDER, TeamSpaceMemberships.TYPE_SYSTEM, null, null);
        if (!systemList.isEmpty()) {
            if (systemList.size() > 1) {
                logger.warn("Found more than one inbox teamspace: enterpriseId={}", enterpriseId);
            }
            //正常情况下，有且仅有一个
            TeamSpaceMemberships membership = systemList.get(0);

            return membership.getCloudUserId();
        } else {
            logger.warn("Can't find  inbox teamspace: enterpriseId={}", enterpriseId);
        }

        return 0;
    }
}
