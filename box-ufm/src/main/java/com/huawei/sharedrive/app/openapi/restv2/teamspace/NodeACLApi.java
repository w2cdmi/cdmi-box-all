package com.huawei.sharedrive.app.openapi.restv2.teamspace;

import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.INodeACLList;
import com.huawei.sharedrive.app.acl.service.INodeACLManger;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.teamspace.*;
import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 节点访问控制对外接口
 *
 * @author t00159390
 */
@Controller
@RequestMapping(value = "/api/v2/acl")
@Api(description = "节点访问控制对外接口")
public class NodeACLApi extends TeamSpaceCommonApi {
    @Autowired
    private INodeACLManger nodeACLManger;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private FileBaseService fileBaseService;

    @Autowired
    private INodeACLService iNodeACLService;


    @RequestMapping(value = "/{ownerId}/{nodeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getIsVisibleNodeACL(@PathVariable Long ownerId, @PathVariable Long nodeId, @RequestHeader("Authorization") String token,
                                                 HttpServletRequest request)
            throws BaseRunException {

        UserToken userToken = null;
        INodeACL inodeRole = null;
        try {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            // 参数校验
//            modifyRequest.checkParameter();
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);

            inodeRole = new INodeACL();
            inodeRole.setOwnedBy(ownerId);
            inodeRole.setiNodeId(nodeId);
            inodeRole.setAccessUserId(INodeACL.ID_SECRET);
            inodeRole.setUserType(INodeACL.TYPE_SECRET);
            inodeRole.setResourceRole(INodeACL.TYPE_SECRET);
            INodeACL iNodeACL = nodeACLManger.getNodeIsVisibleACL(userToken, inodeRole);
            if (iNodeACL == null) {
                return new ResponseEntity(false, HttpStatus.OK);
            } else {
                return new ResponseEntity(true, HttpStatus.OK);
            }
        } catch (RuntimeException t) {
            throw t;
        }
    }


    /**
     * 列举节点访问控制
     *
     * @param ownerId
     * @param listRequest
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{ownerId}", method = RequestMethod.POST)
    @ApiOperation(value = "列举节点访问控制")
    @ResponseBody
    public ResponseEntity<RestNodeACLList> listNodeACLs(@PathVariable Long ownerId, @RequestBody ListNodeACLRequest listRequest,
                                                        @RequestHeader("Authorization") String token, HttpServletRequest requestServlet) throws BaseRunException
    {
        UserToken userToken = null;
        INodeACLList nodeACLList = null;
        INode fNode = null;
        try {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            FilesCommonUtils.checkNonNegativeIntegers(ownerId);
            listRequest.checkParameter();

            // 设置每次返回结果数
            Limit limitObj = new Limit(listRequest.getOffset(), listRequest.getLimit());

            // 默认按照时间升序排列
            List<Order> orderList = getDefaultOrderList();

            // nodeId可以为null,表示列举整个资源空间的访问控制列表
            if (listRequest.getNodeId() != null) {
                if (listRequest.getNodeId() != INode.FILES_ROOT) {
                    fNode = fileBaseService.getINodeInfo(ownerId, listRequest.getNodeId());
                    if (null == fNode) {
                        throw new NoSuchItemsException("inode is null,ownerid:" + ownerId + ",id:"
                                + listRequest.getNodeId());
                    }
                }

                nodeACLList = nodeACLManger.listINodeACLs(userToken,
                        ownerId,
                        listRequest.getNodeId(),
                        orderList,
                        limitObj);
            } else {
                nodeACLList = nodeACLManger.listAllACLs(userToken, ownerId, orderList, limitObj);
            }

            return new ResponseEntity<RestNodeACLList>(new RestNodeACLList(nodeACLList), HttpStatus.OK);
        } catch (RuntimeException t) {
            String keyword = null;
            if (fNode != null) {
                keyword = StringUtils.trimToEmpty(fNode.getName());

            }
            UserLogType userLogType = UserLogType.LIST_NODE_LIST_ACL_ERR;
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    userLogType,
                    null,
                    keyword);
            throw t;
        }
    }

    /**
     * 添加节点访问控制
     *
     * @param createRequest
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "添加节点访问控制")
    @ResponseBody
    public ResponseEntity<RestNodeACLInfo> addNodeACL(@RequestBody RestNodeACLCreateRequest createRequest,
                                                      @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        String name = null;
        try {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            //系统用户，使用enterpriseId，作为userId
            if (createRequest.getUserList() != null) {
                for (RestTeamMember user : createRequest.getUserList()) {
                    if (user.getType().equals(INodeACL.TYPE_SYSTEM)) {
                        user.setId(userToken.getAccountVistor().getEnterpriseId().toString());
                    }
                }
            }

            createRequest.checkParameter();

            for (RestTeamMember user : createRequest.getUserList()) {
                INodeACL inodeRole = new INodeACL();
                inodeRole.setOwnedBy(createRequest.getResourceOwnerId());
                inodeRole.setiNodeId(createRequest.getResourceNodeId());
                inodeRole.setAccessUserId(user.getId());
                inodeRole.setUserType(user.getType());
                inodeRole.setResourceRole(createRequest.getRole());

                nodeACLManger.addINodeACL(userToken, inodeRole);
            }

            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (RuntimeException t) {
            if (checkNodeRequestNotNull(createRequest)) {
                INode logNode = fileBaseService.getINodeInfo(createRequest.getResourceOwnerId(), createRequest.getResourceNodeId());
                name = logNode != null ? logNode.getName() : null;
            }
            String[] logMsgs = null;
            if (userToken != null) {
                logMsgs = new String[]{StringUtils.trimToEmpty(userToken.getLoginName()),
                        String.valueOf(createRequest.getResourceOwnerId()), String.valueOf(createRequest.getResourceNodeId()),
                        String.valueOf(createRequest.getRole())};
            }
            String keyword = StringUtils.trimToEmpty(name);
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.ADD_NODE_ACL_ERR,
                    logMsgs,
                    keyword);
            throw t;
        }
    }

    /**
     * 修改节点访问控制
     *
     * @param modifyRequest
     * @param ownerId
     * @param id
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{ownerId}/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新节点访问控制")
    @ResponseBody
    public ResponseEntity<RestNodeACLInfo> modifyNodeACL(@RequestBody RestNodeACLModifyRequest modifyRequest,
                                                         @PathVariable Long ownerId, @PathVariable Long id, @RequestHeader("Authorization") String token,
                                                         HttpServletRequest request)
            throws BaseRunException {
        UserToken userToken = null;
        INodeACL inodeRole = null;
        try {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            // 参数校验
            modifyRequest.checkParameter();
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, id);

            inodeRole = new INodeACL();
            inodeRole.setOwnedBy(ownerId);
            inodeRole.setId(id);
            inodeRole.setResourceRole(modifyRequest.getRole());

            inodeRole = nodeACLManger.modifyINodeACLById(userToken, inodeRole);

            return new ResponseEntity<RestNodeACLInfo>(new RestNodeACLInfo(inodeRole), HttpStatus.OK);
        } catch (RuntimeException t) {
            INodeACL existACL = ownerId != null ? iNodeACLService.getINodeACLById(ownerId, id) : null;
            String[] logMsgs = null;
            String keyword = null;
            if (existACL != null) {
                String loginName = existACL.getUser() != null ? existACL.getUser().getLoginName() : null;
                if (existACL.getUser() != null && existACL.getUser().getLoginName() != null
                        && existACL.getUser().getLoginName().length() > Constants.MAX_NAME_LOG) {
                    loginName = existACL.getUser().getLoginName().substring(0, Constants.MAX_NAME_LOG);
                }
                logMsgs = new String[]{StringUtils.trimToEmpty(loginName),
                        StringUtils.trimToEmpty(existACL.getUserType()), String.valueOf(existACL.getOwnedBy()),
                        String.valueOf(existACL.getiNodeId())};
                if (INode.FILES_ROOT != existACL.getiNodeId()) {
                    INode node = fileBaseService.getINodeInfo(existACL.getOwnedBy(), existACL.getiNodeId());
                    keyword = node == null ? "" : node.getName();
                }
            } else {
                logMsgs = new String[]{null,
                        StringUtils.trimToEmpty(modifyRequest.getRole() != null ? modifyRequest.getRole() : null),
                        String.valueOf(ownerId), String.valueOf(id)};
            }
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.MODIFY_NODE_ACL_ERR,
                    logMsgs,
                    keyword);
            throw t;
        }
    }

    /**
     * 修改节点访问控制
     *
     * @param modifyRequest
     * @param ownerId
     * @param id
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/isVisible/{ownerId}/{id}", method = RequestMethod.POST)
    @ResponseBody
    public void modifyNodeIsVisibleACL(@RequestBody String isavalible,
                                       @PathVariable Long ownerId, @PathVariable Long id, @RequestHeader("Authorization") String token,
                                       HttpServletRequest request)
            throws BaseRunException {
        UserToken userToken = null;
        INodeACL inodeRole = null;
        try {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            // 参数校验
//            modifyRequest.checkParameter();
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, id);

            inodeRole = new INodeACL();
            inodeRole.setOwnedBy(ownerId);
            inodeRole.setiNodeId(id);
            inodeRole.setAccessUserId(INodeACL.ID_SECRET);
            inodeRole.setUserType(INodeACL.TYPE_SECRET);
            inodeRole.setResourceRole(INodeACL.TYPE_SECRET);
            inodeRole = nodeACLManger.modifyNodeIsVisibleACL(userToken, inodeRole, isavalible);


        } catch (RuntimeException t) {
            INodeACL existACL = ownerId != null ? iNodeACLService.getINodeACLById(ownerId, id) : null;
            String[] logMsgs = null;
            String keyword = null;
            if (existACL != null) {
                String loginName = existACL.getUser() != null ? existACL.getUser().getLoginName() : null;
                if (existACL.getUser() != null && existACL.getUser().getLoginName() != null
                        && existACL.getUser().getLoginName().length() > Constants.MAX_NAME_LOG) {
                    loginName = existACL.getUser().getLoginName().substring(0, Constants.MAX_NAME_LOG);
                }
                logMsgs = new String[]{StringUtils.trimToEmpty(loginName),
                        StringUtils.trimToEmpty(existACL.getUserType()), String.valueOf(existACL.getOwnedBy()),
                        String.valueOf(existACL.getiNodeId())};
                if (INode.FILES_ROOT != existACL.getiNodeId()) {
                    INode node = fileBaseService.getINodeInfo(existACL.getOwnedBy(), existACL.getiNodeId());
                    keyword = node == null ? "" : node.getName();
                }
            } else {
                logMsgs = new String[]{null,
                        StringUtils.trimToEmpty(INodeACL.TYPE_SECRET),
                        String.valueOf(ownerId), String.valueOf(id)};
            }
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.MODIFY_NODE_ACL_ERR,
                    logMsgs,
                    keyword);
            throw t;
        }
    }

    /**
     * 删除节点访问控制
     *
     * @param ownerId
     * @param id
     * @param token
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{ownerId}/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除节点访问控制" )
    @ResponseBody
    public void deleteNodeACL(@PathVariable Long ownerId, @PathVariable Long id,
                              @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        try {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, id);
            nodeACLManger.deleteINodeACLById(userToken, ownerId, id);
        } catch (RuntimeException t) {
            INodeACL existACL = ownerId != null ? iNodeACLService.getINodeACLById(ownerId, id) : null;
            String[] logMsgs = null;
            String keyword = null;
            if (existACL != null) {
                String loginName = existACL.getUser() != null ? existACL.getUser().getLoginName() : null;
                if (existACL.getUser() != null && existACL.getUser().getLoginName() != null
                        && existACL.getUser().getLoginName().length() > Constants.MAX_NAME_LOG) {
                    loginName = existACL.getUser().getLoginName().substring(0, Constants.MAX_NAME_LOG);
                }
                logMsgs = new String[]{StringUtils.trimToEmpty(loginName),
                        StringUtils.trimToEmpty(existACL.getUserType()), String.valueOf(existACL.getOwnedBy()),
                        String.valueOf(existACL.getiNodeId())};
                if (INode.FILES_ROOT != existACL.getiNodeId()) {
                    INode node = fileBaseService.getINodeInfo(existACL.getOwnedBy(), existACL.getiNodeId());
                    keyword = node == null ? "" : node.getName();
                }
            } else {
                logMsgs = new String[]{null, null, String.valueOf(ownerId), String.valueOf(id)};
            }
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.DELETE_NODE_ACL_ERR,
                    logMsgs,
                    keyword);
            throw t;
        }
    }

    private boolean checkNodeRequestNotNull(RestNodeACLCreateRequest createRequest) {
        boolean isNotNull = createRequest != null && createRequest.getResource() != null
                && createRequest.getResourceOwnerId() != null && createRequest.getResourceNodeId() != null;

        if (isNotNull) {
            return createRequest.getResourceOwnerId() >= 0 && createRequest.getResourceNodeId() >= 0;
        }

        return isNotNull;
    }

    /**
     * 获取ACL默认排序规则
     *
     * @return
     */
    private List<Order> getDefaultOrderList() {
        List<Order> orderList = new ArrayList<Order>(1);
        // 默认按照时间降序排列
        orderList.add(new Order("modifiedAt", "asc"));
        return orderList;
    }

}
