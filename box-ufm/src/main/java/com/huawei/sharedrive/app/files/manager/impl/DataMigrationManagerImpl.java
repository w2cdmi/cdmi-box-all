package com.huawei.sharedrive.app.files.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.acl.service.INodeACLIdGenerateService;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.dto.DataMigrationRequestDto;
import com.huawei.sharedrive.app.files.dto.MigrationRecordDto;
import com.huawei.sharedrive.app.files.exception.MigrationException;
import com.huawei.sharedrive.app.files.manager.IDataMigrationManager;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.TrashServiceV2;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.manager.GroupMembershipsManager;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.group.RestAddGroupRequest;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroupMember;
import com.huawei.sharedrive.app.openapi.domain.node.CreateFolderRequest;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMember;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMemberCreateRequest;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceMembershipsDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamRole;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipManager;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.dao.UserReverseDAO;
import com.huawei.sharedrive.app.user.domain.GroupInfo;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.GroupMemberService;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.ufm.tools.Doctype.Doctypes;

/**
 * 数据迁移服务接口实现类
 * @author 77235
 *
 */
@Service("dataMigrationManager")
public class DataMigrationManagerImpl implements IDataMigrationManager {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(DataMigrationManagerImpl.class);
    
    @Autowired
    private UserReverseDAO userReverseDAO;
    
    @Autowired
    private UserDAOV2 userDAOV2;
    
    @Autowired
    private INodeDAO inodeDao;
    
    @Autowired
    private TeamSpaceMembershipsDAO teamSpaceMembershipsDAO;
    
    @Autowired
    private TeamSpaceMembershipManager teamSpaceMembershipManager;
    
    @Autowired
    private GroupMemberService groupMemberService;
    
    @Autowired
    private GroupMembershipsManager groupMembershipManager;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private TrashServiceV2 trashServiceV2;
    
    @Autowired
    private INodeACLService iNodeACLService;
    
    @Autowired
    private INodeACLIdGenerateService nodeACLIdGenerateService;
    
    @Override
    public User getAccountUserByAccountAndUserId(Long accountId, Long cloudUserId) {
    	User user =  userReverseDAO.getBycloudUserId(accountId, cloudUserId);
    	
    	return user;
    }
    
    public User getUserByCloudUserId(Long cloudUserId){
        User user = userDAOV2.get(cloudUserId);
        
        return user;
    }
    
	@Override
	public void updateAccountUser(Long accountId, Long cloudUserId, DataMigrationRequestDto migrationRequest) {
		User accountUser = userReverseDAO.getBycloudUserId(accountId, cloudUserId);
		if (null == accountUser){
			throw new InvalidParamException();
		}
		
		accountUser.setLoginName(migrationRequest.getName());
		accountUser.setEmail(migrationRequest.getEmail());
		accountUser.setName(migrationRequest.getAlias());

		userReverseDAO.updateBaseInfo(accountUser);
	}
    
	public void updateUser(Long cloudUserId, DataMigrationRequestDto migrationRequest) {
		User user = userDAOV2.get(cloudUserId);
		if (null == user) {
			throw new InvalidParamException();
		}

		user.setLoginName(migrationRequest.getName());
		user.setEmail(migrationRequest.getEmail());
		user.setName(migrationRequest.getAlias());

		userDAOV2.update(user);
	}
	
	/**
	 * 迁移团队空间
	 * @param userToken
	 * @param migrationRequest
	 */
	private void migrateTeamspace(UserToken userToken, DataMigrationRequestDto migrationRequest) {
		final String emptyString = "";
		Limit limitObj = new Limit(0l, 1000);

		// 1、得到离职人员所在的团队空间编号信息
		List<TeamSpaceMemberships> departurnUserSpaceMemberships = teamSpaceMembershipsDAO.listUserTeamSpaceMemberships(
				migrationRequest.getDepartureCloudUserId() + emptyString, TeamSpace.TYPE_PERSONAL, emptyString, null, limitObj);
		// 2、得到接收人员所在的团队空间编号信息
		List<TeamSpaceMemberships> recipientUserSpaceMemberships = teamSpaceMembershipsDAO.listUserTeamSpaceMemberships(
				migrationRequest.getRecipientCloudUserId() + emptyString, TeamSpace.TYPE_PERSONAL, emptyString, null, limitObj);

		// 3、循环离职人员团队空间
		boolean isAccountOper = false;
		// 设置操作者编号
		userToken.setId(migrationRequest.getDepartureCloudUserId());
		if (null != departurnUserSpaceMemberships) {
			for (TeamSpaceMemberships departureMemberShip : departurnUserSpaceMemberships) {
				try {
					long teamspaceId = departureMemberShip.getCloudUserId(); // 团队空间编号

					if (StringUtils.equalsIgnoreCase(departureMemberShip.getTeamRole(), TeamRole.ROLE_ADMIN)) { // 离职用户为空间拥有者
						boolean IsRecipientUserInTeamspace = false;
						long recipientMemberId = -1;

						if (null != recipientUserSpaceMemberships) {
							for (TeamSpaceMemberships recipientMemberShip : recipientUserSpaceMemberships) {
								if (departureMemberShip.getCloudUserId() == recipientMemberShip.getCloudUserId()) {
									recipientMemberId = recipientMemberShip.getId();
									IsRecipientUserInTeamspace = true;
									break;
								}
							}
						}

						userToken.setId(migrationRequest.getDepartureCloudUserId());
						if (!IsRecipientUserInTeamspace) { // 接受用户在团队空间,将接受者添加进团队空间
							RestTeamMemberCreateRequest restTeamMemberCreateRequest = new RestTeamMemberCreateRequest();
							restTeamMemberCreateRequest.setRole(ResourceRole.EDITER);
							restTeamMemberCreateRequest.setTeamRole(TeamRole.ROLE_MANAGER);

							RestTeamMember restMember = new RestTeamMember();
							restMember.setId(migrationRequest.getRecipientCloudUserId() + ""); // 虚拟用户编号
							restMember.setType(INodeACL.TYPE_USER);

							List<RestTeamMember> memberList = new ArrayList<>();
							memberList.add(restMember);
							restTeamMemberCreateRequest.setMemberList(memberList);
//							UserToken userToken, long teamSpaceId, boolean isAccountOper, String userId, String userType, String teamRole, String role
							TeamSpaceMemberships createdmemberShip = teamSpaceMembershipManager.createTeamSpaceMember(userToken, teamspaceId, isAccountOper, "" + migrationRequest.getRecipientCloudUserId(), INodeACL.TYPE_USER, TeamRole.ROLE_MANAGER, ResourceRole.EDITER);
							recipientMemberId = createdmemberShip.getId();
						}

						// 将接受者变成团队空间的拥有者
						teamSpaceMembershipManager.modifyTeamSpaceMemberRoleById(userToken, teamspaceId, recipientMemberId, TeamRole.ROLE_ADMIN, null, false);
					}

					userToken.setId(migrationRequest.getRecipientCloudUserId());
					// 将离职用户从团队空间删除
					teamSpaceMembershipManager.deleteTeamSpaceMemberById(userToken, teamspaceId,
							departureMemberShip.getId());
				} catch (Exception e) {
					LOGGER.error("[DataMigrationManagerImpl] migrateTeamspace error:" + e.getMessage(), e);
					MigrationException.throwMigrationException(e, EX_MIGRATION_TEAMSPACE_ERROR);
				}
			}
		}
	}
	
	/**
	 * 处理用户组
	 * @param userToken
	 * @param recipientUser
	 * @param migrationRequest
	 */
	private void migrateGroup(UserToken userToken, User recipientUser, DataMigrationRequestDto migrationRequest) {
		// 1、得到离职人员所在的组编号信息
		List<GroupInfo> departureOwnerGroups = groupMemberService
				.getUserGroupList(migrationRequest.getDepartureCloudUserId());
		// 2、得到接受人员所在的组编号信息
		List<GroupInfo> recipientOwnerGroups = groupMemberService
				.getUserGroupList(migrationRequest.getRecipientCloudUserId());

		// 3、遍历离职用户
		if (null != departureOwnerGroups) {
			try {
				for (GroupInfo departureGroup : departureOwnerGroups) {
					userToken.setId(migrationRequest.getDepartureCloudUserId());
					if (departureGroup.getGroupRole() == GroupConstants.GROUP_ROLE_ADMIN) { // 为组拥有者
						boolean isRecipientUserInGroup = false;
						long groupId = departureGroup.getId();

						// 3.1 判断接受用户是否在该组中
						if (null != recipientOwnerGroups) {
							for (GroupInfo recipientGroup : recipientOwnerGroups) {
								if (recipientGroup.getId() == departureGroup.getId()) {
									isRecipientUserInGroup = true;
									break;
								}
							}
						}

						if (!isRecipientUserInGroup) { // 接受用户不在群组中,将接受用户添加进群组
							RestAddGroupRequest restAddGroupRequest = new RestAddGroupRequest();
							restAddGroupRequest.setGroupRole(GroupConstants.ROLE_MANAGER);

							RestGroupMember groupMember = new RestGroupMember();
							groupMember.setGroupId(groupId);
							groupMember.setGroupRole(GroupConstants.ROLE_MANAGER);
							groupMember.setLoginName(recipientUser.getLoginName());
							groupMember.setName(recipientUser.getName());
							groupMember.setUserId(recipientUser.getId());
							groupMember.setUsername(recipientUser.getLoginName());
							groupMember.setUserType(GroupConstants.USERTYPE_USER);

							restAddGroupRequest.setMember(groupMember);
							groupMembershipManager.addMemberShips(userToken, restAddGroupRequest,
									departureGroup.getId());

							// 更改拥有者
							groupMembershipManager.modifyMemberships(userToken, GroupConstants.ROLE_ADMIN,
									migrationRequest.getRecipientCloudUserId(), departureGroup.getId());
						}
					}

					// 删除离职用户，更改接收用户为拥有者
					userToken.setId(migrationRequest.getRecipientCloudUserId());
					groupMembershipManager.deleteOne(userToken, migrationRequest.getDepartureCloudUserId(),
							departureGroup.getId());
				}
			} catch (Exception th) {
				LOGGER.error("[DataMigrationManagerImpl] migrateGroup error:" + th.getMessage(), th);
				MigrationException.throwMigrationException(th, EX_MIGRATION_GROUP_ERROR);
			}
		}
	}

	/**
	 * 处理离职用户的个人文件数据
	 * @param userToken
	 * @param migrationRequest
	 * @param folderName
	 * @return
	 */
	private Long migratePersonalFile(UserToken userToken, DataMigrationRequestDto migrationRequest, String folderName){
		// III:处理个人文件夹数据，为接受用户创建文件夹
		try {
			userToken.setId(migrationRequest.getRecipientCloudUserId());
			//userToken.setId(User.SYSTEM_USER_ID);
			
			INode folderNode = createMigrateFolderForRecipienter(migrationRequest, folderName);
			createInodeAclForRecipienter(userToken.getId(), migrationRequest);
			
			return folderNode.getId();
		} catch (Exception th) {
			LOGGER.error("[DataMigrationManagerImpl] migratePersonalFile error:" + th.getMessage(), th);
			MigrationException.throwMigrationException(th, EX_MIGRATION_PERSONAL_FILE_ERROR);
		}
		
		throw new MigrationException();
	}

	/**
	 * 为接收用户创建迁移文件夹
	 * @param migrationRequest
	 * @param folderName
	 * @return
	 */
	private INode createMigrateFolderForRecipienter(DataMigrationRequestDto migrationRequest, String folderName) {
		CreateFolderRequest createRequest = new CreateFolderRequest();
		createRequest.setParent(0L);
		createRequest.setContentCreatedAt(migrationRequest.getRecipientCloudUserId());
		createRequest.setContentModifiedAt(migrationRequest.getRecipientCloudUserId());
		createRequest.setName(folderName);
		createRequest.setAutoMerge(true);

		Date currentDate = new Date();
		INode folderNode = createRequest.transToINode();
		folderNode.setOwnedBy(migrationRequest.getRecipientCloudUserId());
		folderNode.setName(folderName);

		INode parentInode = fileBaseService.getAndCheckParentNode(folderNode);
		
		folderNode.setId(fileBaseService.buildINodeID(parentInode.getOwnedBy()));
		folderNode.setCreatedBy(migrationRequest.getDepartureCloudUserId());
		folderNode.setOwnedBy(migrationRequest.getRecipientCloudUserId());
		folderNode.setCreatedAt(currentDate);
		folderNode.setModifiedAt(currentDate);
		folderNode.setModifiedBy(migrationRequest.getRecipientCloudUserId());
		folderNode.setType((byte)INode.TYPE_MIGRATION);
		folderNode.setDoctype(Doctypes.other.getValue());
		fileBaseService.createNode(folderNode);
		
		return folderNode;
	}

	/**
	 * 为接收用户创建ACL
	 * @param userId
	 * @param migrationRequest
	 */
	private void createInodeAclForRecipienter(long userId, DataMigrationRequestDto migrationRequest) {
		INodeACL inodeAcl = new INodeACL();
		Date date = new Date();
		
		inodeAcl.setId(nodeACLIdGenerateService.getNextNodeACLId(migrationRequest.getDepartureCloudUserId()));
		inodeAcl.setOwnedBy(migrationRequest.getDepartureCloudUserId());
		inodeAcl.setiNodeId(INode.FILES_ROOT);
		inodeAcl.setAccessUserId(String.valueOf(migrationRequest.getRecipientCloudUserId()));
		inodeAcl.setUserType(INodeACL.TYPE_MIGRATION);
		inodeAcl.setResourceRole(ResourceRole.VIEWER);
		inodeAcl.setCreatedAt(date);
		inodeAcl.setCreatedBy(userId);
		inodeAcl.setModifiedAt(date);
		inodeAcl.setModifiedBy(userId);
		
		iNodeACLService.addINodeACL(inodeAcl);
	}
	
	/**
	 * 离职用户数据迁移
	 * @param userToken
	 * @param migrationRequest
	 * @param folderName
	 * @return
	 */
	public Long migrateData(UserToken userToken, User recipientUser, DataMigrationRequestDto migrationRequest, String folderName){
		migrateTeamspace(userToken, migrationRequest);
		migrateGroup(userToken, recipientUser, migrationRequest);
		Long nodeId = migratePersonalFile(userToken, migrationRequest, folderName);
		
		return nodeId;
	}
	
    @Override
    public List<Long> listUserTeamSpaceIds(String userId, String userType, List<Order> orderList) {
        
        return teamSpaceMembershipsDAO.listUserTeamSpaceIds(userId, userType, orderList);
    }

	@Override
	public void cleanDepartureUserInfo(UserToken userToken, MigrationRecordDto migrationRecord) {
		if (null != migrationRecord){
			try {
				// 1、删除接受用户的文件夹
				deleteRecipientFolder(migrationRecord.getRecipientUserId(), migrationRecord.getInodeId());
				
				// 2、删除离职用户的个人文件数据信息
				inodeDao.updateAllNodesStatusToDelete(migrationRecord.getDepartureUserId());
				userToken.setId(migrationRecord.getDepartureUserId());
				trashServiceV2.cleanTrash(userToken, migrationRecord.getDepartureUserId());
				
				// 3、更改用户状态（用户不做物理删除）
				userDAOV2.updateStatus(userToken.getAccountId(), migrationRecord.getDepartureUserId(),
						User.STATUS_DISABLE_INTEGER);
			} catch (Exception e) {
				LOGGER.error("[DataMigrationManagerImpl] cleanDepartureUserInfo error:" + e.getMessage(), e);
			}
		} else
			throw new InvalidParamException();
	}
	
	/**
	 * 删除接受用户的离职文件夹
	 * @param recipientUserId
	 * @param inodeId
	 */
	private void deleteRecipientFolder(long recipientUserId, long inodeId) {
		try {
			INode file = new INode();
			file.setOwnedBy(recipientUserId);
			file.setId(inodeId);

			inodeDao.delete(file);
		} catch (Exception e) {
			LOGGER.error("[DataMigrationManagerImpl] deleteRecipientFolder error:" + e.getMessage(), e);
		}
	}
}
