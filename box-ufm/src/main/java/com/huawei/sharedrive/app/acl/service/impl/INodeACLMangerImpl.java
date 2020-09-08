package com.huawei.sharedrive.app.acl.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.INodeACLList;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.acl.service.INodeACLIdGenerateService;
import com.huawei.sharedrive.app.acl.service.INodeACLManger;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.acl.service.ResourceRoleService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.InvalidPermissionRoleException;
import com.huawei.sharedrive.app.exception.NoSuchACLException;
import com.huawei.sharedrive.app.exception.NoSuchGroupException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.exception.NoSuchUserException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.FileBasicConfig;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.group.service.GroupService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.Department;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.DepartmentService;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.LogEvent;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Component
public class INodeACLMangerImpl implements INodeACLManger
{
    private static final Logger LOGGER = LoggerFactory.getLogger(INodeACLMangerImpl.class);
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private INodeACLService iNodeACLService;
    
    @Autowired
    private ResourceRoleService resourceRoleService;
    
    @Autowired
    private INodeACLIdGenerateService nodeACLIdGenerateService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private TeamSpaceService teamSpaceService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private SystemRoleUtil systemRoleUtil;
    
    @Autowired
    private GroupService groupService;
    
	@Autowired
	private DepartmentService departmentService;
    
    @Override
    public INodeACL addINodeACL(UserToken userToken, INodeACL iNodeACL) throws BaseRunException
    {
    	String enterpriseId = "";
		if (userToken.getAccountVistor() != null) {
			enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
		}
        // 是否具有授权权限判断
        checkAclOperPermission(iNodeACL.getOwnedBy(), userToken.getId(),enterpriseId,userToken);
        String keyword = null;
        // 检查资源是否存在
        
        String loginName = null;
        if (iNodeACL.getiNodeId() != INode.FILES_ROOT)
        {
            INode fNode = fileBaseService.getINodeInfo(iNodeACL.getOwnedBy(), iNodeACL.getiNodeId());
            if (null == fNode)
            {
                LOGGER.error("inode is null,ownerid:" + iNodeACL.getOwnedBy() + ",id:"
                    + iNodeACL.getiNodeId());
                throw new NoSuchItemsException("inode is null,ownerid:" + iNodeACL.getOwnedBy() + ",id:"
                    + iNodeACL.getiNodeId());
            }
            keyword = StringUtils.trimToEmpty(fNode.getName());
            iNodeACL.setiNodePid(fNode.getParentId());
        }
        else
        {
            // 判断用户是否存在
            User user = userService.get(iNodeACL.getOwnedBy());
            if (user == null)
            {
                String errorMsg = "ownedBy is not exist, or regionId = -1 ,userId:" + iNodeACL.getOwnedBy();
                throw new NoSuchItemsException(errorMsg);
            }
            keyword = StringUtils.trimToEmpty(user.getName());
            iNodeACL.setiNodePid(INode.FILES_ROOT);
        }
        
        // 判断用户是否存在 : 需要限定是否团队空间用户
        if (INodeACL.TYPE_USER.equals(iNodeACL.getUserType()))
        {
            User user = userService.get(Long.parseLong(iNodeACL.getAccessUserId()));
            if (user == null|| teamSpaceService.getTeamSpaceNoCheck(Long.parseLong(iNodeACL.getAccessUserId())) != null)
            {
                String errorMsg = "user is not exist, userId:" + iNodeACL.getAccessUserId();
                throw new NoSuchUserException(errorMsg);
            }
            loginName = user.getLoginName();
        }
        else if (INodeACL.TYPE_GROUP.equals(iNodeACL.getUserType()))
        {
            Group group = groupService.get(Long.valueOf(iNodeACL.getAccessUserId()));
            if (group == null)
            {
                String errorMsg = "group is not exist, groupId:" + iNodeACL.getAccessUserId();
                throw new NoSuchGroupException(errorMsg);
            }
            loginName = group.getName();
        }
        else if (INodeACL.TYPE_TEAM.equals(iNodeACL.getUserType()))
        {
            if (teamSpaceService.getTeamSpaceNoCheck(iNodeACL.getOwnedBy()) == null)
            {
                String errorMsg = "usertype error, ownerId is not a teamSpace";
                throw new InvalidParamException(errorMsg);
            }
        }
        
        // 判段role的有效性
        checkRoleValid(iNodeACL.getResourceRole());
        
        // 判断是否允许子目录设置更小的权限
        checkTopAcls(userToken,
            iNodeACL.getResourceRole(),
            iNodeACL.getAccessUserId(),
            iNodeACL.getUserType(),
            new INode(iNodeACL.getOwnedBy(), iNodeACL.getiNodeId()));
        
        Date date = new Date();
        iNodeACL.setId(nodeACLIdGenerateService.getNextNodeACLId(iNodeACL.getOwnedBy()));
        iNodeACL.setCreatedAt(date);
        iNodeACL.setCreatedBy(userToken.getId());
        iNodeACL.setOwnedBy(iNodeACL.getOwnedBy());
        iNodeACL.setModifiedAt(date);
        iNodeACL.setModifiedBy(userToken.getId());
        iNodeACL = iNodeACLService.addINodeACL(iNodeACL);
        
        // 判断是否需要覆盖子目录的acl
        overwriteSubAcls(userToken, iNodeACL);
        
        // 判断父目录是否需要添加权限
//        overwriteParentAcls(userToken, iNodeACL);
        
        
        // 发送事件
        createEvent(userToken, EventType.ACL_CREATE, new INode(iNodeACL.getOwnedBy(), iNodeACL.getiNodeId()));
        if (loginName != null && loginName.length() > Constants.MAX_NAME_LOG)
        {
            loginName = loginName.substring(0, Constants.MAX_NAME_LOG);
        }
        String[] logMsgs = new String[]{StringUtils.trimToEmpty(loginName),
            StringUtils.trimToEmpty(iNodeACL.getUserType()), String.valueOf(iNodeACL.getOwnedBy()),
            String.valueOf(iNodeACL.getiNodeId())};
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.ADD_NODE_ACL,
            logMsgs,
            keyword);
        return iNodeACL;
    }
    
    @Override
    public void deleteINodeACLById(UserToken user, long ownedBy, long id) throws BaseRunException
    {
    	String enterpriseId = "";
		if (user.getAccountVistor() != null) {
			enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
		}
        // 是否具有授权权限判断
        checkAclOperPermission(ownedBy, user.getId(),enterpriseId,user);
        
        // acl是否存在判断
        INodeACL existACL = iNodeACLService.getINodeACLById(ownedBy, id);
        
        if (existACL == null)
        {
            String excepMessage = "acl not exist,ownerid:" + ownedBy + ",id:" + id;
            throw new NoSuchACLException(excepMessage);
        }
        
        iNodeACLService.deleteINodeACLById(ownedBy, id);
        // 发送事件
        User userInfo = null;
        if (INodeACL.TYPE_USER.equals(existACL.getUserType()))
        {
            userInfo = userService.get(Long.valueOf(existACL.getAccessUserId()));
        }
        else if (INodeACL.TYPE_GROUP.equals(existACL.getUserType()))
        {
            // group暂时设置为空
            userInfo = null;
        }
        createEvent(user, EventType.ACL_DELETE, new INode(existACL.getOwnedBy(), existACL.getiNodeId()));
        
        String keyword = null;
        if (INode.FILES_ROOT != existACL.getiNodeId())
        {
            INode node = fileBaseService.getINodeInfo(existACL.getOwnedBy(), existACL.getiNodeId());
            keyword = node == null ? "" : node.getName();
        }
        else
        {
            User existUser = userService.get(existACL.getOwnedBy());
            if (existUser != null)
            {
                keyword = existUser.getLoginName();
            }
        }
        String loginName = null;
        if (userInfo != null)
        {
            loginName = userInfo.getLoginName();
        }
        if (loginName != null && loginName.length() > Constants.MAX_NAME_LOG)
        {
            loginName = loginName.substring(0, Constants.MAX_NAME_LOG);
        }
        String[] logMsgs = new String[]{StringUtils.trimToEmpty(loginName),
            StringUtils.trimToEmpty(existACL.getUserType()), String.valueOf(existACL.getOwnedBy()),
            String.valueOf(existACL.getiNodeId())};
        
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.DELETE_NODE_ACL,
            logMsgs,
            keyword);
    }
    
    @Override
    public void deleteINodeAllACLs(UserToken user, long ownedBy, long nodeId) throws BaseRunException
    {
    	String enterpriseId = "";
		if (user.getAccountVistor() != null) {
			enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
		}
        // 是否具有授权权限判断
        checkAclOperPermission(ownedBy, user.getId(),enterpriseId,user);
        
        iNodeACLService.deleteINodeAllACLs(ownedBy, nodeId);
        
        // 发送事件
        createEvent(user, EventType.ACL_DELETE, new INode(ownedBy, nodeId));
    }
    
    @Override
    public INodeACL getINodeACLById(UserToken user, long ownedBy, long id) throws BaseRunException
    {
        // acl是否存在判断
        INodeACL existACL = iNodeACLService.getINodeACLById(ownedBy, id);
        
        if (existACL == null)
        {
            String excepMessage = "acl not exist,ownerid:" + ownedBy + ",id:" + id;
            throw new NoSuchItemsException(excepMessage);
        }
        
        return existACL;
    }
    
    @Override
    public ACL getINodePermissionsByLink(UserToken userToken, Long ownerId, Long nodeId, String linkCode)
        throws BaseRunException
    {
        // 如果是非root目录需要判断节点资源是否存在
        INode fNode = null;
        String[] logMsgs = null;
        String keyword = null;
        if (nodeId != INode.FILES_ROOT)
        {
            fNode = fileBaseService.getINodeInfo(ownerId, nodeId);
            if (null == fNode)
            {
                throw new NoSuchItemsException("inode is null,ownerid:" + ownerId + ",id:" + nodeId);
            }
            keyword = StringUtils.trimToEmpty(fNode.getName());
        }
        else
        {
            String errorMsg = "node id is 0";
            throw new NoSuchItemsException(errorMsg);
        }
        
        logMsgs = new String[]{StringUtils.trimToEmpty(userToken.getLinkCode()), String.valueOf(nodeId)};
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_NODE_PERMISSION,
            logMsgs,
            keyword);
        return iNodeACLService.getACLForLink(userToken.getLinkCode(), fNode);
    }
    
    @Override
    public ACL getINodePermissionsByUser(UserToken userToken, Long ownerId, Long nodeId, long userId,
        String userType) throws BaseRunException
    {
    	String enterpriseId = "";
		if (userToken.getAccountVistor() != null) {
			enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
		}
        // 是否具有授权权限判断，不允许获取别人的权限
        if (userId != userToken.getId())
        {
            checkAclOperPermission(ownerId, userToken.getId(),enterpriseId,userToken);
        }
        
        // 如果是非root目录需要判断节点资源是否存在
        INode fNode = null;
        String[] logMsgs = null;
        String keyword = null;
        if (nodeId != INode.FILES_ROOT)
        {
            fNode = fileBaseService.getINodeInfo(ownerId, nodeId);
            if (null == fNode || fNode.getStatus() == INode.STATUS_DELETE)
            {
                throw new NoSuchItemsException("inode is null,ownerid:" + ownerId + ",id:" + nodeId);
            }
            keyword = StringUtils.trimToEmpty(fNode.getName());
        }
        else
        {
            // 判断用户是否存在
            User user = userService.get(null, ownerId);
            if (user == null)
            {
                String errorMsg = "ownedBy is not exist, or regionId = -1 ,userId:" + ownerId;
                throw new NoSuchItemsException(errorMsg);
            }
            keyword = user.getLoginName();
            fNode = new INode(ownerId, INode.FILES_ROOT);
        }
        
        // 判断用户是否存在
        User user = userService.get(null, userId);
        if (user == null)
        {
            String errorMsg = "userId is not exist,userId:" + userId;
            throw new NoSuchUserException(errorMsg);
        }
        logMsgs = new String[]{StringUtils.trimToEmpty(user.getLoginName()), String.valueOf(nodeId)};
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_NODE_PERMISSION,
            logMsgs,
            keyword);
        return iNodeACLService.getACLForAccessUser(userId, userType, fNode, enterpriseId,userToken);
    }
    
    @Override
    public INodeACLList listAllACLs(UserToken user, long ownerId, List<Order> orderList, Limit limit)
        throws BaseRunException
    {
    	String enterpriseId = "";
		if (user.getAccountVistor() != null) {
			enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
		}
        // 判断用户是否存在
        User user1 = userService.get(ownerId);
        if (user1 == null || user1.getRegionId() == -1)
        {
            String errorMsg = "ownedBy is not exist, or regionId = -1 ,userId:" + ownerId;
            throw new NoSuchItemsException(errorMsg);
        }
        // 是否具有授权权限判断
        checkAclOperPermission(ownerId, user.getId(),enterpriseId,user);
        
        INodeACLList nodeACLList = iNodeACLService.listAllACLs(ownerId, orderList, limit);
        // 设置返回体中的用户信息
        List<INodeACL> itemList = nodeACLList.getNodeACLs();
        User userInfo = null;
        for (INodeACL item : itemList)
        {
            if (INodeACL.TYPE_USER.equals(item.getUserType()))
            {
                userInfo = userService.get(Long.valueOf(item.getAccessUserId()));
            }
            else
            // TODO 先暂时设置为空
            {
                userInfo = null;
            }
            item.setUser(userInfo);
        }
        String[] logMsgs = new String[]{String.valueOf(ownerId), String.valueOf(INode.FILES_ROOT)};
        String keyword = "Total:" + String.valueOf(nodeACLList.getTotalCount());
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.LIST_NODE_ALLLIST_ACL,
            logMsgs,
            keyword);
        return nodeACLList;
    }
    
    @Override
    public INodeACLList listINodeACLs(UserToken userToken, long ownerId, long nodeId, List<Order> orderList,
        Limit limit) throws BaseRunException
    {
    	String enterpriseId = "";
		if (userToken.getAccountVistor() != null) {
			enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
		}
        INode fNode = null;
        // 检查资源是否存在
        if (nodeId != INode.FILES_ROOT)
        {
            fNode = fileBaseService.getINodeInfo(ownerId, nodeId);
            if (null == fNode)
            {
                throw new NoSuchItemsException("inode is null,ownerid:" + ownerId + ",id:" + nodeId);
            }
        }
        else
        {
            // 判断用户是否存在
            User user = userService.get(ownerId);
            if (user == null)
            {
                String errorMsg = "ownedBy is not exist, or regionId = -1 ,userId:" + ownerId;
                throw new NoSuchItemsException(errorMsg);
            }
        }
        
        // 是否具有授权权限判断
        checkAclOperPermission(ownerId, userToken.getId(),enterpriseId,userToken);
        
        INodeACLList nodeACLList = iNodeACLService.listINodeACLs(ownerId, nodeId, orderList, limit);
        // 设置返回体中的用户信息
        List<INodeACL> itemList = nodeACLList.getNodeACLs();
        User userInfo = null;
        for (INodeACL item : itemList)
        {
            if (INodeACL.TYPE_USER.equals(item.getUserType()))
            {
                userInfo = userService.get(Long.valueOf(item.getAccessUserId()));
            }
            else if (INodeACL.TYPE_GROUP.equals(item.getUserType()))
            {
                Group group = groupService.get(Long.valueOf(item.getAccessUserId()));
                userInfo = new User();
                if (group != null)
                {
                    userInfo.setName(group.getName());
                    userInfo.setId(group.getId());
                }
            }else if (INodeACL.TYPE_DEPT.equals(item.getUserType()))
            {
            	Department department = departmentService.getByEnterpriseIdAndDepartmentCloudUserId(userToken.getAccountVistor().getEnterpriseId(), Long.valueOf(item.getAccessUserId()));
                userInfo = new User();
                if (department != null)
                {
                    userInfo.setName(department.getName());
                    userInfo.setLoginName(department.getName());
                    userInfo.setId(department.getDepartmentId());
                }
            }
            else
            // TODO 先暂时设置为空
            {
                userInfo = null;
            }
            item.setUser(userInfo);
        }
        String[] logMsgs = null;
        String keyword = "";
        if (fNode != null)
        {
            logMsgs = new String[]{String.valueOf(ownerId), String.valueOf(fNode.getParentId())};
            keyword = StringUtils.trimToEmpty(fNode.getName());
        }
        else
        {
            logMsgs = new String[]{String.valueOf(ownerId), String.valueOf(INode.FILES_ROOT)};
        }
        
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.LIST_NODE_LIST_ACL,
            logMsgs,
            keyword);
        return nodeACLList;
    }
    
    @Override
    public INodeACL modifyINodeACLById(UserToken user, INodeACL iNodeACL) throws BaseRunException
    {
    	String enterpriseId = "";
		if (user.getAccountVistor() != null) {
			enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
		}
        // 是否具有授权权限判断
        checkAclOperPermission(iNodeACL.getOwnedBy(), user.getId(),enterpriseId,user);
        
        // acl是否存在判断
        INodeACL existACL = iNodeACLService.getINodeACLById(iNodeACL.getOwnedBy(), iNodeACL.getId());
        
        if (existACL == null)
        {
            String excepMessage = "acl not exist,ownerid:" + iNodeACL.getOwnedBy() + ", inodeId:"
                + iNodeACL.getiNodeId() + ",id:" + iNodeACL.getId();
            throw new NoSuchACLException(excepMessage);
        }
        
        // 判读role的有效性
        checkRoleValid(iNodeACL.getResourceRole());
        
        // 判断是否允许子目录设置更小的权限
        checkTopAcls(user,
            iNodeACL.getResourceRole(),
            existACL.getAccessUserId(),
            existACL.getUserType(),
            new INode(existACL.getOwnedBy(), existACL.getiNodeId()));
        
        Date date = new Date();
        existACL.setModifiedAt(date);
        existACL.setResourceRole(iNodeACL.getResourceRole());
        existACL.setModifiedBy(user.getId());
        existACL = iNodeACLService.modifyINodeACLById(existACL);
        
        // 判断是否需要覆盖子目录的acl
        overwriteSubAcls(user, existACL);
        
        // 发送事件
        createEvent(user, EventType.ACL_UPDATE, new INode(existACL.getOwnedBy(), existACL.getiNodeId()));
        String keyword = null;
        if (INode.FILES_ROOT != existACL.getiNodeId())
        {
            INode node = fileBaseService.getINodeInfo(existACL.getOwnedBy(), existACL.getiNodeId());
            keyword = node == null ? "" : node.getName();
        }
        String loginName = existACL.getUser() != null ? existACL.getUser().getLoginName() : null;
        if (existACL.getUser() != null && existACL.getUser().getLoginName() != null
            && existACL.getUser().getLoginName().length() > Constants.MAX_NAME_LOG)
        {
            loginName = existACL.getUser().getLoginName().substring(0, Constants.MAX_NAME_LOG);
        }
        String[] logMsgs = new String[]{StringUtils.trimToEmpty(loginName),
            StringUtils.trimToEmpty(existACL.getUserType()), String.valueOf(existACL.getOwnedBy()),
            String.valueOf(existACL.getiNodeId())};
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.MODIFY_NODE_ACL,
            logMsgs,
            keyword);
        
        return existACL;
    }
    
    private void checkAclOperPermission(long ownerId, long userId, String enterpriseId,UserToken userToken) throws ForbiddenException
    {
        if (ownerId == userId)
        {
            return;
        }
        // 是否具有授权权限判断:只需要获取根节点是否具有author权限就可以
        List<INodeACL> aclList = iNodeACLService.getINodeACLSelfAndAnyACLs(ownerId, INode.FILES_ROOT, userId,enterpriseId,userToken);
        
        if (CollectionUtils.isEmpty(aclList))
        {
            String excepMessage = "Not allowed to operate acl , userId:" + userId;
            throw new ForbiddenException(excepMessage);
        }
        
        boolean isForbidden = true;
        for (INodeACL acl : aclList)
        {
            if (ResourceRole.AUTHER.equals(acl.getResourceRole()))
            {
                isForbidden = false;
                break;
            }
        }
        
        if (isForbidden)
        {
            String excepMessage = "Not allowed to operate acl , userId:" + userId;
            throw new ForbiddenException(excepMessage);
        }
    }
    
    private void checkRoleValid(String role) throws InvalidPermissionRoleException
    {
        ResourceRole roleInfo = resourceRoleService.getResourceRole(role);
        // 检查权限角色合法性
        if (roleInfo == null)
        {
            String errorMsg = "role is not valid, role:" + role;
            throw new InvalidPermissionRoleException(errorMsg);
        }
        // 数据库不区分大小写，需要判断
        if (!StringUtils.equals(role, roleInfo.getResourceRole()))
        {
            String errorMsg = "role is not valid, role:" + role;
            throw new InvalidPermissionRoleException(errorMsg);
        }
    }
    
    private boolean checkSubAclSmaller(ACL parentAcl, ACL acl)
    {
        if (parentAcl.getListValue() > acl.getListValue())
        {
            return true;
        }
        if (parentAcl.getDeleteValue() > acl.getDeleteValue())
        {
            return true;
        }
        if (parentAcl.getDownloadValue() > acl.getDownloadValue())
        {
            return true;
        }
        if (parentAcl.getEditValue() > acl.getEditValue())
        {
            return true;
        }
        if (parentAcl.getPublishLinkValue() > acl.getPublishLinkValue())
        {
            return true;
        }
        if (parentAcl.getPreviewValue() > acl.getPreviewValue())
        {
            return true;
        }
        if (parentAcl.getUploadValue() > acl.getUploadValue())
        {
            return true;
        }
        
        return false;
    }
    
    private void checkTopAcls(UserToken userToken, String role, String userId, String userType, INode node)
    {
    	String enterpriseId = "";
		if (userToken.getAccountVistor() != null) {
			enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
		}

        FileBasicConfig config = fileBaseService.getFileBaiscConfig(userToken);
        if (null != config && !config.getOverwriteAcl())
        {
            return;
        }
        
        ACL nodeACL = getAclByRole(role);
        // TODO 只考虑个人的检查 ，其他场景暂未想到解决方案
        if (INodeACL.TYPE_USER.equals(userType))
        {
            ACL parentAcl = iNodeACLService.getACLForAccessUser(Long.parseLong(userId), userType, node,enterpriseId,userToken);
            if (parentAcl == null || checkSubAclSmaller(parentAcl, nodeACL))
            {
                throw new ForbiddenException();
            }
        }
    }
    
    private void createEvent(UserToken userToken, EventType type, INode srcNode)
    {
        LogEvent.createEvent(userToken, type, srcNode, eventService, LOGGER);
    }
    
    private ACL getAclByRole(String roleStr)
    {
        ResourceRole role = systemRoleUtil.getRole(roleStr);
        ACL parentAcl = new ACL(role);
        parentAcl.getPriviledge(role);
        return parentAcl;
    }
    
    private void overwriteSubAcls(UserToken userToken, INodeACL iNodeACL)
    {
        FileBasicConfig config = fileBaseService.getFileBaiscConfig(userToken);
        if (null != config && !config.getOverwriteAcl())
        {
            return;
        }
        ACL parentAcl = getAclByRole(iNodeACL.getResourceRole());
        INode inode = new INode(iNodeACL.getOwnedBy(), iNodeACL.getiNodeId());
        // TODO 只考虑个人的检查
        overwriteSubAclsByRecursive(userToken, iNodeACL, inode, parentAcl);
    }
    
    private void overwriteSubAclsByRecursive(UserToken userToken, INodeACL iNodeACL, INode iNode,
        ACL parentAcl)
    {
        Limit limit = new Limit();
        long offset = 0;
        
        List<INode> subNodeList = null;
        INodeACL tempAcl = null;
        ACL acl = null;
        
        while (true)
        {
            limit.setOffset(offset);
            limit.setLength(100);
            subNodeList = iNodeDAO.getINodeByParent(iNode, null, limit);
            if (CollectionUtils.isEmpty(subNodeList))
            {
                break;
            }
            for (INode subNode : subNodeList)
            {
                tempAcl = iNodeACLService.getByResourceAndUser(subNode.getOwnedBy(),
                    subNode.getId(),
                    iNodeACL.getAccessUserId(),
                    iNodeACL.getUserType());
                
                if (tempAcl == null)
                {
                    continue;
                }
                
                acl = getAclByRole(tempAcl.getResourceRole());
                
                // 判断子目录是否设置小的acl,如果是就删除
                if (checkSubAclSmaller(parentAcl, acl))
                {
                    iNodeACLService.deleteINodeACLById(tempAcl.getOwnedBy(), tempAcl.getId());
                }
                
                // 递归覆盖子文件或文件版本的ACL
                if (FilesCommonUtils.isFolderType(subNode.getType()) || INode.TYPE_FILE == subNode.getType())
                {
                    overwriteSubAclsByRecursive(userToken, iNodeACL, subNode, parentAcl);
                }
                
            }
            offset += 100;
        }
    }
    
    private void overwriteParentAclsByRecursive(UserToken userToken, INodeACL iNodeACL, INode iNode,
            ACL parentAcl)
        {
            Limit limit = new Limit();
            long offset = 0;
            
            List<INode> subNodeList = null;
            INodeACL tempAcl = null;
            ACL acl = null;
            
            while (true)
            {
                limit.setOffset(offset);
                limit.setLength(100);
                subNodeList = iNodeDAO.getINodeByParent(iNode, null, limit);
                if (CollectionUtils.isEmpty(subNodeList))
                {
                    break;
                }
                for (INode subNode : subNodeList)
                {
                    tempAcl = iNodeACLService.getByResourceAndUser(subNode.getOwnedBy(),
                        subNode.getId(),
                        iNodeACL.getAccessUserId(),
                        iNodeACL.getUserType());
                    
                    if (tempAcl == null)
                    {
                        continue;
                    }
                    
                    acl = getAclByRole(tempAcl.getResourceRole());
                    iNodeACLService.deleteINodeACLById(tempAcl.getOwnedBy(), tempAcl.getId());
                   /* // 判断子目录是否设置小的acl,如果是就删除
                    if (checkSubAclSmaller(parentAcl, acl))
                    {
                        iNodeACLService.deleteINodeACLById(tempAcl.getOwnedBy(), tempAcl.getId());
                    }*/
                    
                    // 递归覆盖子文件或文件版本的ACL
                    if (FilesCommonUtils.isFolderType(subNode.getType()) || INode.TYPE_FILE == subNode.getType())
                    {
                        overwriteSubAclsByRecursive(userToken, iNodeACL, subNode, parentAcl);
                    }
                    
                }
                offset += 100;
            }
        }
    
    
    private void deleteSubAcls(UserToken userToken, INodeACL iNodeACL, INode iNode,
            ACL parentAcl)
        {
            Limit limit = new Limit();
            long offset = 0;
            
            List<INode> subNodeList = null;
            INodeACL tempAcl = null;
            ACL acl = null;
            
            while (true)
            {
                limit.setOffset(offset);
                limit.setLength(100);
                subNodeList = iNodeDAO.getINodeByParent(iNode, null, limit);
                if (CollectionUtils.isEmpty(subNodeList))
                {
                    break;
                }
                for (INode subNode : subNodeList)
                {
                    tempAcl = iNodeACLService.getByResourceAndUser(subNode.getOwnedBy(),
                        subNode.getId(),
                        iNodeACL.getAccessUserId(),
                        iNodeACL.getUserType());
                    
                    if (tempAcl == null)
                    {
                        continue;
                    }
                    
                    acl = getAclByRole(tempAcl.getResourceRole());
                    iNodeACLService.deleteINodeACLById(tempAcl.getOwnedBy(), tempAcl.getId());
                   /* // 判断子目录是否设置小的acl,如果是就删除
                    if (checkSubAclSmaller(parentAcl, acl))
                    {
                        iNodeACLService.deleteINodeACLById(tempAcl.getOwnedBy(), tempAcl.getId());
                    }*/
                    
                    // 递归覆盖子文件或文件版本的ACL
                    if (FilesCommonUtils.isFolderType(subNode.getType()) || INode.TYPE_FILE == subNode.getType())
                    {
                        overwriteSubAclsByRecursive(userToken, iNodeACL, subNode, parentAcl);
                    }
                    
                }
                offset += 100;
            }
        }

	@Override
	public INodeACL modifyNodeIsVisibleACL(UserToken userToken, INodeACL iNodeACL,String isavalible) {
		// TODO Auto-generated method stub
		String enterpriseId = "";
		if (userToken.getAccountVistor() != null) {
			enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
		}

		 // 是否具有授权权限判断
        checkAclOperPermission(iNodeACL.getOwnedBy(), userToken.getId(),enterpriseId,userToken);
        String keyword = null;
        // 检查资源是否存在
        
        String loginName = null;
        if (iNodeACL.getiNodeId() != INode.FILES_ROOT)
        {
            INode fNode = fileBaseService.getINodeInfo(iNodeACL.getOwnedBy(), iNodeACL.getiNodeId());
            if (null == fNode)
            {
                LOGGER.error("inode is null,ownerid:" + iNodeACL.getOwnedBy() + ",id:"
                    + iNodeACL.getiNodeId());
                throw new NoSuchItemsException("inode is null,ownerid:" + iNodeACL.getOwnedBy() + ",id:"
                    + iNodeACL.getiNodeId());
            }
            keyword = StringUtils.trimToEmpty(fNode.getName());
            iNodeACL.setiNodePid(fNode.getParentId());
        }
        else
        {
            // 判断用户是否存在
            User user = userService.get(iNodeACL.getOwnedBy());
            if (user == null)
            {
                String errorMsg = "ownedBy is not exist, or regionId = -1 ,userId:" + iNodeACL.getOwnedBy();
                throw new NoSuchItemsException(errorMsg);
            }
            keyword = StringUtils.trimToEmpty(user.getName());
            iNodeACL.setiNodePid(INode.FILES_ROOT);
        }
        
   
        
        Date date = new Date();
        iNodeACL.setId(nodeACLIdGenerateService.getNextNodeACLId(iNodeACL.getOwnedBy()));
        iNodeACL.setCreatedAt(date);
        iNodeACL.setiNodeId(iNodeACL.getiNodeId());
        iNodeACL.setCreatedBy(userToken.getId());
        iNodeACL.setOwnedBy(iNodeACL.getOwnedBy());
        iNodeACL.setModifiedAt(date);
        iNodeACL.setModifiedBy(userToken.getId());
        iNodeACL.setAccessUserId(INodeACL.ID_SECRET);
        INodeACL existACL =iNodeACLService.getByResourceAndUser(iNodeACL.getOwnedBy(),
                iNodeACL.getiNodeId(),
                INodeACL.ID_SECRET,
                iNodeACL.getUserType());
        if(isavalible.equals("1")){
        	if(existACL==null){
        		 iNodeACL = iNodeACLService.addINodeIsVisibleACL(iNodeACL);
        	}
        }else{
        	 iNodeACLService.deleteByResourceAndUser(iNodeACL.getOwnedBy(), iNodeACL.getiNodeId(), INodeACL.ID_SECRET, INodeACL.TYPE_SECRET);
        	  try {
                  iNodeACLService.deleteINodeAllACLs(iNodeACL.getOwnedBy(), iNodeACL.getiNodeId());
              }catch (Exception e){

              }

        }
       
        // 发送事件
        createEvent(userToken, EventType.ACL_CREATE, new INode(iNodeACL.getOwnedBy(), iNodeACL.getiNodeId()));
        if (loginName != null && loginName.length() > Constants.MAX_NAME_LOG)
        {
            loginName = loginName.substring(0, Constants.MAX_NAME_LOG);
        }
        String[] logMsgs = new String[]{StringUtils.trimToEmpty(loginName),
            StringUtils.trimToEmpty(iNodeACL.getUserType()), String.valueOf(iNodeACL.getOwnedBy()),
            String.valueOf(iNodeACL.getiNodeId())};
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.ADD_NODE_ACL,
            logMsgs,
            keyword);
        return iNodeACL;
	}

	@Override
	public INodeACL getNodeIsVisibleACL(UserToken userToken, INodeACL inodeRole) {
		// TODO Auto-generated method stub
		String enterpriseId = "";
		if (userToken.getAccountVistor() != null) {
			enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
		}
        // 是否具有授权权限判断
        checkAclOperPermission(inodeRole.getOwnedBy(), userToken.getId(),enterpriseId,userToken);
        
        INodeACL iNodeACL = iNodeACLService.getNodeIsVisibleACL(inodeRole);
        return iNodeACL;
	}
}
