package com.huawei.sharedrive.app.group.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.exception.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BadRequestException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.group.manager.GroupManager;
import com.huawei.sharedrive.app.group.service.GroupCachedService;
import com.huawei.sharedrive.app.group.service.GroupIdGenerateService;
import com.huawei.sharedrive.app.group.service.GroupMembershipsIdGenerateService;
import com.huawei.sharedrive.app.group.service.GroupMembershipsService;
import com.huawei.sharedrive.app.group.service.GroupService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.group.GroupList;
import com.huawei.sharedrive.app.openapi.domain.group.GroupMembershipsInfo;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrder;
import com.huawei.sharedrive.app.openapi.domain.group.GroupUserList;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroup;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroupMember;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroupModifyRequest;
import com.huawei.sharedrive.app.share.service.ShareService;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.BusinessConstants;

import pw.cdmi.box.domain.Limit;

@Component
public class GroupManagerImpl implements GroupManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(GroupManagerImpl.class);

	@Autowired
	private FileBaseService fileBaseService;

	@Autowired
	private GroupCachedService groupCachedService;

	@Autowired
	private GroupIdGenerateService groupIdGenerateService;

	@Autowired
	private GroupMembershipsIdGenerateService groupMembershipsIdGenerateService;

	@Autowired
	private GroupMembershipsService groupMembershipsService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private INodeACLService iNodeACLService;

	@Autowired
	private ShareService shareService;

	@Autowired
	private TeamSpaceMembershipService teamSpaceMembershipService;

	@Autowired
	private TeamSpaceService teamSpaceService;

	@Autowired
	private UserService userService;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public Group createGroup(UserToken userToken, Group group, Long ownedBy) {
		User user = null;
		if (ownedBy != null) {
			user = userService.get(null, ownedBy);
			if (user == null) {
				throw new NoSuchUserException("user not exist:" + ownedBy);
			}
			if (StringUtils.equals(User.STATUS_DISABLE_INTEGER, user.getStatus())) {
				throw new InvalidSpaceStatusException("User space status is abnormal:" + user.getStatus());
			}
		}
		long groupId = groupIdGenerateService.getNextGroupId();
		assemGroup(userToken, group, groupId, user);
		groupService.createGroup(userToken, group);
		groupCachedService.deleteCached(group.getOwnedBy());
		long membershipsId = groupMembershipsIdGenerateService.getNextMembershipsId(userToken.getCloudUserId());
		GroupMemberships groupMemberships = assemMemberships(userToken, group, groupId, membershipsId, user);
		groupMembershipsService.createMemberships(userToken, groupMemberships);
		String keyword = StringUtils.trim(group.getName());
		String[] logParams = new String[] { String.valueOf(groupId) };
		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.CREATE_GROUP, logParams,
				keyword);
		return group;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteGroup(UserToken userToken, Long groupId) {
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new NoSuchGroupException("group not exist, groupId is " + groupId);
		}
		// 非应用账户鉴权 和 应用鉴权
		if (userToken.getAccountVistor() == null) {
			checkShipsAndRoleRight(userToken, groupId);
		} else if (userToken.getAccountVistor().getId() != group.getAccountId().longValue()) {
			throw new ForbiddenException("The vistor accountId is :" + userToken.getAccountVistor().getId());
		}
		deleteTeamSpaceShips(groupId);
		shareService.deleteShareGroup(userToken, groupId, GroupConstants.GROUP_USERTYPE_GROUP);
		deleteMemberships(groupId);
		groupService.delete(groupId);
		groupMembershipsIdGenerateService.delete(group.getOwnedBy());
		groupCachedService.deleteCached(group.getOwnedBy());
		String keyword = StringUtils.trimToEmpty(group.getName());
		String[] logParams = new String[] { String.valueOf(group.getId()) };
		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.DELETE_GROUP, logParams,
				keyword);
	}

	private void deleteMemberships(Long groupId) {
		GroupMemberships tempShips = new GroupMemberships();
		tempShips.setGroupId(groupId);
		List<GroupMemberships> membershipsList = null;
		membershipsList = groupMembershipsService.getMemberList(null, null, tempShips, null, null);
		for (GroupMemberships gm : membershipsList) {
			groupMembershipsService.deleteUser(groupId, gm.getUserId());
		}
		groupMembershipsService.delete(groupId);
	}

	@Override
	public Group getGroupInfo(UserToken userToken, Long id) {
		Group group = groupService.get(id);
		if (group == null) {
			throw new NoSuchGroupException("Group isn't exist, id is " + id);
		}
		checkPrivateGroupMember(userToken, group);
		String keyword = StringUtils.trimToEmpty(group.getName());
		String[] logParams = new String[] { String.valueOf(id) };
		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_GROUP_INFO, logParams,
				keyword);
		return group;
	}

	@Override
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public GroupList getGroupList(List<GroupOrder> orders, Integer length, Long offset, String keyword, String type,
			UserToken userToken) {
		Group group = new Group();
		group.setName(keyword);
		group.setAppId(userToken.getAppId());
		if (userToken.getAccountVistor() == null && StringUtils.equals(type, GroupConstants.TYPE_ALL)) {
			String message = "Can't list all group by userToken";
			throw new ForbiddenException(message);
		}
		if (!StringUtils.equals(type, GroupConstants.TYPE_ALL)) {
			group.setType(transType(type));
		}
		Integer totalCount = 0;
		Limit limit = new Limit(offset, length);
		List<RestGroup> restGroups = new ArrayList<RestGroup>(BusinessConstants.INITIAL_CAPACITIES);
		// 应用鉴权
		GroupList groupList = new GroupList();
		if (userToken.getAccountVistor() != null) {
			group.setAccountId(userToken.getAccountId());
			totalCount = groupService.getCount(group);
			List<Group> groups = groupService.getGroupsList(orders, limit, group);
			RestGroup restGroup = null;
			for (Group g : groups) {
				restGroup = new RestGroup(g);
				restGroups.add(restGroup);
			}
		} else {
			totalCount = getGroupList(orders, keyword, userToken, group, limit, restGroups);
		}
		groupList.setTotalCount(totalCount);
		groupList.setLimit(length);
		groupList.setOffset(offset);
		groupList.setGroups(restGroups);
		return groupList;
	}

	@Override
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public GroupUserList getUserGroupList(List<GroupOrder> orders, Integer length, Long offset, UserToken userToken,
			String keyword, String type, String isListRole) {
		GroupUserList groupUserList = new GroupUserList();
		int totalCount = 0;
		groupUserList.setLimit(length);
		groupUserList.setOffset(offset);
		List<GroupMemberships> totalMembershipses = groupMembershipsService.getUserList(orders,
				new Limit(GroupConstants.DEFAULT_OFFSET, GroupConstants.DEFAULT_LENGTH), userToken.getCloudUserId(),
				GroupConstants.GROUP_USERTYPE_USER, keyword);
		Group group = null;
		List<GroupMembershipsInfo> currrentMembershipses = new ArrayList<GroupMembershipsInfo>(
				BusinessConstants.INITIAL_CAPACITIES);
		GroupMembershipsInfo groupAndMember = null;
		RestGroup restGroup = null;
		RestGroupMember restGroupMember = null;
		for (GroupMemberships gm : totalMembershipses) {
			group = groupService.get(gm.getGroupId(), userToken.getAccountId());
			if (group == null) {
				// throw new NoSuchGroupException("no such group, id:" +
				// gm.getGroupId());
				LOGGER.error("no such group, id:" + gm.getGroupId());
				continue;
			}
			if (StringUtils.isNotBlank(type) && !StringUtils.equals(type, GroupConstants.TYPE_ALL)
					&& !StringUtils.equals(type, transType(group.getType()))) {
				continue;
			}
			groupAndMember = new GroupMembershipsInfo();
			restGroup = new RestGroup(group);
			restGroupMember = new RestGroupMember(gm);
			groupAndMember.setGroup(restGroup);
			groupAndMember.setMember(restGroupMember);
			groupAndMember.setId(gm.getUserId());
			if (StringUtils.equals(isListRole, GroupConstants.LIST_ROLE)) {
				groupAndMember.setGroupRole(transRole(gm.getGroupRole()));
			}
			assmebleShipses(currrentMembershipses, groupAndMember);
			totalCount++;
		}
		List<GroupMembershipsInfo> memberItems = new ArrayList<GroupMembershipsInfo>(totalCount);
		long end = (length + offset) <= totalCount ? (length + offset) : totalCount;
		for (int i = offset.intValue(); i < end; i++) {
			memberItems.add(currrentMembershipses.get(i));
		}
		groupUserList.setTotalCount(totalCount);
		groupUserList.setMemberships(memberItems);
		return groupUserList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Group modifyGroup(UserToken userToken, Group group, RestGroupModifyRequest restGroupRequest) {
		Group groupInfo = groupService.get(group.getId());
		if (groupInfo == null) {
			throw new NoSuchGroupException("Group isn't exist");
		}
		if (groupInfo.getType() == GroupConstants.GROUP_TYPE_PUBLIC) {
			if (restGroupRequest.getType() != null
					&& StringUtils.equals(restGroupRequest.getType(), GroupConstants.TYPE_PRIVATE)) {
				throw new BadRequestException(
						"Public group can't modify the private group, type error:" + restGroupRequest.getType());
			}
		}
		restGroupAttribute(group, restGroupRequest, groupInfo);
		GroupMemberships groupMemberships = new GroupMemberships();
		groupMemberships = checkRight(userToken, groupInfo, groupMemberships);
		transModifiedDate(userToken, groupInfo);
		groupService.modifyGroup(userToken, groupInfo);

		modifyTeamMenberShip(groupInfo);

		if (restGroupRequest.getName() != null) {
			List<GroupMemberships> groupMembershipsList = groupMembershipsService.getMemberList(null, null,
					groupMemberships, null, null);
			for (GroupMemberships gm : groupMembershipsList) {
				groupMembershipsService.updateNameForGroup(gm.getGroupId(), gm.getUserId(), groupInfo.getName());
			}
		}

		String keyword = StringUtils.trimToEmpty(groupInfo.getName());
		String[] logParams = new String[] { String.valueOf(groupInfo.getId()) };
		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.MODIFY_GROUP, logParams,
				keyword);
		return groupInfo;
	}

	private void modifyTeamMenberShip(Group groupInfo) {
		User user = new User();
		user.setId(groupInfo.getId());
		user.setLoginName(groupInfo.getName());
		user.setName(groupInfo.getName());
		teamSpaceMembershipService.updateUsername(user, TeamSpaceMemberships.TYPE_GROUP);
	}

	@Override
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public void sendEvent(UserToken user, EventType type, INode srcNode, INode destNode, UserLogType userLogType,
			String[] logParams, String keyword, Long groupId) {
		if (groupId != null) {
			Group group = groupService.get(groupId);
			keyword = StringUtils.trimToEmpty(group != null ? group.getName() : null);
		}
		fileBaseService.sendINodeEvent(user, type, srcNode, destNode, userLogType, logParams, keyword);

	}

	private void assemGroup(UserToken userToken, Group group, long groupId, User user) {
		group.setId(groupId);
		Date date = new Date();
		group.setCreatedAt(date);
		group.setCreatedBy(userToken.getCloudUserId());
		group.setModifiedAt(date);
		group.setAccountId(userToken.getAccountId());
		if (user == null) {
			group.setOwnedBy(userToken.getCloudUserId());
		} else {
			group.setOwnedBy(user.getId());
		}
	}

	private GroupMemberships assemMemberships(UserToken userToken, Group group, long groupId, long membershipsId,
			User user) {
		GroupMemberships groupMemberships = new GroupMemberships();
		groupMemberships.setId(membershipsId);
		groupMemberships.setGroupId(groupId);
		groupMemberships.setName(group.getName());
		groupMemberships.setGroupRole(GroupConstants.GROUP_ROLE_ADMIN);
		groupMemberships.setUserType(GroupConstants.GROUP_USERTYPE_USER);
		if (user == null) {
			groupMemberships.setUserId(userToken.getCloudUserId());
			if (userToken.getLoginName() != null) {
				groupMemberships.setLoginName(userToken.getLoginName());
			} else {
				groupMemberships.setLoginName(group.getName());
			}

			if (userToken.getName() != null) {
				groupMemberships.setUsername(userToken.getName());
			} else {
				groupMemberships.setUsername(group.getName());
			}
		} else {
			groupMemberships.setUserId(user.getId());
			if (user.getLoginName() != null) {
				groupMemberships.setLoginName(user.getLoginName());
			} else {
				groupMemberships.setLoginName(group.getName());
			}

			if (user.getName() != null) {
				groupMemberships.setUsername(user.getName());
			} else {
				groupMemberships.setUsername(group.getName());
			}
		}
		return groupMemberships;
	}

	private void assmebleShipses(List<GroupMembershipsInfo> currrentMembershipses,
			GroupMembershipsInfo groupAndMember) {
		currrentMembershipses.add(groupAndMember);
	}

	/**
	 * @param userToken
	 * @param id
	 * @param group
	 */
	private void checkPrivateGroupMember(UserToken userToken, Group group) {
		if (userToken.getAccountVistor() != null) {
			if (group.getAccountId().longValue() != userToken.getAccountVistor().getId()) {
				throw new ForbiddenException("the accountId is : " + userToken.getAccountVistor().getId());
			}
		} else {
			if (group.getType() == GroupConstants.GROUP_TYPE_DEFAULT) {
				GroupMemberships info = groupMembershipsService.getMemberships(userToken.getCloudUserId(),
						group.getId(), GroupConstants.GROUP_USERTYPE_USER);
				if (info == null) {
					throw new ForbiddenException("the user is not the member of the group: " + group.getId());
				}
			}
		}

	}

	private GroupMemberships checkRight(UserToken userToken, Group groupInfo, GroupMemberships groupMemberships) {
		if (userToken.getAccountVistor() == null) {
			groupMemberships.setGroupId(groupInfo.getId());
			groupMemberships.setUserType(GroupConstants.GROUP_USERTYPE_USER);
			groupMemberships.setUserId(userToken.getCloudUserId());
			groupMemberships = groupMembershipsService.getUser(groupMemberships);
			if (groupMemberships == null) {
				throw new ForbiddenException();
			}
			if (groupMemberships.getGroupRole() != GroupConstants.GROUP_ROLE_ADMIN) {
				String excepMessage = "Not allowed to group , team Role:" + groupMemberships.getGroupRole();
				throw new ForbiddenException(excepMessage);
			}
			groupInfo.setModifiedBy(userToken.getCloudUserId());
		} else if (userToken.getAccountVistor().getId() == groupInfo.getAccountId().longValue()) {
			groupMemberships.setGroupId(groupInfo.getId());
			groupMemberships.setUserType(GroupConstants.GROUP_USERTYPE_USER);
			groupMemberships.setUserId(groupInfo.getOwnedBy());
			groupMemberships = groupMembershipsService.getUser(groupMemberships);
			if (groupMemberships == null) {
				throw new ForbiddenException();
			}
			groupInfo.setModifiedBy(User.APP_GROUP_ID);
		} else {
			throw new ForbiddenException("The accountId is " + userToken.getAccountVistor().getId());
		}
		return groupMemberships;
	}

	private void checkShipsAndRoleRight(UserToken userToken, Long groupId) {
		GroupMemberships groupMemberships = new GroupMemberships();
		groupMemberships.setGroupId(groupId);
		groupMemberships.setUserId(userToken.getCloudUserId());
		groupMemberships.setUserType(GroupConstants.GROUP_USERTYPE_USER);
		groupMemberships = groupMembershipsService.getUser(groupMemberships);

		if (groupMemberships == null) {
			throw new ForbiddenException("no such groupMember, groupId:" + groupId + ", userId:"
					+ userToken.getCloudUserId() + ", type:" + GroupConstants.GROUP_USERTYPE_USER);
		}

		if (groupMemberships.getGroupRole() != GroupConstants.GROUP_ROLE_ADMIN) {
			throw new ForbiddenException(
					"Not allowed to modifyTeamSpace , team Role:" + groupMemberships.getGroupRole());
		}
	}

	private void deleteTeamSpaceShips(Long groupId) {
		List<TeamSpaceMemberships> memberships = teamSpaceMembershipService.getByUserId(groupId.toString(),
				GroupConstants.USERTYPE_GROUP);
		TeamSpace teamSpace = null;
		for (TeamSpaceMemberships member : memberships) {
			teamSpace = teamSpaceService.getTeamSpaceInfo(null, member.getCloudUserId());
			if (teamSpace == null) {
				continue;
			}
			iNodeACLService.deleteSpaceACLsByUser(teamSpace.getCloudUserId(), String.valueOf(groupId),
					GroupConstants.USERTYPE_GROUP);

			teamSpaceMembershipService.deleteTeamSpaceMember(member.getCloudUserId(), String.valueOf(member.getUserId()), GroupConstants.USERTYPE_GROUP);
		}
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	private Integer getGroupList(List<GroupOrder> orders, String keyword, UserToken userToken, Group group, Limit limit,
			List<RestGroup> restGroups) {
		Integer totalCount = 0;
		List<GroupMemberships> groupMembershipses = null;
		// 用户Token鉴权

		groupMembershipses = groupMembershipsService.getUserList(orders, limit, userToken.getCloudUserId(),
				GroupConstants.GROUP_USERTYPE_USER, keyword);

		Group groupUser = null;
		for (GroupMemberships gm : groupMembershipses) {
			groupUser = groupService.get(gm.getGroupId());
			if (group.getType() != null) {

				// type 为private或public情况
				if (groupUser.getType().byteValue() == group.getType().byteValue()) {
					restGroups.add(new RestGroup(groupUser));
					++totalCount;
					continue;
				}
			} else {
				// type为all情况
				++totalCount;
				restGroups.add(new RestGroup(groupUser));
			}
		}
		return totalCount;
	}

	private void restGroupAttribute(Group group, RestGroupModifyRequest restGroupRequest, Group groupInfo) {
		if (group.getName() != null) {
			groupInfo.setName(group.getName());
		}
		if (group.getDescription() != null) {
			groupInfo.setDescription(group.getDescription());
		}
		if (restGroupRequest.getType() != null) {
			groupInfo.setType(group.getType());
		}
		if (restGroupRequest.getMaxMembers() != null) {
			if (restGroupRequest.getMaxMembers() == GroupConstants.REQUEST_MAXMEMBERS) {
				groupInfo.setMaxMembers(GroupConstants.MAXMEMBERS_DEFAULT);
			} else {
				groupInfo.setMaxMembers(restGroupRequest.getMaxMembers());
			}
		}
	}

	private void transModifiedDate(UserToken userToken, Group groupInfo) {
		Date date = new Date();
		groupInfo.setModifiedAt(date);
		if (userToken.getCloudUserId() == null) {
			userToken.setCloudUserId(User.APP_GROUP_ID);
		}
		groupInfo.setModifiedBy(userToken.getCloudUserId());
	}

	private String transRole(byte groupRole) {
		if (groupRole == GroupConstants.GROUP_ROLE_ADMIN) {
			return GroupConstants.ROLE_ADMIN;
		} else if (groupRole == GroupConstants.GROUP_ROLE_MANAGER) {
			return GroupConstants.ROLE_MANAGER;
		}
		return GroupConstants.ROLE_MEMBER;
	}

	private String transType(byte type) {
		if (type == GroupConstants.GROUP_TYPE_PUBLIC) {
			return GroupConstants.TYPE_PUBLIC;
		}
		return GroupConstants.TYPE_PRIVATE;
	}

	private Byte transType(String type) {
		if (StringUtils.equals(type, GroupConstants.TYPE_PRIVATE)) {
			return GroupConstants.GROUP_TYPE_DEFAULT;
		}
		return GroupConstants.GROUP_TYPE_PUBLIC;
	}

	@Override
	public Group getGroupByName(UserToken userToken, String name) {
		Group group = groupService.getByName(name, userToken.getAccountId());
		String keyword = StringUtils.trimToEmpty(name);
		String[] logParams = new String[] { String.valueOf(name) };
		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_GROUP_INFO, logParams,
				keyword);
		return group;
	}
}
