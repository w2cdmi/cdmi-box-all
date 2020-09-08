/**
 * 文件夹service
 */
package com.huawei.sharedrive.app.files.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.service.INodeACLManger;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.acl.service.impl.ACLManager;
import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ExistShortcutException;
import com.huawei.sharedrive.app.exception.SecurityMatixException;
import com.huawei.sharedrive.app.filelabel.service.IFileLabelService;
import com.huawei.sharedrive.app.filelabel.util.FilelabelUtils;
import com.huawei.sharedrive.app.files.dao.INodeDAOV2;
import com.huawei.sharedrive.app.files.dao.ObjectSecretLevelDAO;
import com.huawei.sharedrive.app.files.dao.RecentBrowseDao;
import com.huawei.sharedrive.app.files.dao.ShortcutDao;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectSecretLevel;
import com.huawei.sharedrive.app.files.domain.RecentBrowse;
import com.huawei.sharedrive.app.files.domain.Shortcut;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderServiceV2;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.openapi.domain.node.ListFolderRequest;
import com.huawei.sharedrive.app.security.client.SecurityRestClient;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.ufm.tools.DoctypeManager;

@Component
public class FolderServiceImplV2 implements FolderServiceV2 {

	private static Logger logger = LoggerFactory.getLogger(FolderServiceImplV2.class);

	@Autowired
	private FileBaseService fileBaseService;

	@Autowired
	private INodeACLService iNodeACLService;
	
	@Autowired
    private SecurityRestClient securityRestClient;
	
	@Autowired
    private ObjectSecretLevelDAO ojectSecretLevelDAO;

	@Autowired
	private INodeDAOV2 iNodeDAOV2;
	
	@Autowired
	private RecentBrowseDao recentBrowseDao;
	
	@Autowired
	private ShortcutDao shortcutDao;
	
	@Autowired
    private UserService userService;
	
	@Autowired
    private INodeACLManger nodeACLManger;

	@Autowired
	private SecurityMatrixService securityMatrixService;

	@Autowired
	private ACLManager aclManager;

	@Autowired
	private DoctypeManager doctypeManager;
	
	@Autowired
    private IFileLabelService fileLabelService;
	
	@Autowired
	private TeamSpaceDAO teamSpaceDAO;

	@Override
	public INode getFolderInfo(UserToken user, long ownerId, long folderId) throws BaseRunException {
		INode folder = fileBaseService.getAndCheckNode(ownerId, folderId, INode.TYPE_FOLDER_ALL);
		iNodeACLService.vaildINodeOperACL(user, folder, AuthorityMethod.GET_INFO.name());
		String[] logMsgs = new String[] { String.valueOf(ownerId), String.valueOf(folder.getParentId()) };
		String keyword = StringUtils.trim(folder.getName());

		fileBaseService.sendINodeEvent(user, EventType.OTHERS, folder, null, UserLogType.GET_FOLDER, logMsgs, keyword);
		return folder;
	}

	@Override
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public FileINodesList listNodesByFilter(UserToken user, INode node, long offset, int limit, List<Order> orderList,
											List<Thumbnail> thumbnailList, boolean withExtraType, Map<String, String> headerCustomMap)
			throws BaseRunException {
		long userId = user.getId();

		if (node.getId() != INode.FILES_ROOT) { /** 需做权限判断 */
			INode folder = fileBaseService.getAndCheckNode(node.getOwnedBy(), node.getId(),INode.TYPE_FOLDER_ALL);
			// 权限检测
			userId = iNodeACLService.vaildINodeOperACL(user, folder, AuthorityMethod.GET_INFO.name(), true);
		} else {
			// 根节点的权限判断，需要兼顾团队空间
			iNodeACLService.vaildINodeOperACL(user, node, AuthorityMethod.GET_INFO.name());
		}

		TeamSpace  teamSpace= teamSpaceDAO.get(node.getOwnedBy());
		int total = 0;
		String doctypeValue = null;
		if (node.getId() == 0 && null != node.getDoctype()) {

			node.setStatus(INode.STATUS_NORMAL); // 查询正常的数据
			node.setType(INode.TYPE_FILE);// 查询文件类型
			if (node.getDoctype() > 5) {
				doctypeValue = doctypeManager.getDoctypeValueByid(node.getDoctype());
				if (doctypeValue != null) {
					doctypeValue = "\\." + doctypeValue.replaceAll("," , "\\$\\|") + "$";
					total = iNodeDAOV2.getINodeByDoctypeNameCount(node,doctypeValue);
				}
			} else if (doctypeManager.values().contains(node.getDoctype())) {
				total = iNodeDAOV2.getINodeByDoctypeCount(node);
			}
		} else if(teamSpace!=null&&teamSpace.getType()==TeamSpace.TYPE_RECEIVE_FOLDER&&node.getId()==INode.FILES_ROOT){
			
			total = iNodeDAOV2.getSubReciveINodeCount(node, withExtraType);
		}else {
			// 原逻辑
			total = iNodeDAOV2.getSubINodeCount(node, withExtraType);
		}

		FileINodesList fileInodeList = new FileINodesList();
		fileInodeList.setTotalCount(total);
		fileInodeList.setLimit(limit);
		fileInodeList.setOffset(offset);

		List<INode> folderList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
		List<INode> fileList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);

		// RM - 文件夹不匹配大小排序规则，按照名称顺序排序
		if (CollectionUtils.isNotEmpty(orderList)) {
			if (orderList.contains(new Order("SIZE", "ASC"))) {
				orderList.add(new Order("NAME", "ASC"));
			}
		} else {
			orderList = new ArrayList<Order>(1);
			orderList.add(new Order("TYPE", "ASC"));
		}

		List<INode> nodeList = null;
		// 文件智能分类
		if (node.getId() == 0 && null != node.getDoctype()) {

			node.setStatus(INode.STATUS_NORMAL); // 查询正常的数据
			node.setType(INode.TYPE_FILE);// 查询文件类型
			if (node.getDoctype() > 5) {
				if (doctypeValue != null) {
					nodeList = iNodeDAOV2.getINodeByDoctypeName(node,doctypeValue, orderList, offset, limit);
				}
			} else if (doctypeManager.values().contains(node.getDoctype())) {
				nodeList = iNodeDAOV2.getINodeByDoctype(node, orderList, offset, limit);
			}
			//收件箱只查询自己创建的文件
		} else if(teamSpace!=null&&teamSpace.getType()==TeamSpace.TYPE_RECEIVE_FOLDER&&node.getId()==INode.FILES_ROOT){
			
			nodeList=iNodeDAOV2.getSubReciveINode(node, orderList, offset, limit, withExtraType);
			for(int i=0;i<nodeList.size();i++){
				nodeList.get(i).setIsListAcl(true);
			}
			
			//团队空间时 ，判断文件夹是否设置私密
		} else if(teamSpace!=null&&teamSpace.getType()!=TeamSpace.TYPE_RECEIVE_FOLDER){
			
			nodeList = iNodeDAOV2.getINodeByParentAndStatus(node, orderList, offset, limit, withExtraType);
			checkAndFullSecretInfo(user, node, userId, teamSpace, nodeList);
		
		}else {
			// 原逻辑
			
			nodeList = iNodeDAOV2.getINodeByParentAndStatus(node, orderList, offset, limit, withExtraType);
			//我的文件，拥有list权限
			for(int i=0;i<nodeList.size();i++){
				nodeList.get(i).setIsListAcl(true);
			}
		}

		for (INode temp : nodeList) {

			if (FilesCommonUtils.isFolderType(temp.getType())) {
				
				folderList.add(temp);
				
			} else if (temp.getType() == INode.TYPE_FILE) {
				setNodeThumbnailUrl(user, thumbnailList, userId, temp, headerCustomMap);
				ObjectSecretLevel objectSecretLevel=ojectSecretLevelDAO.getByAccountId(temp.getSha1(), user.getRegionId(), user.getAccountId());
				if(objectSecretLevel!=null){
					temp.setSecretLevel(objectSecretLevel.getSecretLevel());
				}
				FilesCommonUtils.setNodeVersionsForV2(temp);
				fileList.add(temp);
			}
		}

		fileInodeList.setFolders(folderList);
		fileInodeList.setFiles(fileList);
		
		fillFilelabelForNode(user.getAccountId(), fileList);
		
		
		String parentId = String.valueOf(node.getId() != INode.FILES_ROOT ? node.getParentId() : INode.FILES_ROOT);
		String[] logParams = new String[] { String.valueOf(node.getOwnedBy()), parentId };
		String keyword = StringUtils.trimToEmpty(node.getName());
		fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.LIST_FOLDER, logParams, keyword);
		
		// reset
		node.setFilelabelIds(null);
		return fileInodeList;
	}
	
	@Override
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public FileINodesList wxMpListNodesByFilter(UserToken user, INode node, long offset, int limit, List<Order> orderList,
												List<Thumbnail> thumbnailList, boolean withExtraType, Map<String, String> headerCustomMap)
			throws BaseRunException {
		long userId = user.getId();

		TeamSpace teamSpace= teamSpaceDAO.get(node.getOwnedBy());
		int total = 0;
		String doctypeValue = null;
		if (node.getId() == 0 && null != node.getDoctype()) {

			node.setStatus(INode.STATUS_NORMAL); // 查询正常的数据
			node.setType(INode.TYPE_FILE);// 查询文件类型
			if (node.getDoctype() > 5) {
				doctypeValue = doctypeManager.getDoctypeValueByid(node.getDoctype());
				if (doctypeValue != null) {
					doctypeValue = "\\." + doctypeValue.replaceAll("," , "\\$\\|") + "$";
					total = iNodeDAOV2.getINodeByDoctypeNameCount(node,doctypeValue);
				}
			} else if (doctypeManager.values().contains(node.getDoctype())) {
				total = iNodeDAOV2.getINodeByDoctypeCount(node);
			}
		} else if(teamSpace!=null&&teamSpace.getType()==TeamSpace.TYPE_RECEIVE_FOLDER&&node.getId()==INode.FILES_ROOT){
			
			total = iNodeDAOV2.getSubReciveINodeCount(node, withExtraType);
		}else {
			// 原逻辑
			total = iNodeDAOV2.getSubINodeCount(node, withExtraType);
		}

		FileINodesList fileInodeList = new FileINodesList();
		fileInodeList.setTotalCount(total);
		fileInodeList.setLimit(limit);
		fileInodeList.setOffset(offset);

		List<INode> folderList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
		List<INode> fileList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);

		// RM - 文件夹不匹配大小排序规则，按照名称顺序排序
		if (CollectionUtils.isNotEmpty(orderList)) {
			if (orderList.contains(new Order("SIZE", "ASC"))) {
				orderList.add(new Order("NAME", "ASC"));
			}
		} else {
			orderList = new ArrayList<Order>(1);
			orderList.add(new Order("TYPE", "ASC"));
		}

		List<INode> nodeList = null;
		// 文件智能分类
		if (node.getId() == 0 && null != node.getDoctype()) {

			node.setStatus(INode.STATUS_NORMAL); // 查询正常的数据
			node.setType(INode.TYPE_FILE);// 查询文件类型
			if (node.getDoctype() > 5) {
				if (doctypeValue != null) {
					nodeList = iNodeDAOV2.getINodeByDoctypeName(node,doctypeValue, orderList, offset, limit);
				}
			} else if (doctypeManager.values().contains(node.getDoctype())) {
				nodeList = iNodeDAOV2.getINodeByDoctype(node, orderList, offset, limit);
			}
			//收件箱只查询自己创建的文件
		} else if(teamSpace!=null&&teamSpace.getType()==TeamSpace.TYPE_RECEIVE_FOLDER&&node.getId()==INode.FILES_ROOT){
			
			nodeList=iNodeDAOV2.getSubReciveINode(node, orderList, offset, limit, withExtraType);
			for(int i=0;i<nodeList.size();i++){
				nodeList.get(i).setIsListAcl(true);
			}
			
			//团队空间时 ，判断文件夹是否设置私密
		} else if(teamSpace!=null&&teamSpace.getType()!=TeamSpace.TYPE_RECEIVE_FOLDER){
			
			nodeList = iNodeDAOV2.getINodeByParentAndStatus(node, orderList, offset, limit, withExtraType);
			checkAndFullSecretInfo(user, node, userId, teamSpace, nodeList);
		
		}else {
			// 原逻辑
			
			nodeList = iNodeDAOV2.getINodeByParentAndStatus(node, orderList, offset, limit, withExtraType);
			//我的文件，拥有list权限
			for(int i=0;i<nodeList.size();i++){
				nodeList.get(i).setIsListAcl(true);
			}
		}

		for (INode temp : nodeList) {

			if (FilesCommonUtils.isFolderType(temp.getType())) {
				
				folderList.add(temp);
				
			} else if (temp.getType() == INode.TYPE_FILE) {
				setNodeThumbnailUrl(user, thumbnailList, userId, temp, headerCustomMap);
				ObjectSecretLevel objectSecretLevel=ojectSecretLevelDAO.getByAccountId(temp.getSha1(), user.getRegionId(), user.getAccountId());
				if(objectSecretLevel!=null){
					temp.setSecretLevel(objectSecretLevel.getSecretLevel());
				}
				FilesCommonUtils.setNodeVersionsForV2(temp);
				fileList.add(temp);
			}
		}

		fileInodeList.setFolders(folderList);
		fileInodeList.setFiles(fileList);
		
		fillFilelabelForNode(user.getAccountId(), fileList);
		
		
		String parentId = String.valueOf(node.getId() != INode.FILES_ROOT ? node.getParentId() : INode.FILES_ROOT);
		String[] logParams = new String[] { String.valueOf(node.getOwnedBy()), parentId };
		String keyword = StringUtils.trimToEmpty(node.getName());
		fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.LIST_FOLDER, logParams, keyword);
		
		// reset
		node.setFilelabelIds(null);
		return fileInodeList;
	}

	private boolean checkThumbnailAcl(UserToken user, INode node,String enterpriseId) {
		boolean addThumbnailUrl = false;
		if (user.getId() == node.getOwnedBy()) {
			addThumbnailUrl = true;
		} else {
//			ACL nodeACL = null;
//			if (StringUtils.isNotBlank(user.getLinkCode())) {
//				nodeACL = aclManager.getACLForLink(user.getLinkCode(), node);
//			} else {
//				nodeACL = aclManager.getACLForAccessUser(user.getId(), INodeACL.TYPE_USER, node,enterpriseId);
//			}
//			if (nodeACL.isDownload() || nodeACL.isPreview()) {
				addThumbnailUrl = true;
//			}
		}
		return addThumbnailUrl;
	}

	/**
	 * 填充缩略图地址
	 * 
	 * @param thumbSize
	 * @param userId
	 * @param node
	 * @throws BaseRunException
	 */
	@Override
	public void setNodeThumbnailUrl(UserToken user, List<Thumbnail> thumbnailList, long userId, INode node,
			Map<String, String> headerCustomMap) throws BaseRunException {
		if (!FilesCommonUtils.isImage(node.getName())) {
			return;
		}
		if (CollectionUtils.isEmpty(thumbnailList)) {
			return;
		}
		// 根据用户权限判断是否需要获取缩略图
		boolean addThumbnailUrl = false;

//		addThumbnailUrl = judgeBySecMatrix(user, node, headerCustomMap);
		addThumbnailUrl = true;

		DataAccessURLInfo urlInfo = null;
		ThumbnailUrl thumbnailUrl = null;
		for (Thumbnail thumbnail : thumbnailList) {
			if (addThumbnailUrl) {
				urlInfo = fileBaseService.getINodeInfoDownURL(userId, node);
				thumbnailUrl = new ThumbnailUrl(
						urlInfo.getDownloadUrl() + FileBaseServiceImpl.getThumbnaliSuffix(thumbnail));
				node.addThumbnailUrl(thumbnailUrl);
			} else {
				node.addThumbnailUrl(new ThumbnailUrl(""));
			}

		}

	}

	private boolean judgeBySecMatrix(UserToken user, INode node, Map<String, String> headerCustomMap) {
		
		String enterpriseId = "";
		if(user.getAccountVistor()!=null){
			enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
		}
		
		boolean addThumbnailUrl = checkThumbnailAcl(user, node,enterpriseId);
		try {
			securityMatrixService.checkSecurityMatrix(user, node.getOwnedBy(), node.getId(),
					SecurityMethod.FILE_DOWNLOAD, headerCustomMap);
		} catch (SecurityMatixException e) {
			try {
				securityMatrixService.checkSecurityMatrix(user, node.getOwnedBy(), node.getId(),
						SecurityMethod.FILE_PREVIEW, headerCustomMap);
			} catch (SecurityMatixException e1) {
				addThumbnailUrl = false;
			}
		} catch (Exception e) {
			logger.warn("When checkSecurityMatrix download", e);
		}
		return addThumbnailUrl;
	}

	/**
	 * 
	 * 云盘文件智能分类对应的表进行割接。将DocType=5对应的类型修改DocType=x
	 *
	 * <参数类型> @param paramMap keys:{long ownerId,int doctype} <参数类型> @return
	 * <参数类型> @throws Exception
	 *
	 * @return List<INode>
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { RuntimeException.class, Exception.class })
	public void updateINodeDoctype(Map<String, Map<String, Object>> paramMap) throws Exception {
		iNodeDAOV2.updateINodeDoctype(paramMap);
	}

	/**
	 * 
	 * 云盘文件智能分类对应的表进行割接。将DocType=5对应的类型修改DocType=x
	 *
	 * <参数类型> @return <参数类型> @throws Exception
	 *
	 * @return List<INode>
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { RuntimeException.class, Exception.class })
	public void updateINodeDoctypeAll() throws Exception {
		iNodeDAOV2.updateINodeDoctype(null);
	}

	/**
	 * 
	 * 以json格式返回doctype.json文件内容
	 *
	 * <参数类型> @return <参数类型> @throws Exception
	 *
	 * @return String
	 */
	@Override
	public String getJsonDoctype() throws Exception {
		return doctypeManager.getDoctype().toString();
	}

    /**
     * 为文件返回文件标签
     * @param fileList
     */
    private void fillFilelabelForNode(long enterpriseId, List<INode> fileList) {        
        for (INode node : fileList){
            FilelabelUtils.fillFilelabelForNode(enterpriseId, node, fileLabelService);
        }
        
    }


	@Override
	public List<INode> listFolderForRecent(UserToken userToken, int offset, int limit,List<Thumbnail> thumbnailList, Map<String, String> headerCustomMap) {
		// TODO Auto-generated method stub
		List<RecentBrowse>  recentBrowseList= recentBrowseDao.list(userToken.getId(),userToken.getAccountId(), offset, limit);
		List<INode> fileList=new ArrayList<>();
		for (RecentBrowse recentBrowse : recentBrowseList) {
            INode temp=fileBaseService.getINodeInfo(recentBrowse.getOwnedBy(), recentBrowse.getInodeId());
		    if(temp==null){
		    	continue;
		    }
			setNodeThumbnailUrl(userToken, thumbnailList, userToken.getId(), temp, headerCustomMap);
            FilesCommonUtils.setNodeVersionsForV2(temp);
            temp.setModifiedAt(recentBrowse.getLastBrowseTime());
			fileList.add(temp);
		}
		return fileList;
	}

	@Override
	public List<Shortcut> listFolderForShortcut(UserToken userToken) {
		// TODO Auto-generated method stub\
		List<Shortcut> shortcutList = shortcutDao.list(userToken.getId());
		List<Shortcut> newlist = new ArrayList<>();
		for (Shortcut shortcut : shortcutList) {
			INode temp = fileBaseService.getINodeInfo(shortcut.getOwnerId(), shortcut.getNodeId());
			if (temp != null) {
				if(temp.getType()==-7){
					temp.setName("来自:收件箱");
				}
				try {
					shortcut.setNodeName(temp.getName());
					if (shortcut.getType() == Shortcut.TYPE_TEAM) {

						TeamSpace teamSpace = teamSpaceDAO.get(temp.getOwnedBy());
						shortcut.setOwnerName(teamSpace.getName());
						newlist.add(shortcut);
					}else{
						newlist.add(shortcut);
					}
				} catch (Exception e) {
					shortcutDao.delete(shortcut.getId());
				}

			} else {
				shortcutDao.delete(shortcut.getId());
			}
		}
		return newlist;
	}

	@Override
	public void deleteShortcut(long id) {
		// TODO Auto-generated method stub
		shortcutDao.delete(id);
	}

	@Override
	public void createShortcut(Shortcut shortcut) {
		// TODO Auto-generated method stub
		Shortcut shortcutold=shortcutDao.getByOwnerIdAndNodeId(shortcut);
		if(shortcutold!=null){
			 throw new ExistShortcutException();
		}
		
		shortcut.setId(new Date().getTime()+(long)Math.random()*100);
		shortcutDao.create(shortcut);
	}

	@Override
	public void deleteRecentByNode(long ownerId, long nodeId) {
		// TODO Auto-generated method stub
		recentBrowseDao.deleteRecentByNode(ownerId,nodeId);
	}

	@Override
	public void deleteShortByNodeId(long ownedBy, long nodeId) {
		// TODO Auto-generated method stub
		shortcutDao.deleteByNodeId(ownedBy, nodeId);
	}

	@Override
	public void createRecent(long userId,long ownerId,long nodeId) {
		// TODO Auto-generated method stub
		RecentBrowse recentBrowse =new RecentBrowse();
		recentBrowse.setUserId(userId);
		recentBrowse.setInodeId(nodeId);
		recentBrowse.setOwnedBy(ownerId);
		recentBrowse.setLastBrowseTime(new Date());
		recentBrowseDao.create(recentBrowse);
	}

	@Override
	public void deleteRecent(long userId, long ownerId, long nodeId) {
		// TODO Auto-generated method stub
		RecentBrowse recentBrowse =new RecentBrowse();
		recentBrowse.setUserId(userId);
		recentBrowse.setInodeId(nodeId);
		recentBrowse.setOwnedBy(ownerId);
		recentBrowseDao.delete(recentBrowse);
		
	}

	@Override
	public FileINodesList listNodesByFilter(UserToken user, INode parentNode, ListFolderRequest listFolderRequest,
			Map<String, String> headerCustomMap) {
		// TODO Auto-generated method stub
		List<Order> orderList = listFolderRequest.getOrder();
		List<Thumbnail> thumbnailList = listFolderRequest.getThumbnail();
		int limit = listFolderRequest.getLimit();
		long offset = listFolderRequest.getOffset();
		long userId = user.getId();
        if(parentNode.getId()==null){
        	parentNode.setId(INode.FILES_ROOT);
        	iNodeACLService.vaildINodeOperACL(user, parentNode, AuthorityMethod.GET_INFO.name());
        	parentNode.setId(null);
        }else if (parentNode.getId() != INode.FILES_ROOT && parentNode.getId()>=0) { /** 需做权限判断 */
			INode folder = fileBaseService.getAndCheckNode(parentNode.getOwnedBy(), parentNode.getId(),
					INode.TYPE_FOLDER_ALL);
			// 权限检测
			userId = iNodeACLService.vaildINodeOperACL(user, folder, AuthorityMethod.GET_INFO.name(), true);
		} else {
			// 根节点的权限判断，需要兼顾团队空间
			iNodeACLService.vaildINodeOperACL(user, parentNode, AuthorityMethod.GET_INFO.name());
		}

		TeamSpace teamSpace = teamSpaceDAO.get(parentNode.getOwnedBy());
		int total = iNodeDAOV2.getSubINodeCount(parentNode, listFolderRequest);
		FileINodesList fileInodeList = new FileINodesList();
		fileInodeList.setTotalCount(total);
		fileInodeList.setLimit(limit);
		fileInodeList.setOffset(offset);

		List<INode> folderList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
		List<INode> fileList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);

		// RM - 文件夹不匹配大小排序规则，按照名称顺序排序
		if (CollectionUtils.isNotEmpty(orderList)) {
			if (orderList.contains(new Order("SIZE", "ASC"))) {
				orderList.add(new Order("NAME", "ASC"));
			}
		} else {
			orderList = new ArrayList<Order>(1);
			orderList.add(new Order("TYPE", "ASC"));
			orderList.add(new Order("modifiedAt", "DESC"));
		}

		List<INode> nodeList = iNodeDAOV2.getSubNode(parentNode, listFolderRequest);

		checkAndFullSecretInfo(user, parentNode, userId, teamSpace, nodeList);

		for (INode temp : nodeList) {
			if (FilesCommonUtils.isFolderType(temp.getType())) {
				folderList.add(temp);
			} else if (temp.getType() == INode.TYPE_FILE) {
				setNodeThumbnailUrl(user, thumbnailList, userId, temp, headerCustomMap);
				ObjectSecretLevel objectSecretLevel = ojectSecretLevelDAO.getByAccountId(temp.getSha1(),user.getRegionId(), user.getAccountId());
				if (objectSecretLevel != null) {
					temp.setSecretLevel(objectSecretLevel.getSecretLevel());
				}
				FilesCommonUtils.setNodeVersionsForV2(temp);
				fileList.add(temp);
			}
		}

		fileInodeList.setFolders(folderList);
		fileInodeList.setFiles(fileList);

//		fillFilelabelForNode(user.getAccountId(), fileList);
        if(parentNode.getId()==null){
        	parentNode.setId(INode.FILES_ROOT);
        }
		String parentId = String.valueOf(parentNode.getId() != INode.FILES_ROOT ? parentNode.getParentId() : INode.FILES_ROOT);
		String[] logParams = new String[] { String.valueOf(parentNode.getOwnedBy()), parentId };
		String keyword = StringUtils.trimToEmpty(parentNode.getName());
		fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.LIST_FOLDER, logParams, keyword);
		return fileInodeList;
	}

	private void checkAndFullSecretInfo(UserToken user, INode parentNode, long userId, TeamSpace teamSpace,
			List<INode> nodeList) {
		for (int i = 0; i < nodeList.size(); i++) {
			INodeACL inodeRole = new INodeACL();
			inodeRole.setOwnedBy(parentNode.getOwnedBy());
			inodeRole.setiNodeId(nodeList.get(i).getId());
			inodeRole.setAccessUserId(INodeACL.ID_SECRET);
			inodeRole.setUserType(INodeACL.TYPE_SECRET);
			inodeRole.setResourceRole(INodeACL.TYPE_SECRET);
			INodeACL iNodeACL = iNodeACLService.getNodeIsVisibleACL(inodeRole);
			if (iNodeACL != null && teamSpace != null) {
				// 私密文件
				nodeList.get(i).setIsSecret(true);
				if (teamSpace.getOwnerBy() == user.getCloudUserId()) {
					// 文件拥有者
					nodeList.get(i).setIsListAcl(true);
				} else {
					// 其他人需要判断下权限
					ACL acl = nodeACLManger.getINodePermissionsByUser(user, parentNode.getOwnedBy(), nodeList.get(i).getId(), userId, INodeACL.TYPE_USER);
					if (acl != null && acl.isList() == false) {
						nodeList.get(i).setIsListAcl(false);
					} else {
						nodeList.get(i).setIsListAcl(true);
					}
				}
			} else {
				// 公开文件
				nodeList.get(i).setIsListAcl(true);
			}

		}
	}

	@Override
	public void deleteRecentByOwner(long ownerId) {
		// TODO Auto-generated method stub
		RecentBrowse recentBrowse = new RecentBrowse();
		recentBrowse.setUserId(ownerId);
		recentBrowseDao.deleteRecentByUserId(recentBrowse);
	}

	@Override
	public void deleteShortByOwner(long ownerId) {
		// TODO Auto-generated method stub
		shortcutDao.deleteShortByOwner(ownerId);
	}
}
