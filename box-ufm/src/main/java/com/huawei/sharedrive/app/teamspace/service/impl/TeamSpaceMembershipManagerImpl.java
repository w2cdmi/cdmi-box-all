package com.huawei.sharedrive.app.teamspace.service.impl;

import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.acl.service.INodeACLIdGenerateService;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.acl.service.ResourceRoleService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.manager.PersistentEventManager;
import com.huawei.sharedrive.app.event.service.EventService;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.group.service.GroupMembershipsService;
import com.huawei.sharedrive.app.group.service.GroupService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.message.MessageParamName;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMember;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMemberCreateRequest;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.service.SpaceStatisticsService;
import com.huawei.sharedrive.app.teamspace.domain.TeamMemberList;
import com.huawei.sharedrive.app.teamspace.domain.TeamRole;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.SpaceMemberIdGenerateService;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipManager;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.Department;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.domain.UserAccount;
import com.huawei.sharedrive.app.user.service.DepartmentService;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.LogEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TeamSpaceMembershipManagerImpl implements TeamSpaceMembershipManager {
	private static Logger logger = LoggerFactory.getLogger(TeamSpaceMembershipManagerImpl.class);

	@Autowired
	private TeamSpaceMembershipService teamSpaceMembershipService;

	@Autowired
	private INodeACLService iNodeACLService;

	@Autowired
	private ResourceRoleService resourceRoleService;

	@Autowired
	private TeamSpaceService teamSpaceService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private SpaceMemberIdGenerateService spaceMemberIdGenerateService;

	@Autowired
	private INodeACLIdGenerateService nodeACLIdGenerateService;

	@Autowired
	private EventService eventService;

	@Autowired
	private FileBaseService fileBaseService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private GroupMembershipsService groupMembershipsService;

	@Autowired
	private PersistentEventManager persistentEventManager;

	@Autowired
	private SpaceStatisticsService spaceStatisticsService;
	
	@Autowired
	private DepartmentService departmentService;

	@Override
	public void createTeamSpaceMember(UserToken userToken, long teamSpaceId, RestTeamMemberCreateRequest restTeamMemberCreateRequest, boolean isAccountOper) throws BaseRunException {

		String teamRole = restTeamMemberCreateRequest.getTeamRole();
		String role = restTeamMemberCreateRequest.getRole();

		for(RestTeamMember member : restTeamMemberCreateRequest.getMemberList()) {
			String userId = member.getId();
			String userType = member.getType();

			createTeamSpaceMember(userToken, teamSpaceId, isAccountOper, userId, userType, teamRole, role);
		}
	}

	public TeamSpaceMemberships createTeamSpaceMember(UserToken userToken, long teamSpaceId, boolean isAccountOper, String userId, String userType, String teamRole, String role) {
		// TODO 存在事务问题
		// 判断空间是否存在
		TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamSpaceId);

		checkMemberQuota(teamSpace);
		//判断添加成员是否存在，修改权限
		if (teamSpaceMembershipService.checkTeamSpaceMemberExist(teamSpaceId, userId, userType)) {
			TeamSpaceMemberships teamSpaceMemberByUser = teamSpaceMembershipService.getTeamSpaceMemberByUser(teamSpaceId, userId, userType);
			if (teamSpaceMemberByUser.getTeamRole().equals(TeamRole.ROLE_ADMIN)) {
				teamSpaceMemberByUser.setTeamSpace(teamSpace);
				return teamSpaceMemberByUser;
			}

            return modifyTeamSpaceMemberRoleById(userToken, teamSpaceId, teamSpaceMemberByUser.getId(), teamRole, role, isAccountOper);
		}

		long enterpriseId = 0;
		if (userToken.getAccountVistor() != null) {
			enterpriseId = userToken.getAccountVistor().getEnterpriseId();
		}

		// 判断是否有创建团队成员的权限
		if (!isAccountOper) {
			TeamSpaceMemberships operUserMember = teamSpaceMembershipService.getUserMemberShips(teamSpaceId,
					userToken.getId(),  String.valueOf(enterpriseId));
			if (operUserMember == null) {
				throw new ForbiddenException("not the member");
			}

			if (TeamRole.ROLE_MEMBER.equals(operUserMember.getTeamRole())) {
				throw new ForbiddenException(
						"Not allowed to create teamSpaceMember, role: " + operUserMember.getTeamRole());
			}
		}

		// 不添加拥有者
		if (TeamRole.ROLE_ADMIN.equals(teamRole)) {
			throw new InvalidTeamRoleException("teamRole is not valid, teamRole:" + teamRole);
		}

		chechTeamRoleAndRoleValid(userType, teamRole, role);

		// 判断用户是否存在,排除团队空间虚拟用户
		// 创建团队成员记录
		TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
		User user = null;

		if (TeamSpaceMemberships.TYPE_USER.equals(userType)) {
			user = userService.get(null, Long.valueOf(userId));
			if (user == null || teamSpaceService.getTeamSpaceNoCheck(Long.parseLong(userId)) != null) {
				throw new NoSuchUserException("user is not exist, userId:" + userId);
			}
			teamSpaceMemberships.setUsername(user.getName());
			teamSpaceMemberships.setLoginName(user.getLoginName());
		}
		if (TeamSpaceMemberships.TYPE_DEPT.equals(userType)) {
			//此处的userId就是deptCloudUserId
			long deptId = Long.parseLong(userId);
			Department department = departmentService.getByEnterpriseIdAndDepartmentCloudUserId(enterpriseId, deptId);
			teamSpaceMemberships.setUsername(department.getName());
			teamSpaceMemberships.setLoginName(department.getName());
		}else if (TeamSpaceMemberships.TYPE_SYSTEM.equals(userType)) {
			// userId = TeamSpaceMemberships.ID_TEAM_PUBLIC;
			// modify type system equals all user in enterprise
			userId = String.valueOf(enterpriseId) ;
			teamSpaceMemberships.setUsername(userType);
			teamSpaceMemberships.setLoginName(userType);
		} else if (TeamSpaceMemberships.TYPE_GROUP.equals(userType)) {
			Group group = groupService.get(Long.valueOf(userId));
			if (group == null) {
				throw new NoSuchGroupException("group is not exist, groupId:" + userId);
			}
			if (GroupConstants.GROUP_TYPE_DEFAULT == group.getType()) {
				GroupMemberships groupMemberships = new GroupMemberships();
				groupMemberships.setGroupId(group.getId());
				groupMemberships.setUserId(userToken.getCloudUserId());
				groupMemberships.setUserType(GroupConstants.GROUP_USERTYPE_USER);
				groupMemberships = groupMembershipsService.getUser(groupMemberships);
				if (groupMemberships == null) {
					throw new ForbiddenException(
							"Do not allow non group members, a private group is set to group members");
				}
			}
			teamSpaceMemberships.setUsername(group.getName());
			teamSpaceMemberships.setLoginName(group.getName());
		}

		Date date = new Date();

		teamSpaceMemberships.setId(spaceMemberIdGenerateService.getNextMemberId(teamSpaceId));
		teamSpaceMemberships.setCloudUserId(teamSpaceId);
		teamSpaceMemberships.setUserId(Long.parseLong(userId));
		teamSpaceMemberships.setUserType(userType);

		teamSpaceMemberships.setCreatedAt(date);
		teamSpaceMemberships.setCreatedBy(userToken.getId());
		teamSpaceMemberships.setModifiedAt(date);
		teamSpaceMemberships.setModifiedBy(userToken.getId());
		teamSpaceMemberships.setTeamRole(teamRole);
		teamSpaceMemberships = teamSpaceMembershipService.createTeamSpaceMember(teamSpaceMemberships);

		// 创建ACL
		addACLForAddMember(userToken, teamSpaceId, userId, userType, role, date, teamSpaceMemberships);

		// 添加空间附加属性：当前成员数，已使用配额，该空间创建者姓名
		fillTeamSpaceInfo(teamSpace);
		teamSpaceMemberships.setTeamSpace(teamSpace);
		teamSpaceMemberships.setMember(user);

		INodeACL nodeACL = iNodeACLService.getByResourceAndUser(teamSpaceId, INode.FILES_ROOT, String.valueOf(teamSpaceMemberships.getUserId()), userType);
		teamSpaceMemberships.setRole(nodeACL != null ? nodeACL.getResourceRole() : null);

		// 发送消息
		PersistentEvent event = generalMemberEvent(userToken.getCloudUserId(), Long.parseLong(userId), teamSpaceId,
				userType, EventType.TEAMSPACE_MEMBER_CREATE);
		persistentEventManager.fireEvent(event);

		// 发送事件
		createEvent(userToken, EventType.TEAMSPACE_MEMBER_CREATE, new INode(teamSpaceId, INode.FILES_ROOT));
		String[] logMsgs = new String[] { null, teamRole, String.valueOf(teamSpaceId) };
		if (user != null) {
			logMsgs = new String[] { StringUtils.trimToEmpty(user.getLoginName()), teamRole,
					String.valueOf(teamSpaceId) };
		}
		String keyword = StringUtils.trimToEmpty(teamSpace.getName());

		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.ADD_TEAMSPACE_MEMBER,
				logMsgs, keyword);
		return teamSpaceMemberships;
	}

	@Override
	public void deleteTeamSpaceMemberById(UserToken user, long teamSpaceId, long memberId) throws BaseRunException {

		String enterpriseId = "";
		if (user.getAccountVistor() != null) {
			enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
		}

		// 判断空间是否存在
		TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamSpaceId);

		// 判断是否有删除团队成员的权限
		TeamSpaceMemberships operUserMember;
		if(user.getId() == User.SYSTEM_USER_ID ) {
			//如果是系统管理员，构造TeamSpaceMemberships, 使用之前的流程
			operUserMember = new TeamSpaceMemberships();
			operUserMember.setTeamRole(TeamRole.ROLE_ADMIN);
		} else {
			operUserMember = teamSpaceMembershipService.getUserMemberShips(teamSpaceId, user.getId(), enterpriseId);
		}

		// 删除权限判断：团队空间拥有者可以删除管理者、普通成员；团队空间管理者可以删除普通成员和自己；团队空间普通成员可以删除自己
		if (operUserMember == null) {
			throw new ForbiddenException("not the member");
		}

		//成员
		TeamSpaceMemberships teamMember = teamSpaceMembershipService.getTeamSpaceMemberById(teamSpaceId, memberId);
		if (TeamRole.ROLE_ADMIN.equals(operUserMember.getTeamRole())) {
			// 拥有者删除管理者、普通成员
			if (!TeamRole.ROLE_MANAGER.equals(teamMember.getTeamRole())
					&& !TeamRole.ROLE_MEMBER.equals(teamMember.getTeamRole())) {
				String errorMsg = "owner not allowed to delete admin user!";
				throw new ForbiddenException(errorMsg);
			}
		} else if (TeamRole.ROLE_MANAGER.equals(operUserMember.getTeamRole())) {
			// 管理者可以删除普通成员和自己
			if (!TeamRole.ROLE_MEMBER.equals(teamMember.getTeamRole())&&!TeamRole.ROLE_MANAGER.equals(teamMember.getTeamRole())) {
				if ((user.getId() != teamMember.getUserId()) || !StringUtils.equals(TeamSpaceMemberships.TYPE_USER, teamMember.getUserType())) {
					String errorMsg = "member only allowed to delete self!";
					throw new ForbiddenException(errorMsg);
				}
			}
		} else {
			// 普通成员可以删除自己
			if ((user.getId() != teamMember.getUserId()) || !StringUtils.equals(TeamSpaceMemberships.TYPE_USER, teamMember.getUserType())) {
				String errorMsg = "member only allowed to delete self!";
				throw new ForbiddenException(errorMsg);
			}
		}

		// 先删除对应user的所有权限
		iNodeACLService.deleteSpaceACLsByUser(teamSpaceId, String.valueOf(teamMember.getUserId()), teamMember.getUserType());

		teamSpaceMembershipService.deleteTeamSpaceMemberById(teamSpaceId, teamMember.getId());

		// 发送消息
		PersistentEvent event = generalMemberEvent(user.getCloudUserId(), teamMember.getUserId(), teamSpaceId, teamMember.getUserType(), EventType.TEAMSPACE_MEMBER_DELETE);
		persistentEventManager.fireEvent(event);

		// 发送事件
		createEvent(user, EventType.TEAMSPACE_MEMBER_DELETE, new INode(teamSpaceId, INode.FILES_ROOT));
		String[] logMsgs = new String[] { user.getLoginName(),
				StringUtils.trimToEmpty(teamMember.getRole()), String.valueOf(teamSpaceId) };
		String keyword = StringUtils.trimToEmpty(teamSpace.getName());

		fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.DELETE_TEAMSPACE_MEMBER, logMsgs, keyword);
	}


	@Override
	public void batchDeleteTeamSpaceMemberById(UserToken user, Long teamSpaceId, Long[] memberIds) {

		String enterpriseId = "";
		if (user.getAccountVistor() != null) {
			enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
		}
		// 判断空间是否存在
		TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamSpaceId);
        for(int i=0;i<memberIds.length;i++){
			TeamSpaceMemberships teamMember = teamSpaceMembershipService.getTeamSpaceMemberById(teamSpaceId, memberIds[i]);
			// 先删除对应user的所有权限
			iNodeACLService.deleteSpaceACLsByUser(teamSpaceId, String.valueOf(teamMember.getUserId()), teamMember.getUserType());

			teamSpaceMembershipService.deleteTeamSpaceMemberById(teamSpaceId, teamMember.getId());

			// 发送消息
			PersistentEvent event = generalMemberEvent(user.getCloudUserId(), teamMember.getUserId(), teamSpaceId, teamMember.getUserType(), EventType.TEAMSPACE_MEMBER_DELETE);
			persistentEventManager.fireEvent(event);

			// 发送事件
			createEvent(user, EventType.TEAMSPACE_MEMBER_DELETE, new INode(teamSpaceId, INode.FILES_ROOT));
			String[] logMsgs = new String[] { user.getLoginName(),
					StringUtils.trimToEmpty(teamMember.getRole()), String.valueOf(teamSpaceId) };
			String keyword = StringUtils.trimToEmpty(teamSpace.getName());

			fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.DELETE_TEAMSPACE_MEMBER, logMsgs, keyword);
		}

	}

	@Override
	public TeamSpaceMemberships getTeamSpaceMemberById(UserToken user, long cloudUserID, long id)
			throws BaseRunException {

		String enterpriseId = "";
		if (user.getAccountVistor() != null) {
			enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
		}

		// 判断空间是否存在
		TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(cloudUserID);

		// 判断是否有获取团队成员的权限
		TeamSpaceMemberships operMemberships = teamSpaceMembershipService.getUserMemberShips(cloudUserID, user.getId(),
				enterpriseId);

		if (operMemberships == null) {
			throw new ForbiddenException("not the member");
		}

		if (operMemberships.getId() != id) {
			if (!TeamRole.ROLE_ADMIN.equals(operMemberships.getTeamRole())
					&& !TeamRole.ROLE_MANAGER.equals(operMemberships.getTeamRole())) {
				String errorMsg = "Not allowed to get teamSpaceMemberships, teamrole: " + operMemberships.getTeamRole();
				throw new ForbiddenException(errorMsg);
			}
		}

		TeamSpaceMemberships teamSpaceMemberships = teamSpaceMembershipService.getTeamSpaceMemberById(cloudUserID, id);

		// 添加空间附加属性：当前成员数，最大配额,已使用配额，该空间创建者姓名
		fillTeamSpaceInfo(teamSpace);
		teamSpaceMemberships.setTeamSpace(teamSpace);
		User userInfo = null;

		if (TeamSpaceMemberships.TYPE_USER.equals(teamSpaceMemberships.getUserType())) {
			userInfo = userService.get(null, Long.valueOf(teamSpaceMemberships.getUserId()));
			teamSpaceMemberships.setMember(userInfo);
		}

		INodeACL nodeACL = iNodeACLService.getByResourceAndUser(cloudUserID, INode.FILES_ROOT, String.valueOf(teamSpaceMemberships.getUserId()), teamSpaceMemberships.getUserType());
		teamSpaceMemberships.setRole(nodeACL != null ? nodeACL.getResourceRole() : null);

		String[] logMsgs = new String[] { StringUtils.trimToEmpty(userInfo != null ? userInfo.getLoginName() : null),
				StringUtils.trimToEmpty(teamSpaceMemberships.getRole()), String.valueOf(cloudUserID) };
		String keyword = StringUtils.trimToEmpty(teamSpace.getName());
		fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.GET_TEAMSPACE_MEMBER, logMsgs,
				keyword);
		return teamSpaceMemberships;
	}

	@Override
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public TeamMemberList listTeamSpaceMemberships(UserToken user, long teamSpaceId, List<Order> orderList, Limit limit,
			String teamRole, String keyword, boolean isAccountOper) throws BaseRunException {

		String enterpriseId = "";
		if (user.getAccountVistor() != null) {
			enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
		}

		// 判断空间是否存在
		TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamSpaceId);


		// teamRole允许传入参数为： admin:拥有者manager:管理者member:普通用户all:所有用户
		if (StringUtils.isNotEmpty(teamRole) && !"all".equals(teamRole)) {
			// 检查团队角色合法性
			checkTeamRoleValid(teamRole);
		} else {
			// 如果是空或"all", 则传null值, 表示不过滤
			teamRole = null;
		}

		TeamMemberList teamMemberList = teamSpaceMembershipService.listTeamSpaceMemberships(teamSpaceId, orderList,
				limit, teamRole, keyword);

		// 设置返回体中的用户信息
		List<TeamSpaceMemberships> itemList = teamMemberList.getTeamMemberList();

		INodeACL nodeACL = null;
		User userInfo = null;
		
		//部门空间中部门所包含的user
		List<TeamSpaceMemberships> deptMemberships = new ArrayList<>();
		
		for (TeamSpaceMemberships item : itemList) {
			nodeACL = iNodeACLService.getByResourceAndUser(teamSpaceId, INode.FILES_ROOT, String.valueOf(item.getUserId()), item.getUserType());
			item.setRole(nodeACL != null ? nodeACL.getResourceRole() : null);
			if (TeamSpaceMemberships.TYPE_USER.equals(item.getUserType())) {
				userInfo = userService.get(null, Long.valueOf(item.getUserId()));
				item.setMember(userInfo);
			} else if (StringUtils.equals(TeamSpaceMemberships.TYPE_GROUP, item.getUserType())) {
				userInfo = new User();
				Group group = groupService.get(Long.valueOf(item.getUserId()));
				if (group == null) {
					throw new NoSuchGroupException();
				}
				userInfo.setId(group.getId());
				userInfo.setLoginName(group.getName());
				userInfo.setName(group.getName());
				item.setMember(userInfo);
			} else if (StringUtils.equals(TeamSpaceMemberships.TYPE_DEPT, item.getUserType())){
				
				userInfo = new User();
				userInfo.setId(item.getUserId());
				userInfo.setLoginName(item.getLoginName());
				userInfo.setName(item.getUsername());
				item.setMember(userInfo);
				if(teamSpace.getType()==TeamSpace.TYPE_OFFICIAL){
					List<UserAccount> userlist = departmentService.getUsersByDept(item.getUserId(),enterpriseId,user.getAccountId());
					
					for(int i=0;i<userlist.size();i++){
						TeamSpaceMemberships userShip=new TeamSpaceMemberships();
						UserAccount userAccount = userlist.get(i);
						User userAccountInfo = new User();
						userAccountInfo.setId(userAccount.getCloudUserId());
						userAccountInfo.setLoginName(userAccount.getName());
						userAccountInfo.setName(userAccount.getName());
						userShip.setUserId(userAccount.getCloudUserId());
						userShip.setMember(userAccountInfo);
						userShip.setCreatedAt(item.getCreatedAt());
						userShip.setModifiedAt(item.getModifiedAt());
						userShip.setUserType(TeamSpaceMemberships.TYPE_USER);
						userShip.setTeamSpace(teamSpace);
						userShip.setTeamRole(item.getTeamRole());
						userShip.setRole(item.getRole());
						if(user.getCloudUserId()!=userAccount.getCloudUserId()){
							deptMemberships.add(userShip);
						}
					}
				}
			}
			
			
			
			// 添加空间附加属性：当前成员数，最大配额,已使用配额，该空间创建者姓名
			fillTeamSpaceInfo(teamSpace);
			item.setTeamSpace(teamSpace);

		}
		
		itemList.addAll(deptMemberships);
		teamMemberList.setTeamMemberList(itemList);
		String[] logMsgs = new String[] { String.valueOf(teamSpaceId) };
		String key = StringUtils.trimToEmpty(teamSpace.getName());
		try {
			fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.LIST_TEAMSPACE_MEMBER, logMsgs,key);
		} catch (Exception e) {
			// TODO: handle exception
		}
		logger.warn("teamMemberList member userType " +teamMemberList.getTeamMemberList().get(0).getUserType());
		return teamMemberList;
	}
	
	
	
	@Override
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public long countTeamSpaceMemberships(UserToken user, long teamSpaceId, List<Order> orderList, Limit limit,
			String teamRole, String keyword, boolean isAccountOper) throws BaseRunException {

		String enterpriseId = "";
		if (user.getAccountVistor() != null) {
			enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
		}

		// 判断空间是否存在
		TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamSpaceId);


		// teamRole允许传入参数为： admin:拥有者manager:管理者member:普通用户all:所有用户
		if (StringUtils.isNotEmpty(teamRole) && !"all".equals(teamRole)) {
			// 检查团队角色合法性
			checkTeamRoleValid(teamRole);
		} else {
			// 如果是空或"all", 则传null值, 表示不过滤
			teamRole = null;
		}

		long  totalCount = teamSpaceMembershipService.countTeamSpaceMemberships(teamSpaceId, orderList,
				limit, teamRole, keyword);

		return totalCount;
	}

	@Override
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public TeamSpaceMemberships modifyTeamSpaceMemberRoleById(UserToken userToken, long teamSpaceId, long id,
			String teamRole, String role, boolean isAccountOper) throws BaseRunException {
		String enterpriseId = "";
		if (userToken.getAccountVistor() != null) {
			enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
		}
		// 判断空间是否存在 TODO 事务问题
		TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamSpaceId);

		TeamSpaceMemberships operUserMember = null;
		if (!isAccountOper) {
			// 判断是否有修改团队成员的权限:只能拥有者和管理者具有修改权限
			operUserMember = teamSpaceMembershipService.getUserMemberShips(teamSpaceId, userToken.getId(),
					enterpriseId);

			if (operUserMember == null) {
				throw new ForbiddenException("not the member");
			}
		}
		if (isAccountOper) {
			//用户创建普通空间
			if(teamSpace.getOwnerBy() > 0) {
				// 判断是否有修改团队成员的权限:只能拥有者和管理者具有修改权限
				operUserMember = teamSpaceMembershipService.getUserMemberShips(teamSpaceId, teamSpace.getOwnerBy(),
						enterpriseId);

				if (operUserMember == null) {
					throw new ForbiddenException("not the member");
				}
			} else {
				//由系统创建的团队空间
				operUserMember = new TeamSpaceMemberships();
				operUserMember.setTeamRole(TeamRole.ROLE_ADMIN);
			}
		}

		TeamSpaceMemberships teamSpaceMemberships = teamSpaceMembershipService.getTeamSpaceMemberById(teamSpaceId, id);

		checkModifyOperRight(operUserMember, teamSpaceMemberships);

		chechTeamRoleAndRoleValid(teamSpaceMemberships.getUserType(), teamRole, role);

		String originTeamRole = teamSpaceMemberships.getTeamRole();

		teamSpaceMemberships = modifyTeamRoleById(userToken, teamRole, operUserMember, teamSpaceMemberships);

		// 修改ACL
		// question:修改空间成员角色是否需要根据目标权限对子文件夹的权限做递归处理
		// answer : 由應用層保證
		try {
			modifyNodeACL(userToken, teamSpaceId, role, teamSpaceMemberships);
		} catch (Exception e) {
			if (StringUtils.isNotBlank(teamRole) && !originTeamRole.equals(teamRole)) {
				teamSpaceMemberships.setTeamRole(originTeamRole);
				// 修改团队成员记录
				teamSpaceMembershipService.modifyTeamSpaceMemberRole(teamSpaceMemberships);
			}
			logger.error("createTeamSpaceMember fail", e);
			throw e;
		}

		User userInfo = null;

		if (TeamSpaceMemberships.TYPE_USER.equals(teamSpaceMemberships.getUserType())) {
			userInfo = userService.get(null, teamSpaceMemberships.getUserId());
			teamSpaceMemberships.setMember(userInfo);
		}

		// 添加空间附加属性：当前成员数，最大配额,已使用配额，该空间创建者姓名
		fillTeamSpaceInfo(teamSpace);

		INodeACL nodeACL = iNodeACLService.getByResourceAndUser(teamSpaceId, INode.FILES_ROOT, String.valueOf(teamSpaceMemberships.getUserId()), teamSpaceMemberships.getUserType());
		teamSpaceMemberships.setRole(nodeACL != null ? nodeACL.getResourceRole() : null);

		autoChangetTeamRole(userToken, teamSpaceId, teamRole, teamSpace, operUserMember, teamSpaceMemberships);

		teamSpaceMemberships.setTeamSpace(teamSpace);
		if (userToken.getCloudUserId() == null) {
			userToken.setCloudUserId(teamSpaceMemberships.getCreatedBy());
		}
		// 发送消息
		PersistentEvent event = generalRoleUpdateEvent(userToken.getCloudUserId(), teamSpaceMemberships.getUserId(), teamSpaceId, teamSpaceMemberships.getUserType(),
				teamRole, role);
		persistentEventManager.fireEvent(event);

		// 发送事件
		createEvent(userToken, EventType.TEAMSPACE_MEMBER_UPDATE, new INode(teamSpaceId, INode.FILES_ROOT));

		if (TeamSpaceMemberships.TYPE_USER.equals(teamSpaceMemberships.getUserType())) {
			userInfo = userService.get(null, teamSpaceMemberships.getUserId());
			teamSpaceMemberships.setMember(userInfo);
		}
		String[] logMsgs = new String[] { StringUtils.trimToEmpty(userInfo != null ? userInfo.getLoginName() : null),
				teamRole, String.valueOf(teamSpaceId) };
		String keyword = StringUtils.trimToEmpty(teamSpace.getName());

		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.MODIFY_TEAMSPACE_MEMBER,
				logMsgs, keyword);
		return teamSpaceMemberships;
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	private void addACLForAddMember(UserToken userToken, long teamSpaceId, String userId, String userType, String role,
			Date date, TeamSpaceMemberships teamSpaceMemberships) {
		String enterpriseId = "";
		if (userToken.getAccountVistor() != null) {
			enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
		}
		if (StringUtils.isNotBlank(role)) {
			INodeACL iNodeACL = new INodeACL();
			iNodeACL.setId(nodeACLIdGenerateService.getNextNodeACLId(teamSpaceId));
			if (TeamSpaceMemberships.TYPE_SYSTEM.equals(userType)) {
				iNodeACL.setAccessUserId(enterpriseId);
				iNodeACL.setUserType(INodeACL.TYPE_SYSTEM);
			} else {
				iNodeACL.setAccessUserId(userId);
				iNodeACL.setUserType(userType);
			}

			iNodeACL.setCreatedAt(date);
			iNodeACL.setCreatedBy(userToken.getId());
			iNodeACL.setiNodeId(INode.FILES_ROOT);
			iNodeACL.setiNodePid(INode.FILES_ROOT);
			iNodeACL.setOwnedBy(teamSpaceId);
			iNodeACL.setModifiedAt(date);
			iNodeACL.setModifiedBy(userToken.getId());
			iNodeACL.setResourceRole(role);
			try {
				iNodeACLService.addINodeACL(iNodeACL);
			} catch (Exception e) {
				// 跨库异常回滚
				teamSpaceMembershipService.deleteTeamSpaceMemberById(teamSpaceId, teamSpaceMemberships.getId());
				logger.error("createTeamSpaceMember fail", e);
				throw e;
			}
		}
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	private void autoChangetTeamRole(UserToken userToken, long teamSpaceId, String teamRole, TeamSpace teamSpace,
			TeamSpaceMemberships operUserMember, TeamSpaceMemberships teamSpaceMemberships) throws BaseRunException {
		if (StringUtils.isNotBlank(teamRole)) {
			Date date = new Date();
			// 如果将其他成员更新为拥有者，则自身需要变为管理者
			if (isChangeOwner(teamRole, operUserMember)) {
				long userId = teamSpaceMemberships.getUserId();
				String userType = teamSpaceMemberships.getUserType();
				operUserMember.setModifiedBy(userToken.getId());
				operUserMember.setModifiedAt(date);
				operUserMember.setTeamRole(TeamRole.ROLE_MANAGER);
				teamSpaceMembershipService.modifyTeamSpaceMemberRole(operUserMember);

				teamSpaceService.updateOwner(userToken, teamSpaceId, userId);
				teamSpace.setOwnerBy(userId);

				// 直接增加对应auter权限
				iNodeACLService.deleteByResourceAndUser(teamSpaceId, INode.FILES_ROOT, String.valueOf(userId),
						userType);
				INodeACL iNodeACL = new INodeACL();
				iNodeACL.setId(nodeACLIdGenerateService.getNextNodeACLId(teamSpaceId));
				iNodeACL.setAccessUserId(String.valueOf(userId));
				iNodeACL.setUserType(userType);
				iNodeACL.setCreatedAt(date);
				iNodeACL.setCreatedBy(userToken.getId());
				iNodeACL.setiNodeId(INode.FILES_ROOT);
				iNodeACL.setiNodePid(INode.FILES_ROOT);
				iNodeACL.setOwnedBy(teamSpaceId);
				iNodeACL.setModifiedAt(date);
				iNodeACL.setModifiedBy(userToken.getId());
				iNodeACL.setResourceRole(ResourceRole.AUTHER);
				iNodeACLService.addINodeACL(iNodeACL);
			}

		}
	}

	private void chechTeamRoleAndRoleValid(String userType, String teamRole, String role)
			throws InvalidTeamRoleException, InvalidPermissionRoleException {

		if (StringUtils.isNotBlank(teamRole)) {
			// 检查团队角色合法性
			checkTeamRoleValid(teamRole);

			if ((TeamSpaceMemberships.TYPE_SYSTEM.equals(userType)) && TeamRole.ROLE_MANAGER.equals(teamRole)) {
				String errorMsg = "Not allowed to create manager role on group or system";
				throw new ForbiddenException(errorMsg);
			}
		}

		if (StringUtils.isNotBlank(role)) {
			// 检查权限角色合法性
			checkRoleValid(role);

			// MANAGER 不能指定为群组
			if (TeamSpaceMemberships.TYPE_SYSTEM.equals(userType) && ResourceRole.AUTHER.equals(role)) {
				String errorMsg = "Not allowed to create AUTHER role on group or system";
				throw new ForbiddenException(errorMsg);
			}
		}
	}

	private void checkMemberQuota(TeamSpace teamSpace) {
		long courNumber = teamSpaceMembershipService.getTeamSpaceMembersCount(teamSpace.getCloudUserId());

		if (teamSpace.getMaxMembers() != TeamSpace.MEMBER_NUM_UNLIMITED && courNumber >= teamSpace.getMaxMembers()) {
			String errorMsg = "teamspace total Member Num exceed maximum , maximum:" + teamSpace.getMaxMembers()
					+ ", current:" + courNumber;
			throw new ExceedTeamSpaceMaxMemberNumException(errorMsg);
		}
	}

	private void checkModifyOperRight(TeamSpaceMemberships operUserMember, TeamSpaceMemberships teamSpaceMemberships)
			throws ForbiddenException {
		if (!TeamRole.ROLE_ADMIN.equals(operUserMember.getTeamRole())
				&& !TeamRole.ROLE_MANAGER.equals(operUserMember.getTeamRole())) {
			String errorMsg = "Not allowed to modify teamSpaceMember, teamrole: " + operUserMember.getTeamRole();
			throw new ForbiddenException(errorMsg);
		}

		// 不能修改自身角色
		if (teamSpaceMemberships.getId() == operUserMember.getId()) {
			String errorMsg = "Not allowed to modify teamSpaceMember role self, id: " + operUserMember.getId();
			throw new ForbiddenException(errorMsg);
		}
	}

	private void checkRoleValid(String role) throws InvalidPermissionRoleException {
		if (StringUtils.isNotBlank(role)) {
			ResourceRole roleInfo = resourceRoleService.getResourceRole(role);
			// 检查权限角色合法性
			if (roleInfo == null) {
				String errorMsg = "role is not valid, role:" + role;
				throw new InvalidPermissionRoleException(errorMsg);
			}
			// 数据库不区分大小写，需要判断
			if (!StringUtils.equals(role, roleInfo.getResourceRole())) {
				String errorMsg = "role is not valid, role:" + role;
				throw new InvalidPermissionRoleException(errorMsg);
			}
		}
	}

	private void checkTeamRoleValid(String teamRole) throws InvalidTeamRoleException {
		if (!TeamRole.ROLE_ADMIN.equals(teamRole) && !TeamRole.ROLE_MANAGER.equals(teamRole)
				&& !TeamRole.ROLE_MEMBER.equals(teamRole)) {
			throw new InvalidTeamRoleException("teamRole is not valid, teamRole:" + teamRole);
		}

	}

	private void createEvent(UserToken userToken, EventType type, INode srcNode) {
		LogEvent.createEvent(userToken, type, srcNode, eventService, logger);
	}

	/**
	 * 添加团队空间附加信息
	 * 
	 * @param teamSpace
	 * @throws BaseRunException
	 */
	private void fillTeamSpaceInfo(TeamSpace teamSpace) throws BaseRunException {
		User teamSpaceUser;
		User ownerUser;
		User createUser;
		// 设置当前的成员数
		teamSpace.setCurNumbers(teamSpaceMembershipService.getTeamSpaceMembersCount(teamSpace.getCloudUserId()));

		// 设置最大版本数
		teamSpaceUser = userService.get(null, teamSpace.getCloudUserId());
		if (teamSpaceUser == null) {
			String excepMessage = "no such teamSpace user, cloudUserID:" + teamSpace.getCloudUserId();
			throw new NoSuchUserException(excepMessage);
		}

		teamSpace.setMaxVersions(teamSpaceUser.getMaxVersions());
		UserStatisticsInfo userInfo = spaceStatisticsService.getUserCurrentInfo(teamSpace.getCloudUserId(),
				teamSpaceUser.getAccountId());
		teamSpace.setSpaceUsed(userInfo.getSpaceUsed());
		teamSpace.setSpaceQuota(teamSpaceUser.getSpaceQuota());

		if(teamSpace.getCreatedBy()!=-2){
			// 设置创建者名称
			createUser = userService.get(null, teamSpace.getCreatedBy());
			if (createUser == null) {
				String excepMessage = "no such user for create this teamSpace, userId:" + teamSpace.getCreatedBy();
				logger.error(excepMessage);
			} else {
				teamSpace.setCreatedByUserName(createUser.getName());
			}

			// 设置拥有者名称
			ownerUser = userService.get(null, teamSpace.getOwnerBy());
			if (ownerUser == null) {
				String excepMessage = "no such user for own this teamSpace, userId:" + teamSpace.getOwnerBy();
				logger.error(excepMessage);
			} else {
				teamSpace.setOwnerByUserName(ownerUser.getName());
			}
		}
		
	}

	private PersistentEvent generalMemberEvent(Long providerId, Long recieverId, Long teamSpaceId, String memberType,
			EventType eventType) {
		PersistentEvent event = new PersistentEvent();
		event.setEventType(eventType);
		event.addParameter(MessageParamName.TEAMSPACE_ID, teamSpaceId);
		event.addParameter(MessageParamName.PROVIDER_ID, providerId);
		event.addParameter(MessageParamName.RECEIVER_ID, recieverId);
		event.addParameter(MessageParamName.MEMBER_TYPE, memberType);
		return event;
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	private PersistentEvent generalRoleUpdateEvent(Long providerId, Long recieverId, Long teamSpaceId,
			String memberType, String teamRole, String role) {
		PersistentEvent event = new PersistentEvent();
		event.setEventType(EventType.TEAMSPACE_MEMBER_UPDATE);
		event.addParameter(MessageParamName.TEAMSPACE_ID, teamSpaceId);
		event.addParameter(MessageParamName.PROVIDER_ID, providerId);
		event.addParameter(MessageParamName.RECEIVER_ID, recieverId);
		event.addParameter(MessageParamName.MEMBER_TYPE, memberType);
		if (TeamRole.ROLE_MEMBER.equals(teamRole)) {
			event.addParameter(MessageParamName.CURRENT_ROLE, role);
		} else {
			event.addParameter(MessageParamName.CURRENT_ROLE, teamRole);
		}
		return event;
	}

	private boolean isChangeOwner(String teamRole, TeamSpaceMemberships operMember) {
		return TeamRole.ROLE_ADMIN.equals(operMember.getTeamRole()) && TeamRole.ROLE_ADMIN.equals(teamRole);
	}

	private void modifyNodeACL(UserToken userToken, long teamSpaceId, String role,
			TeamSpaceMemberships teamSpaceMemberships) throws BaseRunException {
		Date date = new Date();
		if (StringUtils.isNotBlank(role)) {
			INodeACL iNodeACL = iNodeACLService.getByResourceAndUser(teamSpaceId, INode.FILES_ROOT, String.valueOf(teamSpaceMemberships.getUserId()), teamSpaceMemberships.getUserType());

			if (iNodeACL == null) {
				iNodeACL = new INodeACL();
				iNodeACL.setId(nodeACLIdGenerateService.getNextNodeACLId(teamSpaceId));
				iNodeACL.setAccessUserId(String.valueOf(teamSpaceMemberships.getUserId()));
				iNodeACL.setUserType(teamSpaceMemberships.getUserType());
				iNodeACL.setCreatedAt(date);
				iNodeACL.setCreatedBy(userToken.getId());
				iNodeACL.setiNodeId(INode.FILES_ROOT);
				iNodeACL.setiNodePid(INode.FILES_ROOT);
				iNodeACL.setOwnedBy(teamSpaceId);
				iNodeACL.setModifiedAt(date);
				iNodeACL.setModifiedBy(userToken.getId());
				iNodeACL.setResourceRole(role);
				iNodeACLService.addINodeACL(iNodeACL);
			} else if (!role.equals(iNodeACL.getResourceRole())) {
				iNodeACL.setModifiedAt(date);
				iNodeACL.setModifiedBy(userToken.getId());

				iNodeACL.setResourceRole(role);
				iNodeACLService.modifyINodeACLById(iNodeACL);
			} else {
				logger.info("role not change, no need to update acL");
			}

		}
	}

	private TeamSpaceMemberships modifyTeamRoleById(UserToken userToken, String teamRole,
			TeamSpaceMemberships operUserMember, TeamSpaceMemberships teamSpaceMemberships) throws ForbiddenException {
		if (StringUtils.isBlank(teamRole)) {
			return teamSpaceMemberships;
		}
		if (TeamRole.ROLE_MANAGER.equals(operUserMember.getTeamRole())) {
			if (!TeamRole.ROLE_MEMBER.equals(teamSpaceMemberships.getTeamRole())) {
				String errorMsg = "Not allowed manager to modify the teamSpaceMember teamrole: "
						+ operUserMember.getTeamRole();
				throw new ForbiddenException(errorMsg);
			}

			if (!TeamRole.ROLE_MEMBER.equals(teamRole)) {
				String errorMsg = "Not allowed manager to modify teamSpaceMember teamrole to: " + teamRole;
				throw new ForbiddenException(errorMsg);
			}
		}

		// 修改权限判断：变更拥有者,变更管理员只能指定type为user
		if (TeamRole.ROLE_ADMIN.equals(teamRole) || TeamRole.ROLE_MANAGER.equals(teamRole)) {
			if (TeamRole.ROLE_MANAGER.equals(teamRole)) {
				if (!teamSpaceMemberships.getUserType().equals(TeamSpaceMemberships.TYPE_USER)
						&& !teamSpaceMemberships.getUserType().equals(TeamSpaceMemberships.TYPE_GROUP)) {
					String errorMsg = "Only allowed set manager role to user type or group type";
					throw new ForbiddenException(errorMsg);
				}
			} else {
				if (!teamSpaceMemberships.getUserType().equals(TeamSpaceMemberships.TYPE_USER)) {
					String errorMsg = "Only allowed set admin role to user type";
					throw new ForbiddenException(errorMsg);
				}
			}
		}
		if (!teamSpaceMemberships.getTeamRole().equals(teamRole)) {
			teamSpaceMemberships.setTeamRole(teamRole);
			teamSpaceMemberships.setModifiedAt(new Date());
			teamSpaceMemberships.setModifiedBy(userToken.getId());
			// 修改团队成员记录
			teamSpaceMemberships = teamSpaceMembershipService.modifyTeamSpaceMemberRole(teamSpaceMemberships);
		}
		return teamSpaceMemberships;
	}

	@Override
	public void deleteMemberByUserId(UserToken user, long teamSpaceId, long cloudUserId) throws BaseRunException {
		// 判断空间是否存在
		TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamSpaceId);

		//成员
		TeamSpaceMemberships teamMember = teamSpaceMembershipService.getByTeamIdAndUserId(teamSpaceId, cloudUserId);
        if (teamMember != null) {
			// 判断是否有删除团队成员的权限
			if(!hasDeletePrivilege(user, teamMember)) {
				throw new ForbiddenException("Does not have the DELETE privilege.");
			}

			// 先删除对应user的所有权限
			iNodeACLService.deleteSpaceACLsByUser(teamSpaceId, String.valueOf(teamMember.getUserId()), teamMember.getUserType());
			teamSpaceMembershipService.deleteTeamSpaceMemberById(teamSpaceId, teamMember.getId());

			// 发送消息
			PersistentEvent event = generalMemberEvent(user.getCloudUserId(), teamMember.getUserId(), teamSpaceId, teamMember.getUserType(), EventType.TEAMSPACE_MEMBER_DELETE);
			persistentEventManager.fireEvent(event);

			// 发送事件
			createEvent(user, EventType.TEAMSPACE_MEMBER_DELETE, new INode(teamSpaceId, INode.FILES_ROOT));
			String[] logMsgs = new String[] { user.getLoginName(),
					StringUtils.trimToEmpty(teamMember.getRole()), String.valueOf(teamSpaceId) };
			String keyword = StringUtils.trimToEmpty(teamSpace.getName());

			fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.DELETE_TEAMSPACE_MEMBER, logMsgs, keyword);
			
		}
	}

	// 删除权限判断：团队空间拥有者可以删除管理者、普通成员；团队空间管理者可以删除普通成员和自己；团队空间普通成员可以删除自己
	private boolean hasDeletePrivilege(UserToken user, TeamSpaceMemberships target) {
		if(user.getId() == User.SYSTEM_USER_ID ) {
			//如果是系统管理员，放行
			return true;
		}

		//团队空间成员可以删除自己
		if(user.getId() == target.getUserId()) {
			return true;
		}

		TeamSpaceMemberships own = teamSpaceMembershipService.getByTeamIdAndUserId(target.getCloudUserId(), user.getId());

		//不在该团队空间中
		if (own == null) {
			return false;
		}

		//团队空间拥有者可以删除管理者、普通成员；
		if(!TeamRole.ROLE_ADMIN.equals(own.getTeamRole())){
			return true;
		}

		//团队空间管理者可以删除普通成员和自己；
		if(!TeamRole.ROLE_MANAGER.equals(own.getTeamRole())){
			if(!TeamRole.ROLE_ADMIN.equals(target.getTeamRole())) {
				return true;
			}
		}

		return false;
	}

}
