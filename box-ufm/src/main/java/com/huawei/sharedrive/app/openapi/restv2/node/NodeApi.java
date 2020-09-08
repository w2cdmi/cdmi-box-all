package com.huawei.sharedrive.app.openapi.restv2.node;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.service.AccountService;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectSecretLevel;
import com.huawei.sharedrive.app.files.service.*;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.node.manager.NodeManager;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.*;
import com.huawei.sharedrive.app.openapi.domain.node.*;
import com.huawei.sharedrive.app.openapi.domain.share.RestNodeLinksList;
import com.huawei.sharedrive.app.openapi.rest.FilesCommonApi;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.share.service.INodeLinkApproveService;
import com.huawei.sharedrive.app.share.service.LinkServiceV2;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
import pw.cdmi.box.domain.Limit;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件夹/文件共用接口, 提供复制, 移动, 删除等共用操作
 *
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-4-30
 * @see
 * @since
 */
@Controller
@RequestMapping(value = "/api/v2/nodes/{ownerId}")
@Api(description = "文件夹/文件操作接口")
public class NodeApi extends FilesCommonApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeApi.class);

    @Autowired
    private FileBaseService fileBaseService;

    @Autowired
    private FileService fileService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private INodeACLService iNodeACLService;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private RecentBrowseService recentBrowseService;

    @Autowired
    private INodeLinkApproveService linkApproveService;

    @Autowired
    private SecurityMatrixService securityMatrixService;

    @Autowired
    private UserService userService;

    @Autowired
    private LinkServiceV2 linkServiceV2;

    @Autowired
    private ObjectSecretLevelService ObjectSecretLevelService;

    @Autowired
    private TrashServiceV2 trashServiceV2;

    @Autowired
    private NodeManager nodeManager;

    private static final String FORCE_RETURN = "true";

    @Autowired
    private SystemConfigDAO systemConfigDAO;

    @Autowired
    private FolderServiceV2 folderServiceV2;


    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AccountService accountService;

    /**
     * 删除节点至回收站
     * 
     * @param ownerId
     * @param nodeId
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{nodeId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除节点至回收站")
    @ResponseBody
    public ResponseEntity<String> deleteNode(@PathVariable Long ownerId, @PathVariable Long nodeId,
                                             @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        INode nodeInfo = null;
        byte nodeType = INode.TYPE_FOLDER;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);

            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, nodeId, SecurityMethod.NODE_DELETE,headerCustomMap);
            INode node = new INode();
            node.setId(nodeId);
            node.setOwnedBy(ownerId);
            node.setType(INode.TYPE_ALL);
            nodeInfo = fileBaseService.getAndCheckNode(node.getOwnedBy(), node.getId(), node.getType());
            nodeType = nodeInfo.getType();
            nodeService.deleteNode(userToken, nodeInfo);
            linkApproveService.deleteByNodeId(userToken,node);
            ResponseEntity<String> rsp = new ResponseEntity<String>(HttpStatus.OK);

            return rsp;
        } catch (RuntimeException t) {
            String keyword = null;
            String parentId = null;
            if (nodeInfo != null) {
                keyword = StringUtils.trimToEmpty(nodeInfo.getName());
                parentId = String.valueOf(nodeInfo.getParentId());
            }
            String[] logParams = new String[]{String.valueOf(ownerId), parentId};
            UserLogType userLogType = UserLogType.DELETE_FOLDER_ERR;
            if (nodeType == INode.TYPE_FILE) {
                userLogType = UserLogType.DELETE_FILE_ERR;
            } else if (nodeType == INode.TYPE_VERSION) {
                if (nodeInfo != null) {
                    logParams = new String[]{String.valueOf(ownerId), parentId,
                            String.valueOf(nodeInfo.getId())};
                }
                userLogType = UserLogType.DELETE_VERSION_FILE_ERR;
            }
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    userLogType,
                    logParams,
                    keyword);
            throw t;
        }
    }

    /**
     * 获取节点信息
     *
     * @param ownerId
     * @param nodeId
     * @param token
     * @param request
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{nodeId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取节点信息")
    @ResponseBody
    public ResponseEntity<?> getNodeInfo(@PathVariable Long ownerId, @PathVariable Long nodeId,
                                         @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        INode node = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            if (token.startsWith(UserTokenHelper.LINK_PREFIX)) {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                assembleUserToken(ownerId, userToken);
            } else {
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            }

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            securityMatrixService.checkSecurityMatrix(userToken, ownerId, nodeId, SecurityMethod.NODE_INFO, headerCustomMap);

            node = fileBaseService.getAndCheckNode(userToken, ownerId, nodeId, INode.TYPE_ALL);
            // 权限检测
            iNodeACLService.vaildINodeOperACL(userToken, node, AuthorityMethod.GET_INFO.name());

            if (INode.TYPE_FILE == node.getType()) {
                FilesCommonUtils.setNodeVersionsForV2(node);
                RestFileInfo fileInfo = new RestFileInfo(node, userToken.getDeviceType());
                transThumbnailUrlList(fileInfo);
                return new ResponseEntity<RestFileInfo>(fileInfo, HttpStatus.OK);
            }
            if (FilesCommonUtils.isFolderType(node.getType())) {
                RestFolderInfo folderInfo = new RestFolderInfo(node);
                return new ResponseEntity<RestFolderInfo>(folderInfo, HttpStatus.OK);
            }
            throw new NoSuchItemsException();
        } catch (RuntimeException t) {
            String keyword = null;
            String parentId = null;
            if (node != null) {
                keyword = StringUtils.trimToEmpty(node.getName());
                parentId = String.valueOf(node.getParentId());
            }
            String[] logParams = new String[]{String.valueOf(ownerId), parentId};
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_NODEINFO_ERR,
                    logParams,
                    keyword);
            throw t;
        }
    }

    /**
     * 获取节点所有父节点集合(不含本节点), 集合内元素按目录结构由深至浅的顺序放置
     * 
     * @param ownerId
     * @param nodeId
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{nodeId}/path", method = RequestMethod.GET)
    @ApiOperation(value = " 获取节点所有父节点集合", notes = "集合内元素按目录结构由深至浅的顺序放置")
    @ResponseBody
    public ResponseEntity<?> getNodePath(@PathVariable Long ownerId, @PathVariable Long nodeId, @RequestParam(required = false) Long rootId,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);

            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            List<INode> nodeList = nodeService.getNodePath(ownerId, nodeId);

            List<RestBaseObject> list = new ArrayList<RestBaseObject>(BusinessConstants.INITIAL_CAPACITIES);
            RestBaseObject obj = null;
            for (INode node : nodeList) {
                if (FilesCommonUtils.isFolderType(node.getType())) {
                    obj = new RestFolderInfo(node);
                } else if (INode.TYPE_FILE == node.getType()) {
                    obj = new RestFileInfo(node, userToken.getDeviceType());
                } else {
                    throw new NoSuchItemsException("Invalid file type. ID: " + node.getId() + ", type: " + node.getType());
                }
                list.add(obj);
            }

            //指定一某个nodeId作为根节点
            if(rootId != null) {
                int index = getShareRootFolderPosition(list, rootId);
                if(index != -1) {
                    list = list.subList(index, list.size());
                } else {
                    //列表没有发现根节点的位置，返回空路径列表
                    list = new ArrayList<>();
                }
            }

            return new ResponseEntity<>(list, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            INode nodeLog = fileBaseService.getAndCheckNodeForGetNodePath(ownerId, nodeId);
            String keyword = null;
            String parentId = null;
            if (nodeLog != null)
            {
                keyword = StringUtils.trimToEmpty(nodeLog.getName());
                parentId = String.valueOf(nodeLog.getParentId());
            }
            String[] logParams = new String[]{String.valueOf(ownerId), parentId};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_NODE_PATH_ERR,
                logParams,
                keyword);
            throw t;
        }
    }

    private int getShareRootFolderPosition(List<RestBaseObject> pathList, long rootId) {
        for(int i = 0; i < pathList.size(); i++) {
            if(pathList.get(i).getId() == rootId) {
                return i;
            }
        }

        return  -1;
    }

    /**
     * 节点重命名/同步状态设置
     * 
     * @param ownerId
     * @param nodeId
     * @param request
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{nodeId}", method = RequestMethod.PUT)
    @ApiOperation(value = "节点重命名/同步状态设置")
    @ResponseBody
    public ResponseEntity<?> renameAndSetSyncStatus(@PathVariable Long ownerId, @PathVariable Long nodeId,
                                                    @RequestBody RenameAndSetSyncRequest request, @RequestHeader("Authorization") String token,
                                                    HttpServletRequest requestServlet)
            throws BaseRunException {
        UserToken userToken = null;
        INode node = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);
            request.checkParameter();

            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            node = fileBaseService.getAndCheckNode(ownerId, nodeId, INode.TYPE_ALL);
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, nodeId, SecurityMethod.NODE_RENAME,
                    headerCustomMap);
            node = nodeService.renameAndSetSyncStatus(userToken,
                    ownerId,
                    node,
                    request.getName(),
                    request.getSyncStatus(),
                    node.getType());
//            fileBaseService.getParentINodeInfoCheckStatus(ownerId, inodeId, status)


            if (INode.TYPE_FILE == node.getType())

            {
                boolean isVersion = true;
                Limit limitObj = new Limit(0L, 1000);
                limitObj.checkParameter();
                FileINodesList versionList = fileService.getFileVersionLists(userToken, node, limitObj);
                for (INode inode : versionList.getFiles()) {
                    if (inode.getId() == node.getId()) {
                        continue;
                    }
                    nodeService.renameVersionNode(userToken,
                            ownerId,
                            inode,
                            request.getName(),
                            null,
                            inode.getType());
                }

                recentBrowseService.createByNode(userToken, node);
                RestFileInfo fileInfo = new RestFileInfo(node, userToken.getDeviceType());
                transThumbnailUrlList(fileInfo);
                return new ResponseEntity<RestFileInfo>(fileInfo, HttpStatus.OK);
            } else {
                RestFolderInfo folderInfo = new RestFolderInfo(node);
                return new ResponseEntity<RestFolderInfo>(folderInfo, HttpStatus.OK);
            }
        } catch (RuntimeException t) {
            byte nodeType = node != null ? node.getType() : INode.TYPE_ALL;
            String parentId = node != null ? String.valueOf(node.getParentId()) : null;
            String syncStatus = node != null ? String.valueOf(node.getSyncStatus()) : null;
            String[] logParams = new String[]{String.valueOf(ownerId), parentId, syncStatus};
            String keyword = StringUtils.trimToEmpty(request.getName());
            UserLogType userLogType = nodeType == INode.TYPE_FILE ? UserLogType.UPDATE_FILE_NAME_SYNC_ERR
                    : UserLogType.UPDATE_FOLDER_NAME_SYNC_ERR;
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    userLogType,
                    logParams,
                    keyword);
            throw t;
        }
    }

    /**
     * 根据名称搜索文件/文件夹(模糊匹配)
     *
     * @param ownerId
     * @param request
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ApiOperation(value = "根据名称搜索文件/文件夹(模糊匹配)")
    @ResponseBody
    public ResponseEntity<?> search(@PathVariable Long ownerId, @RequestBody ListFolderRequest request,
                                    @RequestHeader("Authorization") String token, HttpServletRequest requestServlet) throws BaseRunException {
        UserToken userToken = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId);
            request.checkParameter();

            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            securityMatrixService.checkSecurityMatrix(userToken, ownerId, null, SecurityMethod.FOLDER_LIST,
                headerCustomMap);

            INode filter = new INode();
            filter.setOwnedBy(ownerId);
            filter.setStatus(INode.STATUS_NORMAL);
            filter.setName(request.getName());
            filter.setFilelabelIds(request.getLabelIds());
            filter.setDoctype(request.getDocType());
            filter.setType(request.getType());
            FileINodesList nodeList = folderServiceV2.listNodesByFilter(userToken,filter,request,headerCustomMap);
            RestFolderLists folderList = new RestFolderLists(nodeList, userToken.getDeviceType(),messageSource,requestServlet.getLocale());
            if (folderList.getFiles() != null && !folderList.getFiles().isEmpty())
            {
                List<RestFileInfo> restFileInfos = folderList.getFiles();
                for (RestFileInfo restFileInfo : restFileInfos) {
                    transThumbnailUrlList(restFileInfo);
                }
            }

            fillListUserInfo(folderList);
            if (request.getWithPath()!=null&&request.getWithPath()==true)
            {
                fillPathInfo(folderList);
            }
            return new ResponseEntity<RestFolderLists>(folderList, HttpStatus.OK);
        } catch (RuntimeException t) {
            // TODO parentID
            String[] logParams = new String[]{String.valueOf(ownerId),};
            String keyword = StringUtils.trimToEmpty(request.getName());
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.SEARCH_NODE_LISTS_ERR,
                    logParams,
                    keyword);
            throw t;
        }
    }

    private void fillPathInfo(RestFolderLists nodeList) {
        List<RestFolderInfo> folderList = nodeList.getFolders();
        List<RestFileInfo> fileList = nodeList.getFiles();
        List<RestBaseObject> pathList = null;
        for (RestFolderInfo tempFolder : folderList) {
            pathList = this.nodeManager.getNodePath(tempFolder.getOwnedBy(), tempFolder.getId());
            tempFolder.setPath(pathList);
        }
        for (RestFileInfo tempFile : fileList) {
            pathList = this.nodeManager.getNodePath(tempFile.getOwnedBy(), tempFile.getId());
            tempFile.setPath(pathList);
        }
    }

    /**
     * 外链鉴权，导致用户业务日志查询不完全
     *
     * @param ownerId
     * @param userToken
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

    /**
     * 获取节点密级
     *
     * @param ownerId
     * @param nodeId
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{nodeId}/secretLevel", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getNodeSecretLevel(@PathVariable Long ownerId, @PathVariable Long nodeId,
                                                @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);

            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            INode node = fileBaseService.getINodeInfo(ownerId, nodeId);
            String sha1 = "";
            if (node.getSha1() == null || node.getSha1().equals("")) {
                sha1 = node.getMd5();
            } else {
                sha1 = node.getSha1();
            }
            ObjectSecretLevel objectSecretLevel = null;
            if (sha1 == null || sha1.equals("")) {
                return new ResponseEntity<ObjectSecretLevel>(objectSecretLevel, HttpStatus.OK);
            }
            objectSecretLevel = ObjectSecretLevelService.getByAccountId(sha1, userToken.getRegionId(), userToken.getAccountId());

            return new ResponseEntity<ObjectSecretLevel>(objectSecretLevel, HttpStatus.OK);
        } catch (RuntimeException t) {
            INode nodeLog = fileBaseService.getAndCheckNodeForGetNodePath(ownerId, nodeId);
            String keyword = null;
            String parentId = null;
            if (nodeLog != null) {
                keyword = StringUtils.trimToEmpty(nodeLog.getName());
                parentId = String.valueOf(nodeLog.getParentId());
            }
            String[] logParams = new String[]{String.valueOf(ownerId), parentId};
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_NODE_PATH_ERR,
                    logParams,
                    keyword);
            throw t;
        }
    }

    @RequestMapping(value = "/{fileId}/secretLevel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateFileSecretLevel(@PathVariable Long ownerId, @RequestHeader("secretLevel") String secretLevel,
                                                   @PathVariable Long fileId, @RequestHeader("Authorization") String token, HttpServletRequest requestServlet) throws BaseRunException {
        UserToken userToken = null;
        INode node = null;
        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, fileId);

            // Token 验证
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId,
                    SecurityMethod.NODE_RENAME, headerCustomMap);

            node = fileBaseService.getAndCheckNode(ownerId, fileId, INode.TYPE_FILE);
            ObjectSecretLevelService.updateSecretLevel(node.getSha1(), userToken.getAccountId(), userToken.getRegionId(), Byte.parseByte(secretLevel));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException t) {
            String parentId = node != null ? String.valueOf(node.getParentId()) : null;
            String syncStatus = node != null ? String.valueOf(node.getSyncStatus()) : null;
            String[] logParams = new String[]{String.valueOf(ownerId), parentId, syncStatus};
            String keyword = StringUtils.trimToEmpty(userToken.getName());
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




    /**
     * 删除节点至回收站
     *
     * @param ownerId
     * @param nodeId
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{nodeId}/admin", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteNodeByAdmin(@PathVariable Long ownerId, @PathVariable Long nodeId,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		// Token 验证
		Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
		UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
		long adminId = userToken.getId();
		try {
			// 用户状态校验
			userTokenHelper.assembleUserToken(adminId, userToken);
			userTokenHelper.checkUserStatus(userToken.getAppId(), adminId);
			FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);
			userTokenHelper.checkAdminUser(ownerId, adminId, userToken);

			User owner = userService.get(ownerId);
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(owner);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);

			INode node = new INode();
			node.setId(nodeId);
			node.setOwnedBy(ownerId);
			node.setType(INode.TYPE_ALL);
			INode nodeInfo = fileBaseService.getAndCheckNode(node.getOwnedBy(), node.getId(), node.getType());
			nodeService.deleteNode(ownerToken, nodeInfo);
			linkApproveService.deleteByNodeId(ownerToken, node);
			trashServiceV2.deleteTrashItem(ownerToken, nodeInfo);
			ResponseEntity<String> rsp = new ResponseEntity<String>(HttpStatus.OK);

			return rsp;
		} catch (RuntimeException t) {
			throw t;
		}
	}

}
