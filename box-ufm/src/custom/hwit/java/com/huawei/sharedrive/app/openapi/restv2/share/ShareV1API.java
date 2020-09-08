package com.huawei.sharedrive.app.openapi.restv2.share;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.core.domain.Limit;
import com.huawei.sharedrive.app.core.domain.Order;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.manager.PersistentEventManager;
import com.huawei.sharedrive.app.exception.AuthFailedException;
import com.huawei.sharedrive.app.exception.BadRquestException;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.group.service.GroupMembershipsService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.share.MySharesPage;
import com.huawei.sharedrive.app.openapi.domain.share.RestListShareResourceRequestV2;
import com.huawei.sharedrive.app.openapi.domain.share.RestPutShareRequestV2;
import com.huawei.sharedrive.app.openapi.domain.share.RestSharePageRequestV2;
import com.huawei.sharedrive.app.openapi.domain.share.SharePageV2;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.share.domain.INodeShare;
import com.huawei.sharedrive.app.share.domain.SharedUser;
import com.huawei.sharedrive.app.share.domain.UserType;
import com.huawei.sharedrive.app.share.service.SharePrivilegeService;
import com.huawei.sharedrive.app.share.service.ShareService;
import com.huawei.sharedrive.app.share.service.ShareToMeService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.BusinessConstants;

/**
 * 共享对外接口V1
 * 
 * 
 */
@Controller
@RequestMapping(value = "/api/v1")
public class ShareV1API
{
    
    private static final int DEFAULT_LIMIT = 1000;
    
    private static Logger logger = LoggerFactory.getLogger(ShareV1API.class);
    
    private final static int MAX_VALUE_OF_OFFSET = 1000;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private FolderService folderService;
    
    @Autowired
    private SecurityMatrixService securityMatrixService;
    
    @Autowired
    private SharePrivilegeService sharePrivilegeService;
    
    @Autowired
    private ShareService shareService;
    
    @Autowired
    private ShareToMeService shareToMeService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private PersistentEventManager persistentEventManager;
    
    @Autowired
    private GroupMembershipsService groupMembershipsService;
    
    /**
     * 删除共享关系
     * 
     * @param token
     * @param nodeId
     * @param sharedUserIds
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/shareships/{ownerId}/{nodeId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteSharedUser(@RequestHeader("Authorization") String token,
        @PathVariable("ownerId") long ownerId, @PathVariable("nodeId") long nodeId, Long userId, String type)
        throws BaseRunException
    {
        UserToken curUser = null;
        INode node = null;
        try
        {
            curUser = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            type = checkUserValid(ownerId, userId, type, curUser);
            node = checkAndGetNodeInfo(ownerId, nodeId, curUser);
            // 共享用户为空，则表示取消所有共享
            List<INodeShare> sharedList = null;
            if (null == userId)
            {
                sharedList = shareService.cancelAllShareV2(curUser, curUser.getId(), nodeId);
            }
            else
            {
                shareService.deleteShareV2(curUser, ownerId, userId, getUserType(type), nodeId);
                sharedList = new ArrayList<INodeShare>(1);
                INodeShare share = new INodeShare();
                share.setSharedUserId(userId);
                share.setSharedUserType(getUserType(type));
                sharedList.add(share);
            }
            
            // 发送取消共享消息
            sendCancelShareMessage(node, curUser, sharedList);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String name = null;
            String parentId = null;
            if (node != null)
            {
                name = StringUtils.trimToEmpty(node.getName());
                parentId = String.valueOf(node.getParentId());
            }
            String[] logParams = new String[]{
                StringUtils.trimToEmpty(curUser != null ? curUser.getLoginName() : null),
                String.valueOf(ownerId), parentId};
            String keyword = StringUtils.trimToEmpty(name);
            UserLogType userLogType = UserLogType.DELETE_SHARE_ERR;
            if (userId == null)
            {
                logParams = new String[]{String.valueOf(ownerId), parentId};
                userLogType = UserLogType.DELETE_ALL_SHARE_ERR;
            }
            fileBaseService.sendINodeEvent(curUser,
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
     * 列举共享资源
     * 
     * @param received
     * @param pageNumber
     * @param orderField
     * @param desc
     * @return
     * @throws BadRquestException
     * @throws AuthFailedException
     */
    @RequestMapping(value = "/shares/received", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<SharePageV2> list(@RequestHeader("Authorization") String token,
        @RequestBody RestListShareResourceRequestV2 rv) throws BaseRunException
    {
        UserToken curUser = null;
        try
        {
            curUser = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            checkAndPackListMyShareRequest(rv);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(curUser.getAppId(), curUser.getCloudUserId());
            RestSharePageRequestV2 pageRequest = checkFormatSharePage(rv);
            SharePageV2 page = shareToMeService.listShareToMeForClientV2(curUser,
                curUser.getId(),
                pageRequest);
            page.setLimit(rv.getLimit());
            page.setOffset(rv.getOffset());
            return new ResponseEntity<SharePageV2>(page, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String[] logParams = new String[]{curUser != null ? String.valueOf(curUser.getId()) : null, null};
            String keyword = rv != null ? StringUtils.trimToEmpty(rv.getKeyword()) : null;
            fileBaseService.sendINodeEvent(curUser,
                EventType.OTHERS,
                null,
                null,
                UserLogType.LIST_SHARETO_ME_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    /**
     * 列举共享资源
     * 
     * @param received
     * @param pageNumber
     * @param orderField
     * @param desc
     * @return
     * @throws BadRquestException
     * @throws AuthFailedException
     */
    @RequestMapping(value = "/shares/distributed", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<MySharesPage> listMyShares(@RequestHeader("Authorization") String token,
        @RequestBody RestListShareResourceRequestV2 rv) throws BaseRunException
    {
        UserToken curUser = null;
        try
        {
            curUser = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            userTokenHelper.checkUserStatus(curUser.getAppId(), curUser.getCloudUserId());
            checkAndPackListMyShareRequest(rv);
            RestSharePageRequestV2 pageRequest = checkFormatSharePage(rv);
            MySharesPage page = shareService.listMyShares(curUser, pageRequest);
            return new ResponseEntity<MySharesPage>(page, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String[] logParams = new String[]{curUser != null ? String.valueOf(curUser.getId()) : null, null};
            String keyword = StringUtils.trimToEmpty(rv != null ? rv.getKeyword() : null);
            fileBaseService.sendINodeEvent(curUser,
                EventType.OTHERS,
                null,
                null,
                UserLogType.LIST_MY_SHARES_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    /**
     * 列举共享关系 根据被共享用户类型和名称进行升序排序
     * 
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/shareships/{ownerId}/{nodeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<SharePageV2> listSharedUsers(@RequestHeader("Authorization") String token,
        @PathVariable("ownerId") long ownerId, @PathVariable("nodeId") long nodeId, Long offset, Integer limit)
        throws BaseRunException
    {
        UserToken curUser = null;
        INode node = null;
        try
        {
            curUser = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            userTokenHelper.checkUserStatus(curUser.getAppId(), ownerId);
            if (curUser.getCloudUserId() != ownerId)
            {
                throw new AuthFailedException();
            }
            offset = checkOffset(offset);
            limit = checkLimit(limit);
            node = folderService.getNodeInfo(curUser, ownerId, nodeId);
            if (node == null)
            {
                throw new NoSuchItemsException();
            }
            List<Order> defaultOrders = getDefaultOrders();
            Limit tempLimit = new Limit(offset, limit);
            SharePageV2 userList = shareService.getShareUserListOrderV2(curUser,
                ownerId,
                nodeId,
                defaultOrders,
                tempLimit);
            return new ResponseEntity<SharePageV2>(userList, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String parentId = null;
            String keyword = null;
            if (node != null)
            {
                keyword = StringUtils.trimToEmpty(node.getName());
                parentId = String.valueOf(node.getParentId());
            }
            String[] logParams = new String[]{String.valueOf(ownerId), parentId};
            fileBaseService.sendINodeEvent(curUser,
                EventType.OTHERS,
                null,
                null,
                UserLogType.LIST_SHARE_USERS_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    /**
     * 添加共享关系并且自动发送邮件通知
     * 
     * @param ownerId 资源拥有者或团队空间ID
     * @param nodeId 文件夹或文件ID
     * @return
     * @throws InternalServerErrorException
     */
    @RequestMapping(value = "/shareships/{ownerId}/{nodeId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> putShare(@PathVariable long ownerId, @PathVariable long nodeId,
        @RequestHeader("Authorization") String token, @RequestBody RestPutShareRequestV2 putShareRequest)
        throws BaseRunException
    {
        UserToken curUser = null;
        INode node = null;
        List<SharedUser> shareList = new ArrayList<SharedUser>(BusinessConstants.INITIAL_CAPACITIES);
        try
        {
            if (putShareRequest.getSharedUser() == null)
            {
                throw new BadRquestException();
            }
            putShareRequest.checkType();
            putShareRequest.checkAdditionalLog();
            shareList.add(putShareRequest.getSharedUser());
            curUser = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            userTokenHelper.checkUserStatus(curUser.getAppId(), ownerId);
            securityMatrixService.checkSecurityMatrix(curUser, ownerId, nodeId, SecurityMethod.NODE_SETSHARE, null);
            sharePrivilegeService.checkPrivilege(curUser, ownerId, nodeId);
            
            node = checkAndGetNodeInfo(ownerId, nodeId, curUser);
            
            String additionalLog = putShareRequest.getAdditionalLog();
            
            List<INodeShare> failedList = shareService.addShareListV2(curUser,
                ownerId,
                shareList,
                nodeId,
                putShareRequest.getRoleName(),
                "",
                additionalLog);
            if (failedList.isEmpty())
            {
                // 发送消息
                sendShareMessage(node, shareList, curUser);
                return new ResponseEntity<List<INodeShare>>(HttpStatus.OK);
            }
            else
            {
                throw new InternalServerErrorException();
            }
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
            if (!shareList.isEmpty())
            {
                try
                {
                    User shareUser = null;
                    for (SharedUser sharedUser : shareList)
                    {
                        shareUser = userService.get(sharedUser.getId());
                        sendINodeEvent(ownerId, curUser, keyword, parentId, shareUser);
                    }
                }
                catch (Exception e)
                {
                    logger.warn("", e);
                }
            }
            else
            {
                String[] logParams = new String[]{null, String.valueOf(ownerId), parentId};
                fileBaseService.sendINodeEvent(curUser,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.ADD_SHARE_ERR,
                    logParams,
                    keyword);
            }
            throw t;
        }
    }
    
    @RequestMapping(value = "/{inodeId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> putShareOld(@PathVariable long inodeId,
        @RequestHeader("Authorization") String token, @RequestBody List<INodeShare> shareList)
    {
        if (CollectionUtils.isEmpty(shareList))
        {
            return new ResponseEntity<String>("shareList is empty", HttpStatus.BAD_REQUEST);
        }
        UserToken curUser = null;
        try
        {
            // curUser = userTokenHelper.checkTokenAndGetUser(token);
            curUser = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            securityMatrixService.checkSecurityMatrix(curUser,
                curUser.getId(),
                inodeId,
                SecurityMethod.NODE_SETSHARE,
                null);
        }
        catch (Exception e)
        {
            logger.error("token invalid whenputShare: " + token, e);
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
        try
        {
            List<SharedUser> sharedUserList = new ArrayList<SharedUser>(16);
            SharedUser tempSharedUser;
            for (INodeShare tempNodeShare : shareList)
            {
                tempSharedUser = new SharedUser();
                tempSharedUser.setId(tempNodeShare.getSharedUserId());
                tempSharedUser.setType("user");
                sharedUserList.add(tempSharedUser);
            }
            
            List<INodeShare> failedList = shareService.addShareListV2(curUser,
                curUser.getCloudUserId(),
                sharedUserList,
                inodeId,
                "viewer",
                "",
                "");
            
            if (failedList.size() < shareList.size())
            {
                return new ResponseEntity<List<INodeShare>>(HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity<List<INodeShare>>(failedList, HttpStatus.BAD_REQUEST);
            }
        }
        catch (ForbiddenException e)
        {
            logger.error("", e);
            return new ResponseEntity<List<INodeShare>>(HttpStatus.FORBIDDEN);
        }
        catch (NoSuchItemsException e1)
        {
            logger.error("", e1);
            return new ResponseEntity<String>("Can not find the node for " + inodeId, HttpStatus.NOT_FOUND);
        }
        catch (Exception e)
        {
            logger.error("", e);
            return new ResponseEntity<List<INodeShare>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private INode checkAndGetNodeInfo(long ownerId, long nodeId, UserToken curUser)
    {
        INode node;
        node = folderService.getNodeInfo(curUser, ownerId, nodeId);
        if (null == node)
        {
            throw new NoSuchItemsException();
        }
        return node;
    }
    
    private void checkAndPackListMyShareRequest(RestListShareResourceRequestV2 rv) throws BaseRunException
    {
        if (null == rv.getLimit())
        {
            rv.setLimit(100);
        }
        else if (rv.getLimit() <= 0 || rv.getLimit() > 1000)
        {
            throw new InvalidParamException("Bad limit " + rv.getLimit());
        }
        if (null == rv.getOffset())
        {
            rv.setOffset(0L);
        }
        else if (rv.getOffset() < 0)
        {
            throw new InvalidParamException("Bad offset " + rv.getOffset());
        }
        if (CollectionUtils.isEmpty(rv.getOrder()))
        {
            Order order = new Order(OrderConsts.DEFAULT_MYSHARE_FIELD, OrderConsts.DIRECTION_ASC);
            List<Order> orderList = new ArrayList<Order>(1);
            orderList.add(order);
            rv.setOrder(orderList);
        }
        else
        {
            for (Order order : rv.getOrder())
            {
                checkReceivedOrder(order);
            }
        }
        ThumbnailRequestChecker.checkThumbnail(rv.getThumbnail());
    }
    
    /**
     * 检查请求数据，并封装数据
     * 
     * @param pageRequest
     * @throws BadRquestException
     * @throws InvalidParamException
     */
    private RestSharePageRequestV2 checkFormatSharePage(RestListShareResourceRequestV2 rv)
        throws BadRquestException
    {
        if (rv.getLimit() < 0)
        {
            throw new InvalidParamException();
        }
        if (rv.getOffset() < 0 || rv.getOffset() > MAX_VALUE_OF_OFFSET)
        {
            throw new InvalidParamException();
        }
        RestSharePageRequestV2 request = new RestSharePageRequestV2();
        request.setKeyword(StringUtils.trimToEmpty(rv.getKeyword()));
        request.setLimit(rv.getLimit());
        request.setOffset(rv.getOffset());
        request.setOrderList(rv.getOrder());
        ThumbnailRequestChecker.checkThumbnail(rv.getThumbnail());
        request.setThumbnail(rv.getThumbnail());
        return request;
    }
    
    private Integer checkLimit(Integer limit) throws InvalidParamException
    {
        if (null == limit)
        {
            limit = DEFAULT_LIMIT;
        }
        if (limit <= 0 || limit > DEFAULT_LIMIT)
        {
            throw new InvalidParamException("Invalid limit " + limit);
        }
        return limit;
    }
    
    private Long checkOffset(Long offset) throws InvalidParamException
    {
        if (null == offset)
        {
            offset = 0L;
        }
        if (offset < 0)
        {
            throw new InvalidParamException("Invalid ofset " + offset);
        }
        return offset;
    }
    
    private void checkReceivedOrder(Order order) throws BaseRunException
    {
        if (!OrderConsts.FIELD_NAME.equalsIgnoreCase(order.getField())
            && !OrderConsts.FIELD_SIZE.equalsIgnoreCase(order.getField())
            && !OrderConsts.FIELD_TYPE.equalsIgnoreCase(order.getField())
            && !OrderConsts.FIELD_TIME.equalsIgnoreCase(order.getField())
            && !OrderConsts.FIELD_OWNERNAME.equalsIgnoreCase(order.getField()))
        {
            throw new InvalidParamException("Bad order field " + order.getField());
        }
        if (!OrderConsts.DIRECTION_ASC.equalsIgnoreCase(order.getDirection())
            && !OrderConsts.DIRECTION_DESC.equalsIgnoreCase(order.getDirection()))
        {
            throw new InvalidParamException("Bad order direction " + order.getDirection());
        }
    }
    
    private String checkUserValid(long ownerId, Long userId, String type, UserToken curUser)
    {
        userTokenHelper.checkUserStatus(curUser.getAppId(), curUser.getCloudUserId());
        if (curUser.getId() != ownerId && curUser.getId() != userId)
        {
            throw new ForbiddenException();
        }
        if (null == type)
        {
            type = INodeACL.TYPE_USER;
        }
        else if (!type.equals(INodeACL.TYPE_USER) && !type.equals(INodeACL.TYPE_GROUP))
        {
            throw new InvalidParamException();
        }
        return type;
    }
    
    private PersistentEvent generalShareEvent(EventType eventType, INode node, long providerId,
        long receiverId)
    {
        PersistentEvent event = new PersistentEvent();
        event.setEventType(eventType);
        event.setNodeId(node.getId());
        event.setNodeName(node.getName());
        event.setNodeType(node.getType());
        event.setOwnedBy(node.getOwnedBy());
        event.setParentId(node.getParentId());
        event.addParameter("receiverId", receiverId);
        event.addParameter("providerId", providerId);
        return event;
    }
    
    private List<Order> getDefaultOrders()
    {
        List<Order> defaultOrders = new ArrayList<Order>(2);
        Order type = new Order("sharedUserType", "DESC");
        Order name = new Order("sharedUserName", "ASC");
        defaultOrders.add(type);
        defaultOrders.add(name);
        return defaultOrders;
    }
    
    /**
     * 换算用户类型
     * 
     * @param userType
     * @return
     */
    private byte getUserType(String userType)
    {
        if (userType.equals(INodeACL.TYPE_USER))
        {
            return UserType.TYPE_USER;
        }
        return UserType.TYPE_GROUP;
    }
    
    private void sendCancelShareMessage(INode node, User provider, List<INodeShare> sharedList)
    {
        // 取消共享
        if (CollectionUtils.isEmpty(sharedList))
        {
            return;
        }
        
        List<GroupMemberships> members = null;
        GroupMemberships groupMemberShip = new GroupMemberships();
        PersistentEvent event = null;
        for (INodeShare share : sharedList)
        {
            // 取消群组共享
            if (UserType.TYPE_GROUP == share.getSharedUserType())
            {
                groupMemberShip.setGroupId(share.getSharedUserId());
                members = groupMembershipsService.getMemberList(null, null, groupMemberShip, null, null);
                for (GroupMemberships memberShip : members)
                {
                    if (memberShip.getUserId() == provider.getId())
                    {
                        continue;
                    }
                    
                    event = generalShareEvent(EventType.SHARE_DELETE,
                        node,
                        provider.getId(),
                        memberShip.getUserId());
                    persistentEventManager.fireEvent(event);
                }
            }
            else
            // 取消个人共享
            {
                event = generalShareEvent(EventType.SHARE_DELETE,
                    node,
                    provider.getId(),
                    share.getSharedUserId());
                persistentEventManager.fireEvent(event);
            }
        }
    }
    
    private void sendINodeEvent(long ownerId, UserToken curUser, String keyword, String parentId,
        User shareUser)
    {
        String[] logParams;
        if (shareUser != null)
        {
            logParams = new String[]{StringUtils.trimToEmpty(shareUser.getLoginName()),
                String.valueOf(ownerId), parentId};
            fileBaseService.sendINodeEvent(curUser,
                EventType.OTHERS,
                null,
                null,
                UserLogType.ADD_SHARE_ERR,
                logParams,
                keyword);
        }
    }
    
    private void sendShareMessage(INode node, List<SharedUser> shareList, User provider)
    {
        if (CollectionUtils.isEmpty(shareList))
        {
            return;
        }
        List<GroupMemberships> members = null;
        GroupMemberships groupMemberShip = new GroupMemberships();
        PersistentEvent event = null;
        for (SharedUser sharedUser : shareList)
        {
            // 共享给群组
            if (INodeACL.TYPE_GROUP.equals(sharedUser.getType()))
            {
                groupMemberShip.setGroupId(sharedUser.getId());
                members = groupMembershipsService.getMemberList(null, null, groupMemberShip, null, null);
                for (GroupMemberships memberShip : members)
                {
                    if (memberShip.getUserId() == provider.getId())
                    {
                        continue;
                    }
                    event = generalShareEvent(EventType.SHARE_CREATE,
                        node,
                        provider.getId(),
                        memberShip.getUserId());
                    persistentEventManager.fireEvent(event);
                }
            }
            else
            {
                // 共享给个人
                event = generalShareEvent(EventType.SHARE_CREATE, node, provider.getId(), sharedUser.getId());
                persistentEventManager.fireEvent(event);
            }
        }
        
    }
}
