package com.huawei.sharedrive.app.teamspace.service.impl;

import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.acl.service.INodeACLIdGenerateService;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventService;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.files.dao.INodeDAOV2;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.NodeUpdateService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceCreateRequest;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceModifyRequest;
import com.huawei.sharedrive.app.plugins.preview.manager.FilePreviewManager;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.service.SpaceStatisticsService;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceDAO;
import com.huawei.sharedrive.app.teamspace.domain.*;
import com.huawei.sharedrive.app.teamspace.service.SpaceMemberIdGenerateService;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.LogEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pw.cdmi.box.app.convertservice.domain.TaskBean;
import pw.cdmi.box.app.teamspaceattribute.service.TeamSpaceAttributeService;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import java.util.*;

@Component
public class TeamSpaceServiceImpl implements TeamSpaceService {

	private static Logger logger = LoggerFactory.getLogger(TeamSpaceServiceImpl.class);

	@Autowired
	private INodeACLService iNodeACLService;

	@Autowired
	private INodeACLIdGenerateService nodeACLIdGenerateService;

	@Autowired
	private SpaceMemberIdGenerateService spaceMemberIdGenerateService;

	@Autowired
	private TeamSpaceDAO teamSpaceDAO;

	@Autowired
	private TeamSpaceAttributeService teamSpaceAttributeService;

	@Autowired
	private NodeUpdateService nodeUpdateService;

	@Autowired
	private TeamSpaceMembershipService teamSpaceMembershipService;

	@Autowired
	private UserService userService;

	@Autowired
	private FileBaseService fileBaseService;

	@Autowired
	private EventService eventService;

	@Autowired
	private SpaceStatisticsService spaceStatisticsService;

	@Autowired
	private INodeDAOV2 iNodeDAOV2;

	@Autowired
	private FilePreviewManager filePreviewManager;

	@Override
	public TeamSpace changeTeamSpaceOwner(TeamSpace teamSpace, long newOwnerId) throws BaseRunException {
		// 用户是否存在
		User newUser = userService.get(newOwnerId);
		if (newUser == null) {
			throw new NoSuchUserException("no such user for newOwnerId, newOwnerId:" + newOwnerId);
		}

		long oldOwnerBy = teamSpace.getOwnerBy();
		if (oldOwnerBy == newOwnerId) {
			//不需要更改，直接返回
			return teamSpace;
		}

		// 兼容老版本，老版本的ownerBy默认值为-1
		if (oldOwnerBy == User.ANONYMOUS_USER_ID) {
			oldOwnerBy = teamSpace.getCreatedBy();
		}

		Date date = new Date();
		TeamSpaceMemberships oldAdmin = teamSpaceMembershipService.getTeamSpaceMemberByUserNoCheck(teamSpace.getCloudUserId(), String.valueOf(oldOwnerBy), TeamSpaceMemberships.TYPE_USER);
		if (oldAdmin != null) {
			oldAdmin.setModifiedBy(User.APP_USER_ID);
			oldAdmin.setModifiedAt(date);
			//不管之前的权限，直接设置为manager
			oldAdmin.setTeamRole(TeamRole.ROLE_MANAGER);
			teamSpaceMembershipService.modifyTeamSpaceMemberRole(oldAdmin);
		}

		TeamSpaceMemberships newMember = teamSpaceMembershipService.getTeamSpaceMemberByUserNoCheck(teamSpace.getCloudUserId(), String.valueOf(newOwnerId), TeamSpaceMemberships.TYPE_USER);
		if (newMember == null) {
			newMember = new TeamSpaceMemberships();
			// id生成器
			newMember.setId(spaceMemberIdGenerateService.getNextMemberId(teamSpace.getCloudUserId()));
			newMember.setCloudUserId(teamSpace.getCloudUserId());
			newMember.setCreatedAt(date);
			newMember.setCreatedBy(User.APP_USER_ID);
			newMember.setModifiedAt(date);
			newMember.setModifiedBy(User.APP_USER_ID);
			newMember.setStatus(TeamSpaceMemberships.STATUS_NORMAL);
			newMember.setTeamRole(TeamRole.ROLE_ADMIN);
			newMember.setUserId(newOwnerId);
			newMember.setUserType(TeamSpaceMemberships.TYPE_USER);
			newMember.setUsername(newUser.getName());
			newMember.setLoginName(newUser.getLoginName());
			teamSpaceMembershipService.createTeamSpaceMember(newMember);
		} else {
			newMember.setTeamRole(TeamRole.ROLE_ADMIN);
			newMember.setModifiedAt(date);
			newMember.setModifiedBy(User.APP_USER_ID);
			// 修改团队成员记录
			newMember = teamSpaceMembershipService.modifyTeamSpaceMemberRole(newMember);

			//删除之前的ACL配置
			iNodeACLService.deleteByResourceAndUser(teamSpace.getCloudUserId(), INode.FILES_ROOT, String.valueOf(newOwnerId), INodeACL.TYPE_USER);
		}

		// 直接增加对应author权限
		INodeACL iNodeACL = new INodeACL();
		iNodeACL.setId(nodeACLIdGenerateService.getNextNodeACLId(teamSpace.getCloudUserId()));
		iNodeACL.setAccessUserId(String.valueOf(newOwnerId));
		iNodeACL.setUserType(INodeACL.TYPE_USER);
		iNodeACL.setCreatedAt(date);
		iNodeACL.setCreatedBy(User.APP_USER_ID);
		iNodeACL.setiNodeId(INode.FILES_ROOT);
		iNodeACL.setiNodePid(INode.FILES_ROOT);
		iNodeACL.setOwnedBy(teamSpace.getCloudUserId());
		iNodeACL.setModifiedAt(date);
		iNodeACL.setModifiedBy(User.APP_USER_ID);
		iNodeACL.setResourceRole(ResourceRole.AUTHER);
		try {
			iNodeACLService.addINodeACL(iNodeACL);
		} catch (Exception e) {
			// 跨库异常回滚
			teamSpaceMembershipService.deleteTeamSpaceMemberById(teamSpace.getCloudUserId(), newMember.getId());
			logger.error("changeOwner fail", e);
			throw e;
		}

		teamSpace.setOwnerBy(newOwnerId);
		teamSpace.setModifiedAt(date);
		teamSpace.setModifiedBy(User.APP_USER_ID);

		// 更新团队空间owner字段
		teamSpaceDAO.changeOwner(teamSpace);

		// 发送事件
		createEvent(new UserToken(), EventType.TEAMSPACE_CHANGEOWNER, new INode(teamSpace.getCloudUserId(), INode.FILES_ROOT));

		// 添加空间附加属性：当前成员数，最大配额,已使用配额，该空间创建者姓名
		fillTeamSpaceInfo(teamSpace);

		return teamSpace;
	}
	
	
	
	
	
	
	@Override
	public TeamSpace migrationTeamSpace(TeamSpace teamSpace, long newOwnerId) throws BaseRunException {
		// 用户是否存在
		User newUser = userService.get(newOwnerId);
		if (newUser == null) {
			throw new NoSuchUserException("no such user for newOwnerId, newOwnerId:" + newOwnerId);
		}
		long oldOwnerBy = teamSpace.getOwnerBy();
		if (oldOwnerBy == newOwnerId) {
			// 不需要更改，直接返回
			return teamSpace;
		}

		// 兼容老版本，老版本的ownerBy默认值为-1
		if (oldOwnerBy == User.ANONYMOUS_USER_ID) {
			oldOwnerBy = teamSpace.getCreatedBy();
		}

		Date date = new Date();
		TeamSpaceMemberships oldAdmin = teamSpaceMembershipService.getTeamSpaceMemberByUserNoCheck(teamSpace.getCloudUserId(), String.valueOf(oldOwnerBy), TeamSpaceMemberships.TYPE_USER);
		if (oldAdmin != null) {
			teamSpaceMembershipService.deleteTeamSpaceMemberById(teamSpace.getCloudUserId(), oldAdmin.getId());
		}

		TeamSpaceMemberships newMember = teamSpaceMembershipService.getTeamSpaceMemberByUserNoCheck(teamSpace.getCloudUserId(), String.valueOf(newOwnerId), TeamSpaceMemberships.TYPE_USER);
		if (newMember == null) {
			newMember = new TeamSpaceMemberships();
			// id生成器
			newMember.setId(spaceMemberIdGenerateService.getNextMemberId(teamSpace.getCloudUserId()));
			newMember.setCloudUserId(teamSpace.getCloudUserId());
			newMember.setCreatedAt(date);
			newMember.setCreatedBy(User.APP_USER_ID);
			newMember.setModifiedAt(date);
			newMember.setModifiedBy(User.APP_USER_ID);
			newMember.setStatus(TeamSpaceMemberships.STATUS_NORMAL);
			newMember.setTeamRole(TeamRole.ROLE_ADMIN);
			newMember.setUserId(newOwnerId);
			newMember.setUserType(TeamSpaceMemberships.TYPE_USER);
			newMember.setUsername(newUser.getName());
			newMember.setLoginName(newUser.getLoginName());
			teamSpaceMembershipService.createTeamSpaceMember(newMember);
		} else {
			newMember.setTeamRole(TeamRole.ROLE_ADMIN);
			newMember.setModifiedAt(date);
			newMember.setModifiedBy(User.APP_USER_ID);
			// 修改团队成员记录
			newMember = teamSpaceMembershipService.modifyTeamSpaceMemberRole(newMember);

			// 删除之前的ACL配置
			iNodeACLService.deleteByResourceAndUser(teamSpace.getCloudUserId(), INode.FILES_ROOT, String.valueOf(newOwnerId), INodeACL.TYPE_USER);
		}

		// 直接增加对应author权限
		INodeACL iNodeACL = new INodeACL();
		iNodeACL.setId(nodeACLIdGenerateService.getNextNodeACLId(teamSpace.getCloudUserId()));
		iNodeACL.setAccessUserId(String.valueOf(newOwnerId));
		iNodeACL.setUserType(INodeACL.TYPE_USER);
		iNodeACL.setCreatedAt(date);
		iNodeACL.setCreatedBy(User.APP_USER_ID);
		iNodeACL.setiNodeId(INode.FILES_ROOT);
		iNodeACL.setiNodePid(INode.FILES_ROOT);
		iNodeACL.setOwnedBy(teamSpace.getCloudUserId());
		iNodeACL.setModifiedAt(date);
		iNodeACL.setModifiedBy(User.APP_USER_ID);
		iNodeACL.setResourceRole(ResourceRole.AUTHER);
		try {
			iNodeACLService.addINodeACL(iNodeACL);
		} catch (Exception e) {
			// 跨库异常回滚
			teamSpaceMembershipService.deleteTeamSpaceMemberById(teamSpace.getCloudUserId(), newMember.getId());
			logger.error("changeOwner fail", e);
			throw e;
		}

		teamSpace.setOwnerBy(newOwnerId);
		teamSpace.setModifiedAt(date);
		teamSpace.setModifiedBy(User.APP_USER_ID);

		// 更新团队空间owner字段
		teamSpaceDAO.changeOwner(teamSpace);

		// 发送事件
		createEvent(new UserToken(), EventType.TEAMSPACE_CHANGEOWNER, new INode(teamSpace.getCloudUserId(), INode.FILES_ROOT));

		// 添加空间附加属性：当前成员数，最大配额,已使用配额，该空间创建者姓名
		fillTeamSpaceInfo(teamSpace);

		return teamSpace;
	}
	
	
	
	
	
	@Override
	public TeamSpace checkAndGetTeamSpaceExist(long teamSpaceId) throws BaseRunException {
		TeamSpace teamSpace = teamSpaceDAO.get(teamSpaceId);

		if (teamSpace == null) {
			String msg = "no such teamSpace, teamSpaceId:" + teamSpaceId;
			throw new NoSuchTeamSpaceException(msg);
		}

		if (teamSpace.getStatus() != TeamSpace.STATUS_ENABLE) {
			String msg = "teamSpace is abnormal, teamSpaceId:" + teamSpaceId;
			throw new AbnormalTeamSpaceException(msg);
		}

		return teamSpace;
	}

	@Override
	public TeamSpace createTeamSpace(UserToken user, TeamSpace teamSpace) throws BaseRunException {
		// 创建云盘虚拟账户
		User newUser = new User();
		// user用LoginName作为唯一键，需要做区分
		String nameFilter = System.currentTimeMillis() + "";
		newUser.setAppId(user.getAppId());
		newUser.setAccountId(user.getAccountId());
		newUser.setLoginName(user.getId() + nameFilter);
		newUser.setName(user.getId() + nameFilter);
		newUser.setStatus(User.STATUS_ENABLE_INTEGER);
		newUser.setSpaceQuota(teamSpace.getSpaceQuota());
		newUser.setRegionId(user.getRegionId());
		newUser.setMaxVersions(teamSpace.getMaxVersions());
		newUser.setType(User.USER_TYPE_TEAMSPACE);
		userService.create(newUser);

		// 创建团队空间
		long time = new Date().getTime() / 1000 * 1000;
		Date date = new Date(time);
		teamSpace.setCloudUserId(newUser.getId());
		teamSpace.setAccountId(user.getAccountId());
		teamSpace.setOwnerBy(user.getId());
		teamSpace.setOwnerByUserName(user.getName());
		teamSpace.setCreatedBy(user.getId());
		teamSpace.setCreatedByUserName(user.getName());
		teamSpace.setCreatedAt(date);
		teamSpace.setModifiedBy(user.getId());
		teamSpace.setModifiedAt(date);
		teamSpace.setMaxMembers(teamSpace.getMaxMembers());
		teamSpace.setRegionId(user.getRegionId());

		teamSpaceDAO.create(teamSpace);

		// 创建团队空间属性表相关属性
		teamSpaceAttributeService.addTeamSpaceDefaultAttributes(newUser.getId(), user);
		// 将自身加入成员列表
		TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();

        //只有创建普通空间，才将创建人员加入到空间内
        if(teamSpace.getType() == 0) {
            // id生成器
            teamSpaceMemberships.setId(spaceMemberIdGenerateService.getNextMemberId(newUser.getId()));
            teamSpaceMemberships.setCloudUserId(newUser.getId());
            teamSpaceMemberships.setCreatedAt(date);
            teamSpaceMemberships.setCreatedBy(user.getId());
            teamSpaceMemberships.setModifiedAt(date);
            teamSpaceMemberships.setModifiedBy(user.getId());
            teamSpaceMemberships.setStatus(TeamSpaceMemberships.STATUS_NORMAL);
            teamSpaceMemberships.setTeamRole(TeamRole.ROLE_ADMIN);
            teamSpaceMemberships.setUserId(user.getId());
            teamSpaceMemberships.setUserType(TeamSpaceMemberships.TYPE_USER);
            teamSpaceMemberships.setUsername(user.getName());
            teamSpaceMemberships.setLoginName(user.getLoginName());
            teamSpaceMembershipService.createTeamSpaceMember(teamSpaceMemberships);
        }

		// 直接增加对应auter权限
		INodeACL iNodeACL = new INodeACL();
		iNodeACL.setId(nodeACLIdGenerateService.getNextNodeACLId(newUser.getId()));
		iNodeACL.setAccessUserId(String.valueOf(user.getId()));
		iNodeACL.setUserType(INodeACL.TYPE_USER);
		iNodeACL.setCreatedAt(date);
		iNodeACL.setCreatedBy(user.getId());
		iNodeACL.setiNodeId(INode.FILES_ROOT);
		iNodeACL.setiNodePid(INode.FILES_ROOT);
		iNodeACL.setOwnedBy(newUser.getId());
		iNodeACL.setModifiedAt(date);
		iNodeACL.setModifiedBy(user.getId());
		iNodeACL.setResourceRole(ResourceRole.AUTHER);
		try {
			iNodeACLService.addINodeACL(iNodeACL);
		} catch (Exception e) {
			// 跨库异常回滚
            if(teamSpaceMemberships.getId() > 0) {
                teamSpaceMembershipService.deleteTeamSpaceMemberById(newUser.getId(), teamSpaceMemberships.getId());
            }
			teamSpaceDAO.delete(newUser.getId());
			userService.delete(user.getAccountId(), newUser.getId());
			logger.error("createTeamSpace fail", e);
			throw e;
		}

		// 发送事件
		createEvent(user, EventType.TEAMSPACE_CREATE, new INode(newUser.getId(), INode.FILES_ROOT));
		String[] logMsgs = new String[] { String.valueOf(teamSpace.getCloudUserId()) };
		String keyword = StringUtils.trimToEmpty(teamSpace.getName());
		fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.CREATE_TEAMSPACE, logMsgs,
				keyword);
		return teamSpace;
	}

	/**
	 * 删除团队空间
	 */
	@Override
	public void deleteTeamSpace(UserToken user, long teamSpaceId, String enterpriseId) throws BaseRunException {
		// 检查团队空间是否存在
		TeamSpace teamSpace = teamSpaceDAO.get(teamSpaceId);

		if (teamSpace == null) {
			String msg = "no such teamSpace, teamSpaceId:" + teamSpaceId;
			throw new NoSuchTeamSpaceException(msg);
		}

		// 团队空间的拥有者以及应用管理员可以更新团队空间信息
		checkACL(user, teamSpaceId, enterpriseId);

		// 删除所有团队关系，团队成员权限以及，对应资源, 先删除权限，再删除成员
		iNodeACLService.deleteSpaceAllACLs(teamSpaceId);
		teamSpaceMembershipService.deleteAllTeamSpaceMember(teamSpaceId);
		teamSpaceDAO.delete(teamSpaceId);

		// 删除虚拟用户
		userService.delete(user.getAccountId(), teamSpaceId);

		nodeUpdateService.updateAllINodeStatus(teamSpaceId, INode.STATUS_DELETE);

		spaceMemberIdGenerateService.delete(teamSpaceId);
		nodeACLIdGenerateService.delete(teamSpaceId);

		// 发送事件
		if(user.getId()!=User.APP_USER_ID){
			createEvent(user, EventType.TEAMSPACE_DELETE, new INode(teamSpaceId, INode.FILES_ROOT));
			String[] logMsgs = new String[] { String.valueOf(teamSpaceId) };
			String keyword = StringUtils.trimToEmpty(teamSpace.getName());

			fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.DELETE_TEAMSPACE, logMsgs,keyword);
		}
		
	}

	@Override
	public List<TeamSpaceAttribute> getTeamSpaceAttrs(User user, String name, long teamSpaceId, String enterpriseId)
			throws BaseRunException {
		// TODO 暂无扩展属性数据库表
		TeamSpace teamSpace = checkAndGetTeamSpaceExist(teamSpaceId);

		// 操作权限校验
		checkACL(user, teamSpaceId, enterpriseId);

		List<TeamSpaceAttribute> attrList = new ArrayList<TeamSpaceAttribute>(BusinessConstants.INITIAL_CAPACITIES);

		String uploadNotice = teamSpace.getUploadNotice() == TeamSpace.UPLOAD_NOTICE_ENABLE
				? TeamSpaceAttribute.UPLOAD_NOTICE_ENABLE : TeamSpaceAttribute.UPLOAD_NOTICE_DISABLE;
		TeamSpaceAttribute uploadAttr = new TeamSpaceAttribute(TeamSpaceAttributeEnum.UPLOAD_NOTICE.getName(),
				uploadNotice);

		// 查询所有扩展属性
		if (StringUtils.isBlank(name)) {
			attrList.add(uploadAttr);
			return attrList;
		}
		TeamSpaceAttributeEnum item = TeamSpaceAttributeEnum.getTeamSpaceConfig(name);
		if (item == null) {
			return attrList;
		}
		if (TeamSpaceAttributeEnum.UPLOAD_NOTICE == item) {
			attrList.add(uploadAttr);
		}
		return attrList;
	}

	@Override
	public TeamSpace getTeamSpaceInfo(UserToken user, long teamSpaceId) throws BaseRunException {
		TeamSpace teamSpace = teamSpaceDAO.get(teamSpaceId);

		if (teamSpace == null) {
			throw new NoSuchTeamSpaceException();
		}

		// 添加空间附加属性：当前成员数，最大配额,已使用配额，该空间创建者姓名
		fillTeamSpaceInfo(teamSpace);
		String[] logMsgs = new String[] { String.valueOf(teamSpaceId) };
		String keyword = StringUtils.trimToEmpty(teamSpace.getName());
        if(user.getId()!=User.APP_USER_ID){
        	fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.GET_TEAMSPACE, logMsgs, keyword);
        }else{
        	user.setAccountId(teamSpace.getAccountId());
        	user.setCloudUserId(teamSpace.getCloudUserId());
        }
		return teamSpace; 
	}

	@Override
	public TeamSpace getTeamSpaceNoCheck(long teamSpaceId) {
		return teamSpaceDAO.get(teamSpaceId);
	}

	@Override
	public TeamSpaceList listAllTeamSpaces(List<Order> orderList, Limit limit, TeamSpace filter)
			throws BaseRunException {
		TeamSpaceList teamSpaceList = new TeamSpaceList();
		teamSpaceList.setTotalCount(teamSpaceDAO.getTeamSpaceCount(filter));
		teamSpaceList.setLimit(limit.getLength());
		teamSpaceList.setOffset(limit.getOffset());

		// 如果order为空，则设置默认排序规则
		if (CollectionUtils.isEmpty(orderList)) {
			orderList = getDefaultOrderList();
		}
		List<Order> oldOrderList = null;

		for (Order order : orderList) {
			if ("spaceUsed".equals(order.getField())) {
				oldOrderList = orderList;
				orderList = getDefaultOrderList();
			}
		}
		List<TeamSpace> itemList = teamSpaceDAO.listTeamSpaces(orderList, limit, filter);
		List<TeamSpace> toRemoveList = new ArrayList<TeamSpace>(1);
		for (TeamSpace tempTeamspace : itemList) {
			fillTeamspace(toRemoveList, tempTeamspace);
		}
		if (!CollectionUtils.isEmpty(toRemoveList)) {
			itemList.removeAll(toRemoveList);
		}

		if (CollectionUtils.isNotEmpty(oldOrderList)) {
			orderHandleSpaceUsed(oldOrderList, itemList);
		}
		teamSpaceList.setTeamSpaceList(itemList);
		return teamSpaceList;
	}

	private void fillTeamspace(List<TeamSpace> toRemoveList, TeamSpace tempTeamspace) {
		try {
			fillTeamSpaceInfo(tempTeamspace);
		} catch (NoSuchUserException e) {
			toRemoveList.add(tempTeamspace);
		}
	}

	private void orderHandleSpaceUsed(List<Order> oldOrderList, List<TeamSpace> itemList) {
		for (Order order : oldOrderList) {
			if ("spaceUsed".equals(order.getField())) {
				if ("ASC".equalsIgnoreCase(order.getDirection())) {
					Collections.sort(itemList, new TeamUsedSpaceAscComparator());
				} else if ("DESC".equalsIgnoreCase(order.getDirection())) {
					Collections.sort(itemList, new TeamUsedSpaceDescComparator());
				}
			}
		}
	}

	@Override
	public TeamMemberList listUserTeamSpaces(UserToken user, List<Order> orderList, Limit limit, long userId,
											 int type, String userType) throws BaseRunException {
		TeamMemberList teamMemberList = new TeamMemberList();

		// 暂时只允许列举自己的团队空间(ID<0, 表示系统用户查询，允许）
		if (userId != user.getId() && user.getId() > 0) {
			String excepMessage = "not alowed to list other teamSpaces :" + userId;
			throw new ForbiddenException(excepMessage);
		}

		// 设置分页属性
		teamMemberList.setLimit(limit.getLength());
		teamMemberList.setOffset(limit.getOffset());

		// 通过分页获取空间列表
		TeamMemberList membershipsList = teamSpaceMembershipService.listUserTeamSpaceMemberships(user,
				String.valueOf(userId), type, userType, orderList, limit);

		List<TeamSpaceMemberships> itemList = new ArrayList<TeamSpaceMemberships>(BusinessConstants.INITIAL_CAPACITIES);

		TeamSpace teamSpace = null;
		INodeACL nodeACL = null;

		for (TeamSpaceMemberships memberships : membershipsList.getTeamMemberList()) {
			teamSpace = teamSpaceDAO.getByType(memberships.getCloudUserId(),type);
			if (teamSpace == null) {
				continue;
			}

			// 添加空间附加属性：当前成员数，最大配额,已使用配额，该空间创建者姓名
			fillTeamSpaceInfo(teamSpace);
			memberships.setMember(user);
			memberships.setTeamSpace(teamSpace);

			nodeACL = iNodeACLService.getByResourceAndUser(memberships.getCloudUserId(), INode.FILES_ROOT, String.valueOf(memberships.getUserId()), memberships.getUserType());
			memberships.setRole(nodeACL != null ? nodeACL.getResourceRole() : null);

			itemList.add(memberships);
		}
		teamMemberList.setTotalCount(membershipsList.getTotalCount());
		teamMemberList.setTeamMemberList(itemList);
		String[] logMsgs = new String[] { String.valueOf(userId), null };
		String keyword = "TotalCount:" + teamMemberList.getTotalCount();

		fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.LIST_USER_TESMSPACE, logMsgs,
				keyword);
		return teamMemberList;
	}

	@Override
	public TeamSpace modifyTeamSpace(UserToken userToken, Long teamSpaceId, RestTeamSpaceModifyRequest modifyRequest)
			throws BaseRunException {
		String name = modifyRequest.getName();
		String description = modifyRequest.getDescription();
		Long spaceQuota = modifyRequest.getSpaceQuota();
		Integer status = modifyRequest.getStatus();
		Integer maxMembers = modifyRequest.getMaxMembers();
		Integer maxVersions = modifyRequest.getMaxVersions();
		Byte regionId = modifyRequest.getRegionId();

		TeamSpace teamSpace = teamSpaceDAO.get(teamSpaceId);

		// 不进行状态校验，因为接口实现了状态的更新
		if (teamSpace == null) {
			throw new NoSuchTeamSpaceException("no such teamSpace, cloudUserID:" + teamSpaceId);
		}

		// 团队空间的拥有者以及应用管理员可以更新团队空间信息
		checkModifyRight(userToken, teamSpaceId, teamSpace);
		// 设置团队空间名
		if (StringUtils.isNotBlank(name)) {
			teamSpace.setName(name);
		}

		// 设置团队空间描述, description如果为空字符串也可以更新成功
		if (description != null) {
			teamSpace.setDescription(description);
		}

		if (maxMembers != null) {
			teamSpace.setMaxMembers(maxMembers);
		}

		// 如果状态发生变化，才进行设置
		if (status != null && status != teamSpace.getStatus()) {
			teamSpace.setStatus(status);
		}

		// 如果空间配额发生变化，才进行设置
		modifyTeamspaceExtInfo(teamSpaceId, spaceQuota, maxVersions, regionId, status);

		long time = new Date().getTime() / 1000 * 1000;
		Date date = new Date(time);
		teamSpace.setModifiedAt(date);
		teamSpace.setModifiedBy(userToken.getId());
		teamSpaceDAO.update(teamSpace);

		// 添加空间附加属性：当前成员数，最大配额,已使用配额，该空间创建者姓名
		fillTeamSpaceInfo(teamSpace);

		// 发送事件
		createEvent(userToken, EventType.TEAMSPACE_UPDATE, new INode(teamSpaceId, INode.FILES_ROOT));
		String[] logMsgs = new String[] { String.valueOf(teamSpaceId) };
		String keyword = StringUtils.trimToEmpty(teamSpace.getName());
		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.MODIFY_TEAMSPACE, logMsgs,
				keyword);
		return teamSpace;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void setTeamSpaceAttr(User user, TeamSpaceAttribute attribute, long teamSpaceId, String enterpriseId)
			throws BaseRunException {
		// 检查团队空间是否存在
		TeamSpace teamSpace = checkAndGetTeamSpaceExist(teamSpaceId);
		// 操作权限校验
		checkACL(user, teamSpaceId, enterpriseId);
		TeamSpaceAttributeEnum attributeEnum = TeamSpaceAttributeEnum.getTeamSpaceConfig(attribute.getName());
		if (attributeEnum == null) {
			logger.error("ufm.setTeamSpaceAttr failed: invalid attribute.");
			throw new InvalidParamException("Invalid attribute: " + attribute.getName());
		}

		//上传通知属性
		if (TeamSpaceAttributeEnum.UPLOAD_NOTICE == attributeEnum) {
			// TODO 暂无扩展属性数据库表
			byte uploadNotice = TeamSpaceAttribute.UPLOAD_NOTICE_ENABLE.equals(attribute.getValue())? TeamSpace.UPLOAD_NOTICE_ENABLE : TeamSpace.UPLOAD_NOTICE_DISABLE;
			teamSpaceDAO.updateUploadNotice(teamSpaceId, uploadNotice);
			logger.info("ufm.setTeamSpaceAttr end.");
		} else if (TeamSpaceAttributeEnum.AUTO_PREVIEW == attributeEnum) {
			//文件预览自动转换
        	String autoPreview = TeamSpaceAttribute.AUTO_PREVIEW_ENABLE.equals(attribute.getValue()) ? TeamSpaceAttribute.AUTO_PREVIEW_ENABLE_VALUE : TeamSpaceAttribute.AUTO_PREVIEW_DISABLE_VALUE;
        	TeamSpaceAttribute teamSpaceAttribute = new TeamSpaceAttribute(teamSpaceId, attribute.getName(), autoPreview);
        	teamSpaceAttributeService.setTeamSpaceAttribute(teamSpaceAttribute, (UserToken)user);
        	// 开启自动预览时调用转换接口完成对空间文件的转换
        	if(TeamSpaceAttribute.AUTO_PREVIEW_ENABLE.equals(attribute.getValue())){
				// 进行文件转换（cloudUserId用来惟一确定某一个团队空间，然后对团队空间下的文件进行处理）
        		long cloudUserId = teamSpace.getCloudUserId();
	        	doFileConvert4TeamSpace(cloudUserId);
        	}
        	logger.info("ufm.setTeamSpaceAttr end.");
        } else {
			logger.error("ufm.setTeamSpaceAttr failed: invalid attribute.");
			throw new InvalidParamException("Invalid attribute: " + attribute.getName());
		}
	}

	@Override
	public TeamSpace updateOwner(UserToken userToken, long teamSpaceId, long ownerBy) throws BaseRunException {
		TeamSpace teamSpace = teamSpaceDAO.get(teamSpaceId);

		if (teamSpace == null) {
			String msg = "no such teamSpace, teamSpaceId:" + teamSpaceId;
			throw new NoSuchTeamSpaceException(msg);
		}

		Date date = new Date();
		teamSpace.setOwnerBy(ownerBy);
		teamSpace.setModifiedAt(date);
		if (userToken != null) {
			teamSpace.setModifiedBy(userToken.getId());
		}

		teamSpaceDAO.changeOwner(teamSpace);

		return teamSpace;
	}

	@Override
	public void updateTeamSpacesForUserDelete(long accountId, long userId) throws BaseRunException {
		Limit userLimit = null;
		Limit memberLimit = new Limit(0L, 100);
		long offset = 0;
		TeamMemberList membershipsList = null;

		while (true) {
			// 批量更新节点的状态
			userLimit = new Limit(offset, 100);
			membershipsList = teamSpaceMembershipService.listUserTeamSpaceMemberships(String.valueOf(userId),
					TeamSpaceMemberships.TYPE_USER, null, userLimit);

			if (membershipsList.getTotalCount() == 0) {
				return;
			}

			for (TeamSpaceMemberships item : membershipsList.getTeamMemberList()) {
				changeOwnerForOne(accountId, userId, memberLimit, item);
			}

			offset = offset + 100;
		}

	}

	private void changeOwnerForOne(long accountId, long userId, Limit memberLimit, TeamSpaceMemberships item) {
		// TODO 加入系统用户后存在问题,必须在删除用户前预先做判断
		TeamMemberList memberList = null;
		TeamSpaceMemberships newAdmin = null;

		INodeACL iNodeACL = null;
		boolean isAddAcl = false;
		String oldRole = null;
		TeamSpace teamSpace = null;

		Long teamId = item.getCloudUserId();

		if (teamSpaceMembershipService.getTeamSpaceMembersCount(teamId) == 1) {
			// 删除所有团队关系，团队成员权限以及，对应资源, 先删除权限，再删除成员
			iNodeACLService.deleteSpaceAllACLs(teamId);
			teamSpaceMembershipService.deleteAllTeamSpaceMember(teamId);
			teamSpaceDAO.delete(teamId);

			// 删除虚拟用户
			userService.delete(accountId, teamId);

			nodeUpdateService.updateAllINodeStatus(teamId, INode.STATUS_DELETE);

			spaceMemberIdGenerateService.delete(teamId);
			nodeACLIdGenerateService.delete(teamId);
			return;
		}

		if (!TeamRole.ROLE_ADMIN.equals(item.getTeamRole())) {
			// 获取的团队空间关系有可能是系统成员或群组，不能删除该关系
			if (TeamSpaceMemberships.TYPE_USER.equals(item.getUserType())) {
				// 删除所有团队关系，先删除权限，再删除成员
				iNodeACLService.deleteSpaceACLsByUser(teamId, String.valueOf(userId), INodeACL.TYPE_USER);
				teamSpaceMembershipService.deleteTeamSpaceMemberById(teamId, item.getId());
			}

			return;
		}

		memberList = teamSpaceMembershipService.listTeamSpaceMemberships(teamId, null, memberLimit,
				TeamRole.ROLE_MANAGER, null);

		long time = new Date().getTime() / 1000 * 1000;
		Date date = new Date(time);

		// 如果有管理员则变更拥有者，管理员用户类型只能是User
		if (memberList.getTotalCount() > 0) {
			newAdmin = memberList.getTeamMemberList().get(0);
		} else {
			memberList = teamSpaceMembershipService.listTeamSpaceMemberships(teamId, null, memberLimit,
					TeamRole.ROLE_MEMBER, null);

			for (TeamSpaceMemberships item1 : memberList.getTeamMemberList()) {
				if (TeamSpaceMemberships.TYPE_USER.equals(item.getUserType())) {
					newAdmin = item1;
					break;
				}
			}
			if (newAdmin == null) {
				String excepMessage = "no user to change owner, teamId:" + teamId;
				throw new ForbiddenException(excepMessage);
			}
			iNodeACL = iNodeACLService.getByResourceAndUser(teamId, INode.FILES_ROOT, String.valueOf(newAdmin.getUserId()), newAdmin.getUserType());

			if (iNodeACL == null) {
				isAddAcl = true;
				iNodeACL = new INodeACL();
				iNodeACL.setId(nodeACLIdGenerateService.getNextNodeACLId(teamId));
				iNodeACL.setAccessUserId(String.valueOf(newAdmin.getUserId()));
				iNodeACL.setUserType(INodeACL.TYPE_USER);
				iNodeACL.setCreatedAt(date);
				iNodeACL.setCreatedBy(User.APP_USER_ID);
				iNodeACL.setiNodeId(INode.FILES_ROOT);
				iNodeACL.setiNodePid(INode.FILES_ROOT);
				iNodeACL.setOwnedBy(teamId);
				iNodeACL.setModifiedAt(date);
				iNodeACL.setModifiedBy(User.APP_USER_ID);
				iNodeACL.setResourceRole(ResourceRole.AUTHER);
				iNodeACLService.addINodeACL(iNodeACL);
			} else {
				oldRole = iNodeACL.getResourceRole();
				iNodeACL.setModifiedAt(date);
				iNodeACL.setModifiedBy(User.APP_USER_ID);
				iNodeACL.setResourceRole(ResourceRole.AUTHER);
				iNodeACLService.modifyINodeACLById(iNodeACL);
			}

		}

		// 删除所有团队关系，先删除权限，再删除成员
		iNodeACLService.deleteSpaceACLsByUser(teamId, String.valueOf(userId), INodeACL.TYPE_USER);
		try {
			teamSpaceMembershipService.deleteTeamSpaceMemberById(teamId, item.getId());
		} catch (Exception e) {
			rollbackForChangeOwner(teamId, date, iNodeACL, isAddAcl, oldRole);
			logger.error("changeOwner fail", e);
			throw new InternalServerErrorException(e);
		}

		newAdmin.setTeamRole(TeamRole.ROLE_ADMIN);
		newAdmin.setModifiedAt(date);
		newAdmin.setModifiedBy(User.APP_USER_ID);
		// 修改团队成员记录
		newAdmin = teamSpaceMembershipService.modifyTeamSpaceMemberRole(newAdmin);

		teamSpace = teamSpaceDAO.get(teamId);
		if (teamSpace != null) {
			teamSpace.setOwnerBy(newAdmin.getUserId());
			teamSpace.setModifiedAt(date);
			teamSpace.setModifiedBy(User.APP_USER_ID);
			// 更新团队空间owner字段
			teamSpaceDAO.changeOwner(teamSpace);
		}
	}

	private void checkACL(User user, long teamSpaceId, String enterpriseId) {
		// 团队空间的拥有者以及应用管理员可以更新团队空间信息
		if (user.getId() != User.APP_USER_ID) {
			TeamSpaceMemberships teamSpaceMembler = teamSpaceMembershipService.getUserMemberShips(teamSpaceId,
					user.getId(), enterpriseId);

			if (teamSpaceMembler == null) {
				logger.error("User {} is not the member of the teamspace {}", user.getId(), teamSpaceId);
				throw new ForbiddenException("User not the member of teamspace");
			}

			if (!TeamRole.ROLE_ADMIN.equals(teamSpaceMembler.getTeamRole())) {
				String excepMessage = "Operation is not allowed , team Role:" + teamSpaceMembler.getTeamRole();
				throw new ForbiddenException(excepMessage);
			}
		}
	}

	private void checkModifyRight(UserToken userToken, Long teamSpaceId, TeamSpace teamSpace) {
		if (userToken.getId() != UserToken.APP_USER_ID) {
			String enterpriseId = "";
			if (userToken.getAccountVistor() != null) {
				enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
			}
			TeamSpaceMemberships teamSpaceMembler = teamSpaceMembershipService.getUserMemberShips(teamSpaceId,
					userToken.getId(), enterpriseId);

			if (teamSpaceMembler == null) {
				throw new ForbiddenException("not the member");
			}

			if (!TeamRole.ROLE_ADMIN.equals(teamSpaceMembler.getTeamRole())) {
				String excepMessage = "Not allowed to modifyTeamSpace , team Role:" + teamSpaceMembler.getTeamRole();
				throw new ForbiddenException(excepMessage);
			}
		} else {
			if (teamSpace.getAccountId() != userToken.getAccountId()) {
				String excepMessage = "APP manager not allowed to modifyTeamSpace";
				throw new ForbiddenException(excepMessage);
			}
		}

	}

	private void createEvent(UserToken userToken, EventType type, INode srcNode) {
		LogEvent.createEvent(userToken, type, srcNode, eventService, logger);
	}

	/**
	 * 添加团队空间附加信息
	 * 
	 * @param teamSpaceInfo
	 * @throws BaseRunException
	 */
	private void fillTeamSpaceInfo(TeamSpace teamSpaceInfo) throws BaseRunException {
		User teamSpaceUser;
		User ownerUser;
		User createUser;
		// 设置当前的成员数
		teamSpaceInfo.setCurNumbers(teamSpaceMembershipService.getTeamSpaceMembersCount(teamSpaceInfo.getCloudUserId()));
		// 设置最大版本数
		teamSpaceUser = userService.get(teamSpaceInfo.getCloudUserId());
		if (teamSpaceUser == null) {
			String excepMessage = "no such teamSpace user, cloudUserID:" + teamSpaceInfo.getCloudUserId();
			throw new NoSuchUserException(excepMessage);
		}
		teamSpaceInfo.setMaxVersions(teamSpaceUser.getMaxVersions());
		UserStatisticsInfo userInfo = spaceStatisticsService.getUserCurrentInfo(teamSpaceInfo.getCloudUserId(), teamSpaceUser.getAccountId());
		teamSpaceInfo.setSpaceUsed(userInfo.getSpaceUsed());
		teamSpaceInfo.setSpaceQuota(teamSpaceUser.getSpaceQuota());

		teamSpaceInfo.setRegionId(teamSpaceUser.getRegionId());

		// 设置创建者名称
		if(teamSpaceInfo.getCreatedBy()!=-2){
			createUser = userService.get(teamSpaceInfo.getCreatedBy());
			if (createUser == null) {
				String excepMessage = "no such user for create this teamSpace, userId:" + teamSpaceInfo.getCreatedBy();
				logger.error(excepMessage);
			} else {
				teamSpaceInfo.setCreatedByUserName(createUser.getName());
			}
		}


		// 设置拥有者名称
		if(teamSpaceInfo.getOwnerBy()!=-2){
			ownerUser = userService.get(teamSpaceInfo.getOwnerBy());
			if (ownerUser == null) {
				String excepMessage = "no such user for own this teamSpace, userId:" + teamSpaceInfo.getOwnerBy();
				logger.error(excepMessage);
			} else {
				teamSpaceInfo.setOwnerByUserName(ownerUser.getName());
			}
		}
	}

	private List<Order> getDefaultOrderList() {
		List<Order> orderList = new ArrayList<Order>(2);
		// 默认按照时间降序排列
		orderList.add(new Order("name", "asc"));
		orderList.add(new Order("createdAt", "desc"));
		return orderList;
	}

	private void modifyTeamspaceExtInfo(Long teamSpaceId, Long spaceQuota, Integer maxVersions, Byte regionId,
			Integer status) {
		User userInfo = userService.get(teamSpaceId);
		if (userInfo == null) {
			String msg = "no such teamSpace, cloudUserID:" + teamSpaceId;
			throw new NoSuchTeamSpaceException(msg);
		}

		boolean isChange = false;
		if (spaceQuota != null && userInfo.getSpaceQuota() != spaceQuota) {
			userInfo.setSpaceQuota(spaceQuota);
			isChange = true;
		}

		if (maxVersions != null && userInfo.getMaxVersions() != maxVersions) {
			userInfo.setMaxVersions(maxVersions);
			isChange = true;
		}

		if (regionId != null && userInfo.getRegionId() != regionId) {
			userInfo.setRegionId(regionId);
			isChange = true;
		}

		if (status != null && !String.valueOf(status).equals(userInfo.getStatus())) {
			userInfo.setStatus(User.STATUS_DISABLE_INTEGER);
			if (status == TeamSpace.STATUS_ENABLE) {
				userInfo.setStatus(User.STATUS_ENABLE_INTEGER);
			}
			isChange = true;
		}

		if (isChange) {
			userService.update(userInfo);
		}
	}

	private void rollbackForChangeOwner(Long teamId, Date date, INodeACL iNodeACL, boolean isAddAcl, String oldRole) {
		if (iNodeACL != null) {
			if (isAddAcl) {
				iNodeACLService.deleteINodeACLById(teamId, iNodeACL.getId());
			} else {
				iNodeACL.setModifiedAt(date);
				iNodeACL.setModifiedBy(User.APP_USER_ID);
				iNodeACL.setResourceRole(oldRole);
				iNodeACLService.modifyINodeACLById(iNodeACL);
			}
		}
	}

	@Override
	public TeamSpace initTeamspace(RestTeamSpaceCreateRequest createRequest, UserToken userInfo) {
		TeamSpace teamSpace = new TeamSpace();
		teamSpace.setName(createRequest.getName());
		teamSpace.setDescription(createRequest.getDescription());
		teamSpace.setStatus(createRequest.getStatus());
		teamSpace.setSpaceQuota(createRequest.getSpaceQuota());
		teamSpace.setMaxMembers(createRequest.getMaxMembers());
		teamSpace.setMaxVersions(createRequest.getMaxVersions());
		teamSpace.setCurNumbers(1);
		teamSpace.setAppId(userInfo.getAppId());
        teamSpace.setType(createRequest.getType());

		return teamSpace;
	}

	@Override
	public boolean isTeamSpace(long teamSpaceId) {
		return teamSpaceDAO.get(teamSpaceId) != null;
	}

	@Override
	public TeamSpace getTeamSpaceByName(UserToken userToken, String name) {
		TeamSpace teamSpace = teamSpaceDAO.getByName(name,userToken.getAccountId());
		String[] logMsgs = new String[] { String.valueOf(name) };
		String keyword = StringUtils.trimToEmpty(name);
		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_TEAMSPACE, logMsgs,
				keyword);
		return teamSpace;
	}
	
	private void doFileConvert4TeamSpace(long ownerId) {
		// 对ownerId判空
		
		// //文件夹集合
		// List<INode> folderList = new
		// ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
		// //文件集合
		// List<INode> fileList = new
		// ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
		Long folderId = INode.FILES_ROOT;
		INode node = new INode(ownerId, folderId);
		node.setStatus(INode.STATUS_NORMAL);
		List<Order> orderList = new ArrayList<Order>();
		int total = iNodeDAOV2.getSubINodeCount(node, true);
		List<INode> nodeList = iNodeDAOV2.getINodeByParentAndStatus(node, orderList, 0, total, true);
		String[] array = {"doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf"};
		List<String> slist = Arrays.asList(array);
		recursiveGetAllFiles(nodeList, slist);
	}
	
	// 通过递归查询团队空间下的所有文件
	private void recursiveGetAllFiles(List<INode> nodeList, List<String> slist) {
		String filetype = null;
		
		for (INode temp : nodeList) {
			if (temp.getType() == INode.TYPE_FILE) {
				filetype = temp.getName().substring(temp.getName().lastIndexOf(".") + 1, temp.getName().length());
				if (slist.contains(filetype.toLowerCase())) {
					send2AddTask(temp);
				}
				
			} else if (temp.getType() == INode.TYPE_FOLDER) {
				Long fid = temp.getId();
				INode node = new INode(temp.getOwnedBy(), fid);
				node.setStatus(INode.STATUS_NORMAL);
				List<Order> orderList = new ArrayList<Order>();
				int total = iNodeDAOV2.getSubINodeCount(node, true);
				List<INode> nodeList1 = iNodeDAOV2.getINodeByParentAndStatus(node, orderList, 0, total, true);
				recursiveGetAllFiles(nodeList1, slist);
			}
		}
		
	}
	
	private void send2AddTask(INode node) {
		long ownerId = node.getOwnedBy();
		logger.info("enter method send2AddTask ,the parameter ownerid is " + ownerId + "------------inode :" + node.toString());
		String objectId = node.getObjectId();

		TaskBean tb = new TaskBean();
		tb.setObjectId(objectId);
		tb.setOwneId(String.valueOf(ownerId));

		filePreviewManager.startConvertTask(node);
		logger.debug("start the convert task of file " + node.getName());
	}






	@Override
	public TeamMemberList listTeamSpacesByOwner(UserToken user, List<Order> orderList, Limit limit, Long userId, int type, String userType) {
		TeamMemberList teamMemberList = new TeamMemberList();

		// 暂时只允许列举自己的团队空间(ID<0, 表示系统用户查询，允许）
		if (userId != user.getId() && user.getId() > 0) {
			String excepMessage = "not alowed to list other teamSpaces :" + userId;
			throw new ForbiddenException(excepMessage);
		}

		// 设置分页属性
		teamMemberList.setLimit(limit.getLength());
		teamMemberList.setOffset(limit.getOffset());

		// 通过分页获取空间列表
		TeamMemberList membershipsList = teamSpaceMembershipService.listTeamSpacesByOwner(user,
				String.valueOf(userId), type, userType, orderList, limit);

		List<TeamSpaceMemberships> itemList = new ArrayList<TeamSpaceMemberships>(BusinessConstants.INITIAL_CAPACITIES);

		TeamSpace teamSpace = null;
		INodeACL nodeACL = null;

		for (TeamSpaceMemberships memberships : membershipsList.getTeamMemberList()) {
			teamSpace = teamSpaceDAO.getByType(memberships.getCloudUserId(),type);
			if (teamSpace == null) {
				continue;
			}

			// 添加空间附加属性：当前成员数，最大配额,已使用配额，该空间创建者姓名
			fillTeamSpaceInfo(teamSpace);
			memberships.setMember(user);
			memberships.setTeamSpace(teamSpace);
			nodeACL = iNodeACLService.getByResourceAndUser(memberships.getCloudUserId(), INode.FILES_ROOT, String.valueOf(memberships.getUserId()), memberships.getUserType());
			memberships.setRole(nodeACL != null ? nodeACL.getResourceRole() : null);
			itemList.add(memberships);
		}
		teamMemberList.setTotalCount(membershipsList.getTotalCount());
		teamMemberList.setTeamMemberList(itemList);
		String[] logMsgs = new String[] { String.valueOf(userId), null };
		String keyword = "TotalCount:" + teamMemberList.getTotalCount();

		fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.LIST_USER_TESMSPACE, logMsgs,
				keyword);
		return teamMemberList;
	}
}
