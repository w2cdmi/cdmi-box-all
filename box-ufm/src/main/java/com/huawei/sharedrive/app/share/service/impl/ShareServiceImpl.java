package com.huawei.sharedrive.app.share.service.impl;

import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.acl.service.INodeACLIdGenerateService;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.acl.service.ResourceRoleService;
import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventService;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.files.service.NodeUpdateService;
import com.huawei.sharedrive.app.files.service.RecentBrowseService;
import com.huawei.sharedrive.app.files.service.impl.FileBaseServiceImpl;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.service.GroupService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.share.MySharesPage;
import com.huawei.sharedrive.app.openapi.domain.share.RestSharePageRequestV2;
import com.huawei.sharedrive.app.openapi.domain.share.SharePageV2;
import com.huawei.sharedrive.app.share.dao.INodeLinkDAO;
import com.huawei.sharedrive.app.share.dao.INodeLinkReverseDao;
import com.huawei.sharedrive.app.share.dao.INodeShareDeleteDao;
import com.huawei.sharedrive.app.share.dao.ShareDAO;
import com.huawei.sharedrive.app.share.dao.ShareToMeDAO;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.domain.INodeShare;
import com.huawei.sharedrive.app.share.domain.INodeShareDelete;
import com.huawei.sharedrive.app.share.domain.SharedUser;
import com.huawei.sharedrive.app.share.domain.UserType;
import com.huawei.sharedrive.app.share.service.LinkService;
import com.huawei.sharedrive.app.share.service.LinkServiceV2;
import com.huawei.sharedrive.app.share.service.ShareService;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.Department;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.DepartmentService;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <一句话描述这个类的作用> <详细描述这个类作用/特点/使用的业务场景>
 * 
 * @see
 */
@Component
public class ShareServiceImpl implements ShareService {

	private static Logger logger = LoggerFactory.getLogger(ShareServiceImpl.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private FileBaseService fileBaseService;

	@Autowired
	private FolderService folderService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private INodeDAO iNodeDao;

	@Autowired
	private NodeUpdateService nodeUpdateService;

	@Autowired
	private INodeACLIdGenerateService nodeACLIdGenerateService;

	@Autowired
	private INodeACLService nodeACLService;

	@Autowired
	private ResourceRoleService resourceRoleService;

	@Autowired
	private ShareDAO shareDAO;

	@Autowired
	private ShareToMeDAO shareToMeDAO;

	@Autowired
	private TeamSpaceDAO teamSpaceDao;

	@Autowired
	private UserDAOV2 userDAO;

	@Autowired
	private UserService userService;

	@Autowired
	private RecentBrowseService recentBrowseService;

	@Autowired
	private DepartmentService departmentService;
	
    @Autowired
    private INodeLinkDAO iNodeLinkDao;
    
    @Autowired
    private INodeLinkReverseDao iNodeLinkReverseDao;
    
    @Autowired
    private INodeShareDeleteDao iNodeShareDeleteDao;
    
    


	/**
	 * 设置共享
	 */
	@Override
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public List<INodeShare> addShareListV2(UserToken user, Long ownerId, List<SharedUser> shareList, long nodeId,
			String role, String message, String additionalLog) throws BaseRunException {
		TeamSpace teamSpaceInfo = teamSpaceDao.get(ownerId);
		if (teamSpaceInfo != null) {
			throw new ForbiddenException("The operation is prohibited");
		}
		INode node = folderService.getNodeInfo(user, ownerId, nodeId);
		if (null == node) {
			throw new NoSuchItemsException();
		}

		// 创建ACL
		role = checkAndGetRoleValid(role);
		sendEvent(user, EventType.SHARE_CREATE, node);
		List<INodeShare> newUserList = new ArrayList<INodeShare>(BusinessConstants.INITIAL_CAPACITIES);
		List<INodeShare> checkRoleList = new ArrayList<INodeShare>(BusinessConstants.INITIAL_CAPACITIES);
		List<INodeShare> failList = new ArrayList<INodeShare>(BusinessConstants.INITIAL_CAPACITIES);
		List<INodeShare> existList = shareDAO.getShareList(ownerId, user.getId() ,node.getId(),null);
		Date now = new Date();

	
		for (SharedUser sharedUser : shareList) {
			User shareUser = null;
			INodeShare tempShare = null;
			Group group = null;
			Department department = null;
			if (sharedUser.getType()==UserType.TYPE_USER) {
				shareUser = userService.get(sharedUser.getId());
			} else if (sharedUser.getType()==UserType.TYPE_GROUP) {
				group = groupService.get(sharedUser.getId());
			} else if (sharedUser.getType()==UserType.TYPE_DEP){
				department = departmentService.getByEnterpriseIdAndDepartmentCloudUserId(user.getAccountVistor().getEnterpriseId(), sharedUser.getId());
			}
			// checkUserExists(shareUser, group);
			tempShare = new INodeShare();
			tempShare.setType(node.getType());
			tempShare.setCreatedAt(now);
			tempShare.setCreatedBy(user.getId());
			tempShare.setModifiedAt(now);
			tempShare.setModifiedBy(user.getId());
			tempShare.setRoleName(role);
			tempShare.setOwnerName(user.getName());
			tempShare.setOwnerLoginName(user.getLoginName());
			tempShare.setOwnerId(ownerId);
			tempShare.setSharedUserType(sharedUser.getType());
			tempShare.setSharedUserId(sharedUser.getId());
			
			fillName( tempShare,shareUser, group,department);
			tryCheckAndSendEvent(user, node, newUserList, checkRoleList, failList, existList, shareUser, tempShare,
					group, additionalLog);
		}

		for (INodeShare tempNewShare : newUserList) {
			tansForAddShareV2(user, node, tempNewShare);
			addNodeShareACL(user, node, tempNewShare, role);
		}
		for (INodeShare tempNewShare : checkRoleList) {
			tansForModifyShare(user, node, tempNewShare, role);
		}
		return failList;
	}

	/**
	 * 取消所有共享
	 */
	@Override
	public List<INodeShare> cancelAllShareV2(UserToken user, long ownerId, long iNodeId) throws BaseRunException {
		INode node = folderService.getNodeInfo(user, ownerId, iNodeId);

		if (null == node) {
			throw new NoSuchItemsException();
		}

		// 权限拥有者不是自己，则不允许删除所有
		if (node.getOwnedBy() != ownerId) {
			throw new ForbiddenException();
		}

		List<INodeShare> sharedList = deleteAllShare(node,user.getId());

		sendEvent(user, EventType.SHARE_DELETE, node);
		String[] logMsgs = new String[] { String.valueOf(ownerId), String.valueOf(node.getParentId()) };
		String keyword = StringUtils.trimToEmpty(node.getName());
		fileBaseService.sendINodeEvent(user, EventType.OTHERS, node, null, UserLogType.DELETE_ALL_SHARE, logMsgs,
				keyword);

		return sharedList;
	}

	/**
	 * 取消所有共享
	 * 
	 * @throws NoSuchItemsException
	 */
	@Override
	public List<INodeShare> deleteAllShare(INode inode,long createdBy) throws BaseRunException {
		List<INodeShare> shareList = shareDAO.getShareListIgnoreStatus(inode.getOwnedBy(),createdBy, inode.getId());
		if (CollectionUtils.isEmpty(shareList)) {
			throw new NoSuchItemsException("The inode has not any share relation.");
		}
		int errorCount = 0;
		for (INodeShare tempShare : shareList) {
			try {
				nodeACLService.deleteByResourceAndUser(inode.getOwnedBy(), inode.getId(),
						String.valueOf(tempShare.getSharedUserId()), getUserType(tempShare.getSharedUserType()));
			} catch (Exception e) {
				logger.warn("[delete share roles Exeption]", e);
			}
			errorCount = tryDeleteByInode(errorCount, tempShare);
			recentBrowseService.deleteRecentByUserId(tempShare.getSharedUserId(), inode.getOwnedBy(), inode.getId());
		}
		if (errorCount > 0) {
			throw new DbCommitException();
		}
		shareDAO.deleteByInode(inode.getOwnedBy(),createdBy, inode.getId());
		User user = userDAO.get(inode.getOwnedBy());
		UserToken userToken = new UserToken();
		userToken.copyFrom(user);
		try {
			folderService.updateNodeShareStatus(userToken, inode, INode.SHARE_STATUS_UNSHARED);
		} catch (Exception e) {
			logger.warn("[update status Exeption]", e);
		}

		return shareList;
	}

	@Override
	public void deleteShareGroup(UserToken user, long groupId, byte userType) {
		List<INodeShare> shareToMeList = shareToMeDAO.getList(groupId, userType);
		for (INodeShare nodeShare : shareToMeList) {
			transForDelShare(user, nodeShare.getiNodeId(), nodeShare);
			nodeACLService.deleteByResourceAndUser(nodeShare.getOwnerId(), nodeShare.getiNodeId(),
					String.valueOf(nodeShare.getSharedUserId()), getUserType(nodeShare.getSharedUserType()));
		}
	}

	/**
	 * 删除共享V2
	 */
	@Override
	public void deleteShareV2(UserToken user, long ownerId, long sharedUserId, byte sharedUserType, long inodeId)
			throws BaseRunException {
		INode dbNode = folderService.getNodeInfo(user, ownerId, inodeId);
		if (null == dbNode) {
			throw new NoSuchItemsException();
		}
		
		INodeShare iNodeShare = new INodeShare();
		iNodeShare.setiNodeId(inodeId);
		iNodeShare.setOwnerId(ownerId);
		iNodeShare.setSharedUserType(sharedUserType);
		iNodeShare.setSharedUserId(sharedUserId);
		iNodeShare.setCreatedBy(user.getId());
		transForDelShare(user, inodeId, iNodeShare);
		// 删除权限表数据
		nodeACLService.deleteByResourceAndUser(ownerId, inodeId, String.valueOf(sharedUserId),getUserType(sharedUserType));
		recentBrowseService.deleteRecentByUserId(sharedUserId, ownerId, inodeId);

	}

	@Override
	public void deleteUserFromSystem(long userId) {
		// 删除该用户共享出去的数据
		List<INodeShare> shareList = this.shareDAO.getAllByCreated(userId);
		for (INodeShare nodeShare : shareList) {
			this.shareToMeDAO.deleteByInode(nodeShare);
		}
		shareDAO.deleteByCreated(userId);
		// 删除共享给该用户数据
		List<INodeShare> shareToMeList = shareToMeDAO.getList(userId, UserType.TYPE_USER);
		for (INodeShare nodeShare : shareToMeList) {
			shareDAO.deleteByInodeAndSharedUser(nodeShare);
			shareToMeDAO.deleteByInode(nodeShare);
		}
	}

	/**
	 * 获取共享列表
	 */
	@Override
	public SharePageV2 getShareUserListOrderV2(UserToken user, long ownerId, long inodeId, List<Order> orderList,
			Limit limit) throws BaseRunException {
		INode inode = folderService.getNodeInfo(user, ownerId, inodeId);

		if (null == inode) {
			logger.error("inode not exist, ownerid:" + ownerId + ",id:" + inodeId);
			throw new NoSuchItemsException();
		}

		INodeShare tempInodeShare = new INodeShare();
		tempInodeShare.setiNodeId(inodeId);
		tempInodeShare.setOwnerId(ownerId);
		tempInodeShare.setCreatedBy(user.getId());
		List<INodeShare> shareList = shareDAO.getSharePageList(tempInodeShare, orderList, limit);
		int totalCount = shareDAO.getShareCountForPage(tempInodeShare);
		SharePageV2 rv = new SharePageV2(shareList, totalCount);
		rv.setLimit(limit.getLength());
		rv.setOffset(limit.getOffset());
		String[] logMsgs = new String[] { String.valueOf(inode.getOwnedBy()), String.valueOf(inode.getParentId()) };
		String keyword = StringUtils.trim(inode.getName());
		fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.LIST_SHARE_USERS, logMsgs,
				keyword);
		return rv;
	}

	@Override
	public MySharesPage listMyShares(UserToken user, RestSharePageRequestV2 req) throws BaseRunException {
		Long total = shareDAO.getMySharesTotals(user.getCloudUserId(), UserType.TYPE_USER,FilesCommonUtils.transferStringForSql(req.getKeyword()),req.getShareType());
		String order = getOrderByStr(req.getOrderList());
		List<INodeShare> dbList = shareDAO.getListV2(user.getCloudUserId(), UserType.TYPE_USER,FilesCommonUtils.transferStringForSql(req.getKeyword()), order, req.getLimit(),req.getShareType());

		for (INodeShare iNodeShare : dbList) {
            INode node = iNodeDao.get(iNodeShare.getOwnerId(), iNodeShare.getiNodeId());
            
            if(node==null){
		        continue;
            }
			if (iNodeShare.getType() == INode.TYPE_FILE) {
				iNodeShare.setPreviewable(true);
				fillThumbnailUrl(req.getThumbnail(), user.getId(), iNodeShare);
			}
			if (iNodeShare.getType() == INode.TYPE_BACKUP_COMPUTER) {
				iNodeShare.setType(INode.TYPE_FOLDER);
				iNodeShare.setExtraType(INode.TYPE_BACKUP_COMPUTER_STR);
			} else if (iNodeShare.getType() == INode.TYPE_BACKUP_DISK) {
				iNodeShare.setType(INode.TYPE_FOLDER);
				iNodeShare.setExtraType(INode.TYPE_BACKUP_DISK_STR);
			} else if (iNodeShare.getType() == INode.TYPE_BACKUP_EMAIL) {
				iNodeShare.setType(INode.TYPE_FOLDER);
				iNodeShare.setExtraType(INode.TYPE_BACKUP_EMAIL_STR);
			}
			// 列举我发出的共享是以node显示的，不能返回role
			
		}

		MySharesPage page = new MySharesPage(dbList, total);

		page.setLimit(req.getLimit().getLength());

		page.setOffset(req.getOffset());
		return page;
	}

	@Override
	public void updateNodeNameAndSize(INode inode,long createdBy) {
		if (inode.getShareStatus() == INode.SHARE_STATUS_UNSHARED) {
			return;
		}

		List<INodeShare> shareList = shareDAO.getShareListIgnoreStatus(inode.getOwnedBy(),createdBy, inode.getId());
		for (INodeShare tempShareNode : shareList) {
			tryUpdateNodeInfo(inode, tempShareNode);
		}
		shareDAO.updateNodeInfo(inode.getOwnedBy(),inode.getCreatedBy(), inode.getId(), inode.getName(), inode.getSize());
	}

	@Override
	public void updateStatus(INode inode,long createdBy, byte status) {
		List<INodeShare> shareList = shareDAO.getShareListIgnoreStatus(inode.getOwnedBy(),createdBy, inode.getId());
		shareDAO.updateStatus(inode.getOwnedBy(),inode.getCreatedBy(), inode.getId(), status);
		for (INodeShare tempShare : shareList) {
			shareToMeDAO.updateStatus(tempShare, status);
		}

	}

	@Override
	public void updateUsername(User user) {
		shareDAO.updateOwnerName(user.getId(),user.getFileCount(), user.getName());
		shareToMeDAO.updateSharedUserName(user.getId(), user.getName());
	}

	/**
	 * 设置共享以后需要添加共享权限
	 * 
	 * @param user
	 * @param node
	 * @param tempNewShare
	 * @throws BaseRunException
	 */
	private void addNodeShareACL(UserToken user, INode node, INodeShare tempNewShare, String role)
			throws BaseRunException {
		INodeACL iNodeACL = new INodeACL();
		Date date = new Date();
		iNodeACL.setId(nodeACLIdGenerateService.getNextNodeACLId(node.getOwnedBy()));
		iNodeACL.setAccessUserId(String.valueOf(tempNewShare.getSharedUserId()));
		iNodeACL.setUserType(getUserType(tempNewShare.getSharedUserType()));
		iNodeACL.setCreatedAt(date);
		iNodeACL.setCreatedBy(user.getId());
		iNodeACL.setiNodeId(node.getId());
		iNodeACL.setiNodePid(node.getParentId());
		iNodeACL.setOwnedBy(node.getOwnedBy());
		iNodeACL.setModifiedAt(date);
		iNodeACL.setModifiedBy(user.getId());
		iNodeACL.setResourceRole(role);

		nodeACLService.addINodeACL(iNodeACL);
	}

	/**
	 * check role valid
	 * 
	 * @param role
	 * @throws InvalidPermissionRoleException
	 */
	private String checkAndGetRoleValid(String role) throws InvalidPermissionRoleException {
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
		} else {
			role = ResourceRole.VIEWER;
		}
		return role;
	}

	/**
	 * 判断是否已经存在的共享关系，如果是新用户，将该用户加入现有用户列表和新用户列表 <br/>
	 * 至判断被共享用户ID和类型
	 * 
	 * @param existList
	 * @param newList
	 * @param tempShare
	 */
	private void checkNewSharedUser(List<INodeShare> existList, List<INodeShare> newList,
			List<INodeShare> checkRoleList, INodeShare tempShare) {
		for (INodeShare nodeShare : existList) {
			if (nodeShare.getSharedUserId().longValue() == tempShare.getSharedUserId().longValue()
					&& nodeShare.getSharedUserType() == tempShare.getSharedUserType()) {
				checkRoleList.add(tempShare);
				return;
			}
		}
		existList.add(tempShare);
		newList.add(tempShare);
	}

	/**
	 * @param shareUser
	 * @param group
	 */
	private void checkUserExists(User shareUser, Group group) {
		if (null == shareUser && group == null) {
			throw new NoSuchUserException();
		}
		if (shareUser != null && shareUser.getType() == User.STATUS_TEAMSPACE_INTEGER) {
			throw new NoSuchUserException("Cann't share to teamspace");
		}
	}

	private List<INodeShare> fullShareName(List<INodeShare> shareList) {
		if (null == shareList) {
			return null;
		}
		User sharedUser = null;
		Group group = null;
		for (INodeShare inodeShare : shareList) {
			if (inodeShare.getSharedUserType() == GroupConstants.GROUP_USERTYPE_USER) {
				sharedUser = userDAO.get(inodeShare.getSharedUserId());
				inodeShare.setSharedUserName(sharedUser.getName());
				inodeShare.setSharedUserLoginName(sharedUser.getLoginName());
				// tempShare.setSharedUserDescrip(sharedUser.getDepartment());
			} else if (inodeShare.getSharedUserType() == GroupConstants.GROUP_USERTYPE_GROUP) {
				group = groupService.get(inodeShare.getSharedUserId());
				inodeShare.setSharedUserName(group.getName());
				inodeShare.setSharedUserLoginName(group.getName());
				inodeShare.setSharedUserDescrip(group.getDescription());
			}else if (inodeShare.getSharedUserType() == UserType.TYPE_DEP) {
				inodeShare.setSharedUserName(inodeShare.getSharedUserName());
				inodeShare.setSharedUserLoginName(inodeShare.getSharedUserName());
				inodeShare.setSharedUserDescrip(inodeShare.getSharedUserName());
			}
		}
		return shareList;
	}

	/**
	 * @param node
	 * @param shareUser
	 * @param group
	 * @return
	 */
	private String[] fillLogMessages(INode node, User shareUser, Group group, String additionalLog) {
		String[] logMsgs = null;
		if (shareUser != null) {
			if (StringUtils.isEmpty(additionalLog)) {
				logMsgs = new String[] {
						StringUtils.trimToEmpty(shareUser.getName() != null ? shareUser.getName() : null),
						String.valueOf(node.getOwnedBy()), String.valueOf(node.getParentId()) };
			} else {
				logMsgs = new String[] {
						StringUtils.trimToEmpty(shareUser.getName() != null ? shareUser.getName() : null),
						String.valueOf(node.getOwnedBy()), String.valueOf(node.getParentId()),
						'[' + additionalLog + ']' };
			}
		} else if (group != null) {
			if (StringUtils.isEmpty(additionalLog)) {
				logMsgs = new String[] { StringUtils.trimToEmpty(group.getName() != null ? group.getName() : null),
						String.valueOf(node.getOwnedBy()), String.valueOf(node.getParentId()) };
			} else {
				logMsgs = new String[] { StringUtils.trimToEmpty(group.getName() != null ? group.getName() : null),
						String.valueOf(node.getOwnedBy()), String.valueOf(node.getParentId()),
						'[' + additionalLog + ']' };
			}

		}
		return logMsgs;
	}

	/**
	 * @param shareUser
	 * @param tempShare
	 * @param group
	 */
	private void fillName(INodeShare tempShare,User shareUser, Group group,Department department) {
		if (group != null) {
			tempShare.setSharedUserLoginName(group.getName());
			tempShare.setSharedUserName(group.getName());
		} else if(department!=null){
			tempShare.setSharedUserLoginName(department.getName());
			tempShare.setSharedUserName(department.getName());
		} else if(shareUser!=null){
			tempShare.setSharedUserLoginName(shareUser.getLoginName());
			tempShare.setSharedUserName(shareUser.getName());
		}
	}

	/**
	 * 填充缩略图地址
	 * 
	 * @param nodeShare
	 * @throws BaseRunException
	 */
	private void fillThumbnailUrl(List<Thumbnail> sizeList, long userId, INodeShare nodeShare)
			throws BaseRunException {
		if (!FilesCommonUtils.isImage(nodeShare.getName()) || sizeList == null) {
			return;
		}
		INode node = iNodeDao.get(nodeShare.getOwnerId(), nodeShare.getiNodeId());
		if (node == null) {
			return;
		}
		DataAccessURLInfo urlInfo = null;
		List<ThumbnailUrl> thumbList = new ArrayList<ThumbnailUrl>(BusinessConstants.INITIAL_CAPACITIES);
		ThumbnailUrl thumbnailUrl = null;
		for (Thumbnail thumbnail : sizeList) {
			urlInfo = fileBaseService.getINodeInfoDownURL(userId, node);
			thumbnailUrl = new ThumbnailUrl(
					urlInfo.getDownloadUrl() + FileBaseServiceImpl.getThumbnaliSuffix(thumbnail));
			thumbList.add(thumbnailUrl);
		}
        nodeShare.setType(node.getType());
		nodeShare.setThumbnailUrlList(thumbList);
	}


	private String getOrderByStr(List<Order> orderList) {
		StringBuffer orderBy = new StringBuffer();
		String field = null;
		if (null == orderList) {
			return "modifiedAt ASC";
		}
		for (Order order : orderList) {
			field = order.getField();

			// 解决中文名称排序问题
			if ("name".equalsIgnoreCase(field)) {
				field = "convert(name using gb2312)";
			}
			orderBy.append(field).append(' ').append(order.getDirection()).append(',');
		}
		orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
		return orderBy.toString();
	}

	private String getUserType(byte userType) {
		if (userType == User.USER_TYPE_USER) {
			return INodeACL.TYPE_USER;
		}
		if (userType == UserType.TYPE_DEP) {
			return INodeACL.TYPE_DEPT;
		}

		return INodeACL.TYPE_GROUP;
	}

	/**
	 * 换算用户类型
	 * 
	 * @param userType
	 * @return
	 */
	private byte getUserType(String userType) {
		if (INodeACL.TYPE_GROUP.equals(userType)) {
			return UserType.TYPE_GROUP;
		}
		if (INodeACL.TYPE_DEPT.equals(userType)) {
			return UserType.TYPE_DEP;
		}

		return UserType.TYPE_USER;
	}

	/**
	 * 发送共享日志日志
	 * 
	 * @param user
	 * @param type
	 * @param srcNode
	 */
	private void sendEvent(UserToken user, EventType type, INode srcNode) {
		try {
			Event event = new Event(user);
			event.setSource(srcNode);
			event.setCreatedAt(new Date());
			event.setType(type);
			eventService.fireEvent(event);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}

	}

	/**
	 * 添加共享的事务处理 <br/>
	 * 先添加从表，然后添加主表，失败回滚 <br/>
	 * 确保主表再次添加可以成功
	 * 
	 * @param user
	 * @param node
	 * @param tempNewShare
	 * @throws DbRollbackException
	 * @throws DbCommitException
	 */
	@SuppressWarnings("PMD.PreserveStackTrace")
	public void tansForAddShareV2(UserToken user, INode node, INodeShare tempNewShare) throws BaseRunException {
		if(tempNewShare.getSharedUserId()!=null&&tempNewShare.getSharedUserId()!=0){
			shareToMeDAO.saveINodeShare(tempNewShare);
		}
		try {
			shareDAO.saveINodeShare(tempNewShare);
		} catch (Exception e) {
			logger.error("tansForAddShare Exception.", e);
			try {
				shareToMeDAO.deleteByInode(tempNewShare);
			} catch (Exception e1) {
				throw new DbRollbackException("", e1);
			}
			throw new DbCommitException(e);
		}

		updateShareStatus(user, node, INode.SHARE_STATUS_SHARED);
	}
	
	
	
	/**
	 * 添加共享的事务处理 <br/>
	 * 先添加从表，然后添加主表，失败回滚 <br/>
	 * 确保主表再次添加可以成功
	 * 
	 * @param user
	 * @param node
	 * @param tempNewShare
	 * @throws DbRollbackException
	 * @throws DbCommitException
	 */
	@SuppressWarnings("PMD.PreserveStackTrace")
	public void tansForAddShareV2(UserToken user,INodeShare tempNewShare) throws BaseRunException {
		
		shareToMeDAO.saveINodeShare(tempNewShare);
		try {
			shareDAO.saveINodeShare(tempNewShare);
		} catch (Exception e) {
			logger.error("tansForAddShare Exception.", e);
			try {
				shareToMeDAO.deleteByInode(tempNewShare);
			} catch (Exception e1) {
				throw new DbRollbackException("", e1);
			}
			throw new DbCommitException(e);
		}

	}


	private void tansForModifyShare(UserToken user, INode node, INodeShare tempNewShare, String role)
			throws BaseRunException {
		INodeACL iNodeACL = nodeACLService.getByResourceAndUser(node.getOwnedBy(), node.getId(),
				String.valueOf(tempNewShare.getSharedUserId()), getUserType(tempNewShare.getSharedUserType()));

		if (iNodeACL == null) {
			addNodeShareACL(user, node, tempNewShare, role);
		} else if (!role.equals(iNodeACL.getResourceRole())) {
			iNodeACL.setModifiedAt(new Date());
			iNodeACL.setModifiedBy(user.getId());

			iNodeACL.setResourceRole(role);
			nodeACLService.modifyINodeACLById(iNodeACL);
			tansForUpdateShare(tempNewShare, iNodeACL.getResourceRole());
		} else {
			logger.info("role not change, no need to update acL");
		}

	}

	/**
	 * @param tempNewShare
	 */
	private void tansForUpdateShare(INodeShare tempNewShare, String oldRole) throws BaseRunException {
		shareToMeDAO.updateRoleName(tempNewShare);
		try {
			shareDAO.updateRoleName(tempNewShare);
		} catch (Exception e) {
			logger.error("tansForAddShare Exception.", e);
			tempNewShare.setRoleName(oldRole);
			shareToMeDAO.deleteByInode(tempNewShare);
		}

	}

	/**
	 * 删除单条共享关系的事务处理
	 * 
	 * @param user
	 * @param inodeId
	 * @param iNodeShare
	 * @throws DbCommitException
	 * @throws DbRollbackException
	 * @throws NoSuchItemsException
	 */
	private void transForDelShare(UserToken user, long inodeId, INodeShare iNodeShare) throws BaseRunException {
		INodeShare dbNodeShare = null;
		int res = 0;
		try {
			if(iNodeShare.getSharedUserId()!=null&&iNodeShare.getSharedUserId()!=0){
				dbNodeShare = shareToMeDAO.getINodeShare(iNodeShare.getOwnerId(), iNodeShare.getiNodeId(),iNodeShare.getSharedUserId(), iNodeShare.getSharedUserType(),iNodeShare.getLinkCode());
				shareToMeDAO.deleteByInode(iNodeShare);
			}
			shareDAO.deleteByInodeAndSharedUser(iNodeShare);
			try {
				if(iNodeShare.getSharedUserType() == UserType.TYPE_DEP){
					INodeShareDelete iNodeShareDelete = new INodeShareDelete();
					iNodeShareDelete.setiNodeId(inodeId);
					iNodeShareDelete.setOwnerId(iNodeShare.getOwnerId());
					iNodeShareDelete.setShareType(iNodeShare.getShareType());
					iNodeShareDelete.setLinkCode(iNodeShare.getLinkCode());
					iNodeShareDeleteDao.delete(iNodeShareDelete);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
			
		} catch (Exception e) {
			boolean rollbackResult = true;
			try {
				/* 失败进行逻辑事务回滚 */
				shareToMeDAO.saveINodeShare(dbNodeShare);
				logger.error("Fail to delete the share for user " + user.getId() + ", rollback the transaction.");
			} catch (RuntimeException e2) {
				logger.error(
						"Fail to delete the share for user " + user.getId() + ", Fail to rollback the transaction.");
				rollbackResult = false;
			} catch (Exception e2) {
				logger.error(
						"Fail to delete the share for user " + user.getId() + ", Fail to rollback the transaction.");
				rollbackResult = false;
			}
			if (rollbackResult) {
				throw new DbCommitException(e);
			} else {
				throw new DbRollbackException("", e);
			}
		}
		if(inodeId!=-1){
			updateStatusToNoShare(iNodeShare.getOwnerId(),user.getId(), inodeId);

			nodeACLService.deleteByResourceAndUser(iNodeShare.getOwnerId(), inodeId,
					String.valueOf(iNodeShare.getSharedUserId()), getUserType(iNodeShare.getSharedUserType()));
		}
		
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	private void tryCheckAndSendEvent(UserToken user, INode node, List<INodeShare> newUserList,
			List<INodeShare> checkRoleList, List<INodeShare> failList, List<INodeShare> existList, User shareUser,
			INodeShare tempShare, Group group, String additionalLog) {
		try {
			tempShare.setName(node.getName());
			tempShare.setSize(node.getSize());
			tempShare.setType(node.getType());
			tempShare.setiNodeId(node.getId());
			checkNewSharedUser(existList, newUserList, checkRoleList, tempShare);
			String[] logMsgs = fillLogMessages(node, shareUser, group, additionalLog);
			String keyword = StringUtils.trimToEmpty(node.getName());
			fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.ADD_SHARE, logMsgs, keyword);
		} catch (Exception e) {
			logger.warn("Can not get the userId for " + tempShare.getSharedUserName(), e);
			failList.add(tempShare);
		}
	}

	private int tryDeleteByInode(int errorCount, INodeShare tempShare) {
		try {
			if(tempShare.getSharedUserId()!=0){
				shareToMeDAO.deleteByInode(tempShare);
			}
			
		} catch (Exception e) {
			logger.error("Can not remove the share", e);
			errorCount++;
		}
		return errorCount;
	}

	private void tryUpdateNodeInfo(INode inode, INodeShare tempShareNode) {
		try {
			shareToMeDAO.updateNodeInfo(tempShareNode.getSharedUserId(), inode.getOwnedBy(), inode.getId(),
					inode.getName(), inode.getSize());
		} catch (Exception e) {
			logger.warn("Fail to update the NodeName for " + tempShareNode.getName(), e);
		}
	}

	/**
	 * 更新节点为共享状态
	 * 
	 * @param user
	 * @param node
	 */
	private void updateShareStatus(UserToken user, INode node, byte status) {
		try {
			folderService.updateNodeShareStatus(user, node, status);
		} catch (Exception e) {
			logger.warn("Fail to update the status to share_status", e);
		}
	}

	/**
	 * 更新节点为非共享状态
	 * 
	 * @param inodeId
	 */
	private void updateStatusToNoShare(long ownerId,long createdBy, long inodeId) {
		try {
			int count = shareDAO.getShareCountForInode(ownerId,createdBy, inodeId);
			if (count > 0) {
				return;
			}
			INode node = new INode();
			node.setOwnedBy(ownerId);
			node.setId(inodeId);
			nodeUpdateService.updateINodeShareStatus(node);
		} catch (Exception e) {
			logger.warn("", e);
		}
	}

	/**
	 * 将转发信息保存到现有的共享关系表中。
	 */
	@Override
	@Transactional
	public boolean addForwardRecord(INodeShare share) throws BaseRunException {
		
		
		if (shareDAO.getForwardRecord(share).size() == 0) {
			shareDAO.saveINodeShare(share);
		}
		
		// 接收记录, 不存在时才插入
		if (shareToMeDAO.getForwardRecord(share) == null) {
			shareToMeDAO.saveINodeShare(share);
			return true;
		}

		return false;
	}
	

	@Override
	public List<INodeShare> addLinkShare(UserToken user, long ownerId,long inodeId, String linkCode, String role) {
		// TODO Auto-generated method stub

//		TeamSpace teamSpaceInfo = teamSpaceDao.get(ownerId);
//		if (teamSpaceInfo != null) {
//			throw new ForbiddenException("The operation is prohibited");
//		}
		INode node = fileBaseService.getINodeInfo(ownerId, inodeId);
		if (null == node) {
			throw new NoSuchItemsException();
		}
		
		Date now = new Date();
		INodeShare tempShare = new INodeShare();
		tempShare = new INodeShare();
		tempShare.setCreatedAt(now);
		tempShare.setCreatedBy(user.getId());
		tempShare.setModifiedAt(now);
		tempShare.setModifiedBy(user.getId());
		tempShare.setRoleName(role);
		tempShare.setOwnerName(user.getName());
		tempShare.setOwnerLoginName(user.getLoginName());
		tempShare.setOwnerId(ownerId);
		tempShare.setName(node.getName());
		tempShare.setiNodeId(inodeId);
		tempShare.setSharedUserType((byte)0);
		tempShare.setSharedUserId(0L);
		tempShare.setLinkCode(linkCode);
		tempShare.setType(node.getType());
		tempShare.setShareType(INodeShare.SHARE_TYPE_LINK);
		
		tansForAddShareV2(user, node, tempShare);
		List<INodeShare> failList = new ArrayList<INodeShare>(BusinessConstants.INITIAL_CAPACITIES);
		return failList;
	
	}
	
	
	
	@Override
	public List<INodeShare> addLinkShare(UserToken user, long ownerId,String nodeNames, String linkCode, String role) {
		// TODO Auto-generated method stub

		TeamSpace teamSpaceInfo = teamSpaceDao.get(ownerId);
		if (teamSpaceInfo != null) {
			throw new ForbiddenException("The operation is prohibited");
		}
		
		Date now = new Date();
		INodeShare tempShare = new INodeShare();
		tempShare = new INodeShare();
		tempShare.setiNodeId(-1);
		tempShare.setCreatedAt(now);
		tempShare.setCreatedBy(user.getId());
		tempShare.setModifiedAt(now);
		tempShare.setModifiedBy(user.getId());
		tempShare.setRoleName(role);
		tempShare.setOwnerName(user.getName());
		tempShare.setOwnerLoginName(user.getLoginName());
		tempShare.setOwnerId(ownerId);
		tempShare.setName(nodeNames);
		tempShare.setSharedUserType((byte)0);
		tempShare.setSharedUserId(0L);
		tempShare.setLinkCode(linkCode);
		tempShare.setShareType(INodeShare.SHARE_TYPE_LINK);
		tansForAddShareV2(user, tempShare);
		List<INodeShare> failList = new ArrayList<INodeShare>(BusinessConstants.INITIAL_CAPACITIES);
		return failList;
	
	}

	@Override
	public void deleteLinkShare(UserToken curUser, INodeShare iNodeShare, String linkCode) {
		// TODO Auto-generated method stub
        //SharedUserId==0 ，表示自己取消共享
		if(iNodeShare.getSharedUserId()==0){
			 List<INodeShare> sharelist = shareDAO.getShareList(iNodeShare.getOwnerId(), iNodeShare.getCreatedBy(),iNodeShare.getiNodeId(),linkCode);
			 for(INodeShare share:sharelist){
				    transForDelShare(curUser, iNodeShare.getiNodeId(), share);
			 }
			 INodeLink queryLink = new INodeLink();
			 queryLink.setId(linkCode);
			 queryLink.setiNodeId(iNodeShare.getiNodeId());
			 INodeLink linkToDelete = iNodeLinkDao.getV2(queryLink);
			 if(linkToDelete!=null){
					iNodeLinkDao.deleteV2(linkToDelete);
					iNodeLinkReverseDao.deleteV2(linkToDelete);
			 }
		}else{
			transForDelShare(curUser, iNodeShare.getiNodeId(), iNodeShare);
			
		}
		
		
	
	}

	@Override
	public void addShareDelete(INodeShareDelete iNodeShareDelete) {
		// TODO Auto-generated method stub
		iNodeShareDeleteDao.addShareDelete(iNodeShareDelete);
	}
}
