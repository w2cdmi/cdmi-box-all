package com.huawei.sharedrive.app.group.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.manager.PersistentEventManager;
import com.huawei.sharedrive.app.exception.ExceedGroupMaxMemberNumException;
import com.huawei.sharedrive.app.exception.ExistMemberConflictException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.NoSuchGroupException;
import com.huawei.sharedrive.app.exception.NoSuchUserException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.group.manager.GroupMembershipsManager;
import com.huawei.sharedrive.app.group.service.GroupCachedService;
import com.huawei.sharedrive.app.group.service.GroupMembershipsIdGenerateService;
import com.huawei.sharedrive.app.group.service.GroupMembershipsService;
import com.huawei.sharedrive.app.group.service.GroupService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.group.GroupMembershipsInfo;
import com.huawei.sharedrive.app.openapi.domain.group.GroupMembershipsList;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrder;
import com.huawei.sharedrive.app.openapi.domain.group.RestAddGroupRequest;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroup;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroupMember;
import com.huawei.sharedrive.app.openapi.domain.message.MessageParamName;
import com.huawei.sharedrive.app.user.domain.GroupInfo;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.GroupMemberService;
import com.huawei.sharedrive.app.user.service.UserService;

import pw.cdmi.box.domain.Limit;

@Component
public class GroupMembershipsManagerImpl implements GroupMembershipsManager
{
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private GroupCachedService groupCachedService;
    
    @Autowired
    private GroupMemberService groupMemberService;
    
    @Autowired
    private GroupMembershipsIdGenerateService groupMembershipsIdGenerateService;
    
    @Autowired
    private GroupMembershipsService groupMembershipsService;
    
    @Autowired
    private GroupService groupService;
    
    @Autowired
    private PersistentEventManager persistentEventManager;
    
    @Autowired
    private UserService userService;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public GroupMembershipsInfo addMemberShips(UserToken userToken, RestAddGroupRequest groupMember,
        Long groupId)
    {
        Group group = checkAndGetExistGroup(userToken, groupId);
        GroupMemberships operMemberships = new GroupMemberships();
        operMemberships.setGroupId(groupId);
        if (userToken.getId() != User.APP_GROUP_ID)
        {
            operMemberships = isCreateMembershipsRight(userToken, groupId);
            if (StringUtils.equals(GroupConstants.ROLE_ADMIN, groupMember.getGroupRole())
                && operMemberships.getGroupRole() != GroupConstants.GROUP_ROLE_ADMIN)
            {
                throw new ForbiddenException("Not allowed to create groupMember, role: "
                    + groupMember.getGroupRole() + ",the operator role:"
                    + transRole(operMemberships.getGroupRole()));
            }
            if (StringUtils.equals(GroupConstants.ROLE_MANAGER, groupMember.getGroupRole())
                && operMemberships.getGroupRole() == GroupConstants.GROUP_ROLE_MANAGER)
            {
                throw new ForbiddenException("Not allowed to create groupMember, role: "
                    + groupMember.getGroupRole() + ",the operator role:"
                    + transRole(operMemberships.getGroupRole()));
            }
        }
        long totalCount = groupMembershipsService.getMemberListCount(userToken, operMemberships, null, null);
        if (group.getMaxMembers() != GroupConstants.MAXMEMBERS_DEFAULT && group.getMaxMembers() <= totalCount)
        {
            String errorMsg = "group total Member Num exceed maximum , maximum:" + group.getMaxMembers()
                + ", current:" + totalCount;
            throw new ExceedGroupMaxMemberNumException(errorMsg);
        }
        

        User user = userService.get(null, groupMember.getMember().getUserId());
        // TODO 群组作为子群的情况未考虑
        if (user == null || user.getType() == User.STATUS_TEAMSPACE_INTEGER)
        {
            throw new NoSuchUserException("user is not exist, userId:" + groupMember.getMember().getUserId());
        }
        GroupMemberships groupMemberships = new GroupMemberships();
        extractedMemberships(groupMember, groupId, group, user, groupMemberships);
        checkGroupMemberExist(groupMemberships.getUserId(), groupId, groupMemberships.getUserType());
        
        // grouRole 为admin 则原拥有者降级为管理员
        changeOwner(userToken, groupId, group, groupMemberships);
        long membershipsId = groupMembershipsIdGenerateService.getNextMembershipsId(groupId);
        groupMemberships.setId(membershipsId);
        groupMembershipsService.createMemberships(userToken, groupMemberships);
        GroupMembershipsInfo membershipsInfo = new GroupMembershipsInfo();
        
        if (userToken.getId() != User.APP_GROUP_ID)
        {
            membershipsInfo.setId(userToken.getCloudUserId());
            membershipsInfo.setGroupRole(transRole(operMemberships.getGroupRole()));
        }
        else
        {
            membershipsInfo.setId(userToken.getId());
            membershipsInfo.setGroupRole(GroupConstants.OPER_APP);
        }
        
        membershipsInfo.setGroup(new RestGroup(group));
        membershipsInfo.setMember(new RestGroupMember(groupMemberships));
        groupCachedService.deleteCached(groupMember.getMember().getUserId());
        String keyword = StringUtils.trimToEmpty(group.getName());
        String[] logParams = new String[]{StringUtils.trimToEmpty(user.getName()),
            GroupConstants.getRoleStr(groupMemberships.getGroupRole()), String.valueOf(group.getId())};
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.ADD_GROUP_MEMBER,
            logParams,
            keyword);
        
        // 发送消息
        if (userToken.getId() != User.APP_GROUP_ID)
        {
            PersistentEvent event = generalGroupEvent(EventType.GROUP_MEMBER_CREATE,
                userToken.getCloudUserId(),
                groupMember.getMember().getUserId(),
                group.getId());
            persistentEventManager.fireEvent(event);
        }
        return membershipsInfo;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteOne(UserToken userToken, Long userId, Long groupId)
    {
        Group group = checkAndGetExistGroup(userToken, groupId);
        GroupMemberships groupMemberships = groupMembershipsService.getMemberships(userId,
            groupId,
            GroupConstants.GROUP_USERTYPE_USER);
        
        if (groupMemberships == null)
        {
            throw new ForbiddenException("No such groupMember, groupId :" + groupId);
        }
        if (userToken.getId() != User.APP_GROUP_ID)
        {
            isDeleteRight(userToken, groupId, groupMemberships);
        }
        else
        {
            if (GroupConstants.GROUP_ROLE_ADMIN == groupMemberships.getGroupRole())
            {
                String errorMsg = "Not allowed to delete admin user!";
                throw new ForbiddenException(errorMsg);
            }
        }
        
        groupMembershipsService.deleteOne(groupId, userId, GroupConstants.GROUP_USERTYPE_USER);
        groupCachedService.deleteCached(userId);
        String keyword = StringUtils.trimToEmpty(group.getName());
        String[] logParams = new String[]{String.valueOf(groupMemberships.getLoginName()),
            GroupConstants.getRoleStr(groupMemberships.getGroupRole()), String.valueOf(groupId)};
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.DELETE_GROUP_MEMBER,
            logParams,
            keyword);
        
        if (userToken.getId() != User.APP_GROUP_ID)
        {
            // 发送消息
            PersistentEvent event = generalGroupEvent(EventType.GROUP_MEMBER_DELETE,
                userToken.getCloudUserId(),
                userId,
                group.getId());
            persistentEventManager.fireEvent(event);
        }
    }
    
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public GroupMembershipsList listMembers(UserToken userToken, List<GroupOrder> order, Integer length,
        Long offset, String groupRole, String keyword, Long groupId)
    {
        checkAndGetExistGroup(userToken, groupId);

        Limit limit = new Limit(offset, length);
        GroupMemberships groupMemberships = new GroupMemberships();
        
        groupMemberships.setGroupId(groupId);
        Long role = null;
        if (!StringUtils.equals(groupRole, "all"))
        {
            role = Long.parseLong(transRole(groupRole) + "");
        }
        
        long totalCount = groupMembershipsService.getMemberListCount(userToken,
            groupMemberships,
            role,
            keyword);
        
        List<GroupMemberships> groupMembershipses = groupMembershipsService.getMemberList(order,
            limit,
            groupMemberships,
            role,
            keyword);
        List<GroupMembershipsInfo> memberItems = new ArrayList<GroupMembershipsInfo>(
            groupMembershipses.size());
        GroupMembershipsInfo groupAndMember = null;
        Group group = null;
        GroupMembershipsList groupUserList = new GroupMembershipsList();
        RestGroup restGroup = null;
        RestGroupMember restGroupMember = null;
        for (GroupMemberships gm : groupMembershipses)
        {
            groupAndMember = new GroupMembershipsInfo();
            group = groupService.get(gm.getGroupId());
            if (group == null)
            {
                throw new NoSuchGroupException("no such group, id:" + gm.getGroupId());
            }
            restGroup = new RestGroup(group);
            groupAndMember.setGroup(restGroup);
            restGroupMember = new RestGroupMember(gm);
            groupAndMember.setMember(restGroupMember);
            groupAndMember.setId(gm.getUserId());
            groupAndMember.setGroupRole(transRole(gm.getGroupRole()));
            memberItems.add(groupAndMember);
        }
        groupUserList.setLimit(length);
        groupUserList.setOffset(offset);
        groupUserList.setTotalCount(totalCount);
        groupUserList.setMemberships(memberItems);
        return groupUserList;
    }
    
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public GroupMembershipsInfo modifyMemberships(UserToken userToken, String role, Long userId, Long groupId)
    {
        Group group = checkAndGetExistGroup(userToken, groupId);
        
        GroupMemberships groupMemberships = groupMembershipsService.getMemberships(userId,
            groupId,
            GroupConstants.GROUP_USERTYPE_USER);
        if (groupMemberships == null)
        {
            throw new ForbiddenException("Group member not exist.");
        }
        GroupMembershipsInfo groupMembershipsInfo = new GroupMembershipsInfo();
        if (userToken.getId() != User.APP_GROUP_ID)
        {
            modifyByUser(userToken, role, groupId, group, groupMemberships, groupMembershipsInfo);
        }
        else
        {
            modifyByApp(userToken, role, group, groupMemberships, groupMembershipsInfo);
        }
        groupService.modifyGroup(userToken, group);
        groupMembershipsService.update(groupMemberships);
        groupMembershipsInfo.setGroup(new RestGroup(group));
        groupMembershipsInfo.setMember(new RestGroupMember(groupMemberships));
        groupCachedService.deleteCached(userId);
        
        // 非应用账号修改时发送消息
        if (userToken.getId() != User.APP_GROUP_ID)
        {
            PersistentEvent event = generalRoleUpdateEvent(userToken.getCloudUserId(), userId, groupId, role);
            persistentEventManager.fireEvent(event);
        }
        
        String keyword = StringUtils.trimToEmpty(group.getName());
        String[] logParams = new String[]{StringUtils.trimToEmpty(groupMemberships.getLoginName()),
            GroupConstants.getRoleStr(groupMemberships.getGroupRole()), String.valueOf(groupId)};
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.MODIFY_GROUP_MEMBER,
            logParams,
            keyword);
        return groupMembershipsInfo;
    }
    
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public void sendEvent(UserToken userToken, EventType type, INode srcNode, INode destNode,
        UserLogType userLogType, String[] logParams, String keyword, Long groupId, Long userId,
        String userType)
    {
        if (groupId != null)
        {
            Group group = groupService.get(groupId);
            keyword = StringUtils.trimToEmpty(group != null ? group.getName() : null);
        }
        if (userId != null && logParams != null && logParams.length > 0)
        {
            User user = userService.get(null, userId);
            if (user != null && user.getType() == User.STATUS_TEAMSPACE_INTEGER)
            {
                logParams[0] = user.getName();
            }
        }
        if (UserLogType.MODIFY_GROUP_MEMBER_ERR.equals(userLogType)
            || UserLogType.DELETE_GROUP_MEMBER_ERR.equals(userLogType))
        {
            GroupMemberships groupMemberships = groupMembershipsService.getMemberships(userId,
                groupId,
                transUserType(userType));
            if (groupMemberships != null && logParams != null && logParams.length > 1)
            {
                logParams[1] = GroupConstants.getRoleStr(groupMemberships.getGroupRole());
            }
        }
        
        fileBaseService.sendINodeEvent(userToken, type, srcNode, destNode, userLogType, logParams, keyword);
    }
    
    /**
     * 更改拥有者
     * 
     * @param userToken
     * @param groupId
     * @param group
     * @param groupMemberships
     */
    private void changeOwner(UserToken userToken, Long groupId, Group group, GroupMemberships groupMemberships)
    {
        if (groupMemberships.getGroupRole() == GroupConstants.GROUP_ROLE_ADMIN)
        {
            GroupMemberships oldMemberships = groupMembershipsService.getMemberships(group.getOwnedBy(),
                groupId,
                GroupConstants.GROUP_USERTYPE_USER);
            oldMemberships.setGroupRole(GroupConstants.GROUP_ROLE_MANAGER);
            groupMembershipsService.update(oldMemberships);
            group.setModifiedAt(new Date());
            group.setOwnedBy(groupMemberships.getUserId());
            if (userToken.getId() != User.APP_GROUP_ID)
            {
                group.setModifiedBy(userToken.getCloudUserId());
            }
            else
            {
                group.setModifiedBy(userToken.getId());
            }
            groupService.modifyGroup(null, group);
        }
    }
    
    private Group checkAndGetExistGroup(UserToken userToken, Long groupId)
    {
        Group group = groupService.get(groupId);
        if (group == null)
        {
            throw new NoSuchGroupException("Group not exist, id is " + groupId);
        }
        if (userToken.getId() != User.APP_GROUP_ID)
        {
            checkPrivateGroupMember(userToken, group);
        }
        // 群组处于禁用状态
        if (group.getStatus() != GroupConstants.GROUP_STATUS_DEFAULT)
        {
            throw new ForbiddenException();
        }
        return group;
    }
    
    private void checkGroupMemberExist(long userId, Long groupId, byte userType)
    {
        GroupMemberships groupMemberships = groupMembershipsService.getMemberships(userId, groupId, userType);
        if (groupMemberships != null)
        {
            String msg = "GroupMember already exixt";
            throw new ExistMemberConflictException(msg);
        }
    }
    
    /**
     * @param userToken
     * @param id
     * @param group
     */
    private void checkPrivateGroupMember(UserToken userToken, Group group)
    {
        if (group.getType() == GroupConstants.GROUP_TYPE_DEFAULT)
        {
            List<GroupInfo> groupInfoes = this.groupMemberService.getUserGroupList(userToken.getCloudUserId());
            boolean isMember = false;
            for (GroupInfo tempGroupInfo : groupInfoes)
            {
                if (tempGroupInfo.getId() == group.getId())
                {
                    isMember = true;
                    break;
                }
            }
            if (!isMember)
            {
                throw new ForbiddenException("the user is not the member of the group: " + group.getId());
            }
        }
    }
    
    private void extractedMemberships(RestAddGroupRequest groupMember, Long groupId, Group group, User user,
        GroupMemberships groupMemberships)
    {
        groupMemberships.setGroupId(groupId);
        
        groupMemberships.setUserType(transUserType(groupMember.getMember().getUserType()));
        groupMemberships.setUserId(user.getId());
        groupMemberships.setUsername(user.getName());
        groupMemberships.setName(group.getName());
        groupMemberships.setGroupRole(transRole(groupMember.getGroupRole()));
        groupMemberships.setLoginName(user.getLoginName());
    }
    
    private PersistentEvent generalGroupEvent(EventType eventType, long providerId, long receiverId,
        long groupId)
    {
        PersistentEvent event = new PersistentEvent();
        event.setEventType(eventType);
        event.addParameter(MessageParamName.PROVIDER_ID, providerId);
        event.addParameter(MessageParamName.RECEIVER_ID, receiverId);
        event.addParameter(MessageParamName.GROUP_ID, groupId);
        return event;
    }
    
    private PersistentEvent generalRoleUpdateEvent(long providerId, long receiverId, long groupId,
        String currentRole)
    {
        PersistentEvent event = new PersistentEvent();
        event.setEventType(EventType.GROUP_MEMBER_UPDATE);
        event.addParameter(MessageParamName.PROVIDER_ID, providerId);
        event.addParameter(MessageParamName.RECEIVER_ID, receiverId);
        event.addParameter(MessageParamName.GROUP_ID, groupId);
        event.addParameter(MessageParamName.CURRENT_ROLE, currentRole);
        return event;
    }
    
    private GroupMemberships isCreateMembershipsRight(UserToken userToken, Long groupId)
    {
        GroupMemberships groupMemberships = new GroupMemberships();
        groupMemberships.setGroupId(groupId);
        groupMemberships.setUserId(userToken.getCloudUserId());
        groupMemberships.setUserType(GroupConstants.GROUP_USERTYPE_USER);
        groupMemberships = groupMembershipsService.getUser(groupMemberships);
        if (groupMemberships == null)
        {
            throw new ForbiddenException("no such groupMember");
        }
        
        // 操作者不是团队空间的拥有者或管理员
        if (groupMemberships.getGroupRole() != GroupConstants.GROUP_ROLE_ADMIN
            && groupMemberships.getGroupRole() != GroupConstants.GROUP_ROLE_MANAGER)
        {
            throw new ForbiddenException("Not allowed to create groupMember, role: "
                + groupMemberships.getGroupRole());
        }
        return groupMemberships;
    }
    
    private void isDeleteRight(UserToken userToken, Long groupId, GroupMemberships groupMemberships)
    {
        GroupMemberships operGroupMember = new GroupMemberships();
        operGroupMember.setGroupId(groupId);
        operGroupMember.setUserType(GroupConstants.GROUP_USERTYPE_USER);
        operGroupMember.setUserId(userToken.getCloudUserId());
        operGroupMember = groupMembershipsService.getUser(operGroupMember);
        
        if (operGroupMember == null)
        {
            throw new ForbiddenException("No such groupMember, groupId :" + groupId);
        }
        
        if (operGroupMember.getGroupRole() == GroupConstants.GROUP_ROLE_ADMIN)
        {
            if (GroupConstants.GROUP_ROLE_MANAGER != groupMemberships.getGroupRole()
                && GroupConstants.GROUP_ROLE_MEMBER != groupMemberships.getGroupRole())
            {
                throw new ForbiddenException("owner not allowed to delete admin user!");
            }
        }
        else if (operGroupMember.getGroupRole() == GroupConstants.GROUP_ROLE_MANAGER)
        {
            if (GroupConstants.GROUP_ROLE_MEMBER != groupMemberships.getGroupRole())
            {
                if ((userToken.getCloudUserId() != groupMemberships.getUserId())
                    || GroupConstants.GROUP_USERTYPE_USER != groupMemberships.getUserType())
                {
                    throw new ForbiddenException("member only allowed to delete self!");
                }
            }
        }
        else
        {
            if ((userToken.getCloudUserId() != groupMemberships.getUserId())
                || GroupConstants.GROUP_USERTYPE_USER != groupMemberships.getUserType())
            {
                throw new ForbiddenException("member only allowed to delete self!");
            }
        }
        

    }
    
    private void modifyByApp(UserToken userToken, String role, Group group,
        GroupMemberships groupMemberships, GroupMembershipsInfo groupMembershipsInfo)
    {
        groupMemberships.setGroupRole(transRole(role));
        if (StringUtils.equals(role, GroupConstants.ROLE_ADMIN))
        {
            groupMembershipsService.update(groupMemberships);
            GroupMemberships oldOwnerInfo = groupMembershipsService.getMemberships(group.getOwnedBy(),
                group.getId(),
                GroupConstants.GROUP_USERTYPE_USER);
            oldOwnerInfo.setGroupRole(GroupConstants.GROUP_ROLE_MANAGER);
            groupMembershipsService.update(oldOwnerInfo);
            group.setOwnedBy(groupMemberships.getUserId());
        }
        group.setModifiedAt(new Date());
        group.setModifiedBy(userToken.getId());
        groupMembershipsInfo.setId(userToken.getId());
        groupMembershipsInfo.setGroupRole(GroupConstants.OPER_APP);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void modifyByUser(UserToken userToken, String role, Long groupId, Group group,
        GroupMemberships groupMemberships, GroupMembershipsInfo groupMembershipsInfo)
    {
        GroupMemberships operMemberships = new GroupMemberships();
        operMemberships.setGroupId(groupId);
        operMemberships.setUserId(userToken.getCloudUserId());
        operMemberships.setUserType(GroupConstants.GROUP_USERTYPE_USER);
        operMemberships = groupMembershipsService.getUser(operMemberships);
        if (operMemberships == null)
        {
            throw new ForbiddenException("Group member not exist.");
        }
        groupMembershipsInfo.setGroupRole(transRole(operMemberships.getGroupRole()));
        if (groupMemberships.getUserId() == userToken.getCloudUserId())
        {
            throw new ForbiddenException("Not allowed to modify groupMember role self, id"
                + userToken.getCloudUserId());
        }
        if (operMemberships.getGroupRole() != GroupConstants.GROUP_ROLE_ADMIN
            && operMemberships.getGroupRole() != GroupConstants.GROUP_ROLE_MANAGER)
        {
            throw new ForbiddenException("Not allowed to modify groupMember, role: "
                + groupMemberships.getGroupRole());
        }
        
        if (operMemberships.getGroupRole() == GroupConstants.GROUP_ROLE_MANAGER)
        {
            if (groupMemberships.getGroupRole() != GroupConstants.GROUP_ROLE_MEMBER)
            {
                throw new ForbiddenException("Not allowed to modify groupMember, role:"
                    + groupMemberships.getGroupRole());
            }
            
            // 操作者是管理员不能修改拥有者
            if (StringUtils.equals(role, GroupConstants.ROLE_ADMIN))
            {
                throw new ForbiddenException("Not allowed to modify groupMember, role:" + role);
            }
            groupMemberships.setGroupRole(transRole(role));
        }
        else if (operMemberships.getGroupRole() == GroupConstants.GROUP_ROLE_ADMIN)
        {
            
            // 变更拥有者
            if (StringUtils.equals(role, GroupConstants.ROLE_ADMIN))
            {
                group.setOwnedBy(groupMemberships.getUserId());
                groupMemberships.setGroupRole(GroupConstants.GROUP_ROLE_ADMIN);
                
                // 拥有者降级为管理员
                operMemberships.setGroupRole(GroupConstants.GROUP_ROLE_MANAGER);
                groupMembershipsService.update(operMemberships);
            }
            else
            {
                groupMemberships.setGroupRole(transRole(role));
            }
        }
        group.setModifiedAt(new Date());
        group.setModifiedBy(userToken.getCloudUserId());
        groupMembershipsInfo.setId(operMemberships.getUserId());
    }
    
    private String transRole(byte groupRole)
    {
        if (groupRole == GroupConstants.GROUP_ROLE_ADMIN)
        {
            return GroupConstants.ROLE_ADMIN;
        }
        else if (groupRole == GroupConstants.GROUP_ROLE_MANAGER)
        {
            return GroupConstants.ROLE_MANAGER;
        }
        return GroupConstants.ROLE_MEMBER;
    }
    
    private byte transRole(String groupRole)
    {
        if (StringUtils.equals(groupRole, GroupConstants.ROLE_MANAGER))
        {
            return GroupConstants.GROUP_ROLE_MANAGER;
        }
        else if (StringUtils.equals(groupRole, GroupConstants.ROLE_ADMIN))
        {
            return GroupConstants.GROUP_ROLE_ADMIN;
        }
        return GroupConstants.GROUP_ROLE_MEMBER;
    }
    
    private byte transUserType(String userType)
    {
        if (StringUtils.equals(userType, GroupConstants.USERTYPE_GROUP))
        {
            return GroupConstants.GROUP_USERTYPE_GROUP;
        }
        return GroupConstants.GROUP_USERTYPE_USER;
    }
    
}
