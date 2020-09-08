package com.huawei.sharedrive.app.files.service.impl;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.filelabel.service.IFileLabelService;
import com.huawei.sharedrive.app.filelabel.util.FilelabelUtils;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.dao.INodeDAOV2;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderServiceV2;
import com.huawei.sharedrive.app.files.service.NodeMessageService;
import com.huawei.sharedrive.app.files.service.NodeService;
import com.huawei.sharedrive.app.files.service.NodeUpdateService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.openapi.domain.node.ListFolderRequest;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.service.LinkServiceV2;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pw.cdmi.box.domain.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文件或文件夹相关操作业务实现类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-5-12
 * @see
 * @since
 */
@Component
public class NodeServiceImpl implements NodeService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeServiceImpl.class);
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private NodeMessageService nodeMessageService;
    
    @Autowired
    private NodeUpdateService nodeUpdateService;
    
    @Autowired
    private INodeACLService iNodeACLService;
    
    @Autowired
    private INodeDAO nodeDao;
    
    @Autowired
    private INodeDAOV2 nodeDaoV2;
    
    @Autowired
    private IFileLabelService fileLabelService;
    
    @Autowired
    private FolderServiceV2 folderServiceV2;
    
    @Autowired
	private LinkServiceV2 linkServiceV2;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteNode(UserToken user, INode nodeInfo) throws BaseRunException
    {
        long ownerId = nodeInfo.getOwnedBy();
        long nodeId = nodeInfo.getId();
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user, nodeInfo, AuthorityMethod.DELETE_ALL.name());
        
        String[] logMsgs = new String[]{String.valueOf(nodeInfo.getOwnedBy()),
            String.valueOf(nodeInfo.getParentId())};
        String keyword = StringUtils.trimToEmpty(nodeInfo.getName());
        UserLogType userLogType = FilesCommonUtils.isFolderType(nodeInfo.getType())
            ? UserLogType.DELETE_FOLDER : UserLogType.DELETE_FILE;
            
        // 非版本文件删除至回收站, 版本文件直接删除
        if (INode.TYPE_VERSION != nodeInfo.getType())
        {
            // 全盘备份的计算机名目录和磁盘目录不允许删除
            if (FilesCommonUtils.isBackupFolderType(nodeInfo.getType()))
            {
                String message = "node type not allowed to delete, owner id:" + nodeInfo.getOwnedBy()
                    + ", node id:" + nodeInfo.getId() + ",type:" + nodeInfo.getType();
                throw new ForbiddenException(message);
            }
            
            // 对于邮件归档文件夹，暂不提供删除功能
            if (FilesCommonUtils.isEmailBackupFolderType(nodeInfo.getType()))
            {
                String message = "email node type not allowed to delete, owner id:" + nodeInfo.getOwnedBy()
                    + ", node id:" + nodeInfo.getId() + ",type:" + nodeInfo.getType();
                throw new ForbiddenException(message);
            }
            
            deleteToTrash(user, nodeInfo);
            
            fileBaseService.sendINodeEvent(user,
                EventType.INODE_DELETE,
                nodeInfo,
                null,
                userLogType,
                logMsgs,
                keyword);
        }
        else
        {
            userLogType = UserLogType.DELETE_VERSION_FILE;
            logMsgs = new String[]{String.valueOf(nodeInfo.getOwnedBy()),
                String.valueOf(nodeInfo.getParentId()), String.valueOf(nodeInfo.getId())};
            // 更新节点状态为彻底删除
            nodeUpdateService.updateAtmoNodeStatusToDelete(nodeInfo, INode.STATUS_DELETE);
            
            // 更新历史版本数
            nodeDao.decreaseFileVersionNum(nodeInfo.getOwnedBy(), nodeInfo.getParentId());
            
            fileBaseService.sendINodeEvent(user,
                EventType.VERSION_DELETE,
                nodeInfo,
                null,
                userLogType,
                logMsgs,
                keyword);
                
        }
        
        // 標簽刪除
        fileLabelService.unbindAllFileLabelForInode(ownerId, nodeId, user.getAccountId());
        folderServiceV2.deleteRecentByNode(nodeInfo.getOwnedBy(),nodeInfo.getId());
        if(nodeInfo.getType()>1){
        	  folderServiceV2.deleteShortByNodeId(nodeInfo.getOwnedBy(),nodeInfo.getId());
        }
        linkServiceV2.deleteAllLinkByNode(nodeInfo);
		List<INodeLink> list = linkServiceV2.listNodeAllLinks(user, ownerId, nodeId);
		
		for(INodeLink iNodeLink : list){
			linkServiceV2.deleteLinkByTypeOrId(user, nodeInfo,"", iNodeLink.getId());
		}
    }
    
    @Override
    public void deleteUserAllNodes(Long ownerId)
    {
        nodeDao.updateAllNodesStatusToDelete(ownerId);
    }
    
    @Override
    public List<INode> getNodeByName(UserToken user, long ownerId, long parentId, String name)
        throws BaseRunException
    {
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user,
            new INode(ownerId, parentId),
            AuthorityMethod.GET_INFO.name());
        List<INode> list = nodeDao.getNodeByName(ownerId, parentId, name);
        String[] params = new String[]{String.valueOf(ownerId), String.valueOf(parentId)};
        String keyword = StringUtils.trimToEmpty(name);
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_FOLDER_BY_NAME,
            params,
            keyword);
        return list;
    }
    
    @Override
    public List<INode> getNodePath(UserToken token, long ownerId, long nodeId) throws BaseRunException
    {
        iNodeACLService.vaildINodeOperACL(token, new INode(ownerId, nodeId), AuthorityMethod.GET_INFO.name());
        
        List<INode> list = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        
        // 当前节点
        INode node = fileBaseService.getAndCheckNodeForGetNodePath(ownerId, nodeId);
        if (node == null)
        {
            throw new NoSuchItemsException("The node doest not exist");
        }
        
        // 递归获取父节点加入集合
        while (node.getParentId() != INode.FILES_ROOT)
        {
            node = fileBaseService.getAndCheckNodeForGetNodePath(ownerId, node.getParentId());
            if (node == null)
            {
                throw new NoSuchParentException("The parent node does not exist");
            }
            list.add(node);
        }
        String[] logMsgs = new String[]{String.valueOf(node.getOwnedBy()),
            String.valueOf(node.getParentId())};
        String keyword = StringUtils.trimToEmpty(node.getName());
        fileBaseService.sendINodeEvent(token,
            EventType.OTHERS,
            node,
            null,
            UserLogType.GET_NODE_PATH,
            logMsgs,
            keyword);
        return list;
    }

    @Override
    public List<INode> getNodePath(long ownerId, long nodeId) throws BaseRunException {
        List<INode> list = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);

        // 当前节点
        INode node = fileBaseService.getAndCheckNodeForGetNodePath(ownerId, nodeId);
        if (node == null) {
            LOGGER.warn("The node doest not exist: ownerId={}, nodeId={}", ownerId, nodeId);
            throw new NoSuchItemsException("The node doest not exist");
        }

        //将自己加入列表中
        list.add(node);

        // 递归获取父节点加入集合
        while (node.getParentId() != INode.FILES_ROOT) {
            node = fileBaseService.getAndCheckNodeForGetNodePath(ownerId, node.getParentId());
            if (node == null) {
                throw new NoSuchParentException("The parent node does not exist");
            }
            //保证最上层的目录在列表的前端
            list.add(0, node);
        }
        return list;
    }
    
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public INode renameAndSetSyncStatus(UserToken userToken, long ownerId, INode node, String newName, Boolean isSync, byte type) throws BaseRunException {
        if (!FilesCommonUtils.isFolderType(node.getType()) && newName != null) {
            FilesCommonUtils.checkNodeNameVaild(newName);
        }
        // 权限检测
        iNodeACLService.vaildINodeOperACL(userToken, node, AuthorityMethod.PUT_RENAME.name());

        String[] logMsgs = new String[]{String.valueOf(ownerId), String.valueOf(node.getParentId()),
                String.valueOf(node.getSyncStatus())};
        String keyword = StringUtils.trimToEmpty(node.getName());
        // 重命名
        if (StringUtils.isNotBlank(newName) && !StringUtils.equals(newName, node.getName())) {
            keyword = StringUtils.trimToEmpty(newName);
            // 重名检测
            if (fileBaseService.isSameNameNodeExistNoSelf(ownerId, node.getParentId(), node.getId(), newName)) {
                throw new FilesNameConflictException();
            }
            fileBaseService.setNodeSyncVersion(node.getOwnedBy(), node);

            node.setName(newName);
            node.setModifiedAt(new Date());
            node.setModifiedBy(userToken.getId());

            nodeDao.updateForRename(node);
            nodeMessageService.notifyShareToUpdateMsg(node,userToken.getId());
            UserLogType logType = UserLogType.UPDATE_FOLDER_NAME;
            if (type == INode.TYPE_FILE) {
                logType = UserLogType.UPDATE_FILE_NAME;
            }
            fileBaseService.sendINodeEvent(userToken,
                    EventType.INODE_UPDATE_NAME_SYNC,
                    node,
                    null,
                    logType,
                    logMsgs,
                    keyword);
        }

        // 设置同步状态
        if (isSync != null) {
            logMsgs = new String[]{"", String.valueOf(ownerId), String.valueOf(node.getParentId()),
                    String.valueOf(node.getSyncStatus())};
            // 非根目录下的节点不允许设置同步状态
            if (INode.FILES_ROOT != node.getParentId()) {
                throw new ForbiddenException();
            }
            // 全盘备份文件夹不允许设置同步状态
            if (FilesCommonUtils.isBackupFolderType(node.getType())) {
                throw new ForbiddenException();
            }
            if (FilesCommonUtils.isEmailBackupFolderType(node.getType())) {
                throw new ForbiddenException();
            }
            fileBaseService.setNodeSyncVersion(node.getOwnedBy(), node);
            if (isSync) {
                node.setSyncStatus(INode.SYNC_STATUS_SETTED);
                logMsgs[0] = "true";
            } else if (node.getType() == INode.TYPE_BACKUP_EMAIL) {
                node.setSyncStatus(INode.SYNC_STATUS_EMAIL);
                logMsgs[0] = "false";
            } else {
                node.setSyncStatus(INode.SYNC_STATUS_UNSET);
                logMsgs[0] = "false";
            }
            node.setModifiedAt(new Date());
            node.setModifiedBy(userToken.getId());
            nodeUpdateService.updateAtmoNodeSyncStatus(node);
            UserLogType logType = UserLogType.UPDATE_FOLDER_SYNC;
            if (type == INode.TYPE_FILE) {
                logType = UserLogType.UPDATE_FILE_SYNC;
            }
            fileBaseService.sendINodeEvent(userToken,
                    EventType.INODE_UPDATE_NAME_SYNC,
                    node,
                    null,
                    logType,
                    logMsgs,
                    keyword);
        }

        // 发送日志 TODO 两种事件合一
        FilesCommonUtils.setNodeVersionsForV2(node);
        return node;
    }
    
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public INode renameVersionNode(UserToken userToken, long ownerId, INode node, String newName, Boolean isSync, byte type) throws BaseRunException {
        return renameAndSetSyncStatus(userToken, ownerId, node, newName, isSync, type);
    }
    
    
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public FileINodesList search(UserToken user, long ownerId, String name, long offset, int limit,
        List<Order> orderList, List<Thumbnail> thumbnailList, boolean withExtraType) throws BaseRunException
    {
        if (null == user)
        {
            LOGGER.error("user, inode is null");
            throw new BadRequestException();
        }
        
        if (StringUtils.isBlank(name))
        {
            LOGGER.error("name is blank");
            throw new BadRequestException();
        }
        
        INode rootNode = new INode();
        rootNode.setOwnedBy(ownerId);
        rootNode.setId(INode.FILES_ROOT);
        iNodeACLService.vaildINodeOperACL(user, rootNode, AuthorityMethod.GET_INFO.name());
        
        // 编码转换
        String transName = FilesCommonUtils.transferStringForSql(name);
        
        int totalCount = nodeDaoV2.getINodeCountByName(ownerId, transName, withExtraType, null, null);
//        List<INode> tempNode=new ArrayList<>();
//        Long curentOffset=0L;
        List<INode> list = nodeDaoV2.searchByName(ownerId,
            transName,
            orderList,
            offset,
            limit,
            withExtraType, null, null);
        //过滤掉无权限的
//        for(int i=list.size()-1;i>=0;i--){
//        	try{
//        	    iNodeACLService.vaildINodeOperACL(user, list.get(i), AuthorityMethod.GET_INFO.name());
//			} catch (Exception e) {
				// TODO: handle exception
//				list.remove(i);
//				totalCount=totalCount-1;
//			}
//        }
//        for(int i=(int) (offset*limit);i<(int)(offset+1)*limit;i++){
//        	if(i<list.size()){
//        		tempNode.add(list.get(i));
//        	}
        	
//        }
        FileINodesList folderLists = new FileINodesList();
        folderLists.setTotalCount(totalCount);
        folderLists.setLimit(limit);
        folderLists.setOffset(offset);
        
        List<INode> folderList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        List<INode> fileList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        
        for (INode temp : list)
        {
            if (FilesCommonUtils.isFolderType(temp.getType()))
            {
                folderList.add(temp);
            }
            else if (temp.getType() == INode.TYPE_FILE)
            {
                fillThumbnailUrl(thumbnailList, user.getId(), temp);
                FilesCommonUtils.setNodeVersionsForV2(temp);
                // 增加文件标签信息
                FilelabelUtils.fillFilelabelForNode(user.getAccountId(), temp, fileLabelService);
                fileList.add(temp);
            }
        }
        
        folderLists.setFolders(folderList);
        folderLists.setFiles(fileList);
        String[] logMsgs = new String[]{StringUtils.trimToEmpty(name),
            String.valueOf(folderLists.getFolders().size()), String.valueOf(folderLists.getFiles().size())};
        String keyword = StringUtils.trimToEmpty(name);
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.SEARCH_NODE_LISTS,
            logMsgs,
            keyword);
        return folderLists;
    }
    
    
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public FileINodesList searchWithAuth(UserToken user, long ownerId, String name, long offset, int limit,
        List<Order> orderList, List<Thumbnail> thumbnailList, boolean withExtraType) throws BaseRunException
    {
        if (null == user)
        {
            LOGGER.error("user, inode is null");
            throw new BadRequestException();
        }
        
        if (StringUtils.isBlank(name))
        {
            LOGGER.error("name is blank");
            throw new BadRequestException();
        }
        
        INode rootNode = new INode();
        rootNode.setOwnedBy(ownerId);
        rootNode.setId(INode.FILES_ROOT);
        iNodeACLService.vaildINodeOperACL(user, rootNode, AuthorityMethod.GET_INFO.name());
        
        // 编码转换
        String transName = FilesCommonUtils.transferStringForSql(name);
        
        // 其他权限控制
//        result = aCLRuleFactory.getNodeACL(inode, userID, userType);
        
        
        int totalCount = nodeDaoV2.getINodeCountByName(ownerId, transName, withExtraType, null, null);
        List<INode> list = nodeDaoV2.searchByName(ownerId,
            transName,
            orderList,
            offset,
            limit,
            withExtraType, null, null);
        
        for(int i=list.size()-1;i>=0;i--){
        	try {
        	    iNodeACLService.vaildINodeOperACL(user, list.get(i), AuthorityMethod.GET_INFO.name());
			} catch (Exception e) {
				// TODO: handle exception
				list.remove(i);
			}
        }    
        
        FileINodesList folderLists = new FileINodesList();
        folderLists.setTotalCount(totalCount);
        folderLists.setLimit(limit);
        folderLists.setOffset(offset);
        
        List<INode> folderList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        List<INode> fileList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        
        for (INode temp : list)
        {
            if (FilesCommonUtils.isFolderType(temp.getType()))
            {
                folderList.add(temp);
            }
            else if (temp.getType() == INode.TYPE_FILE)
            {
                fillThumbnailUrl(thumbnailList, user.getId(), temp);
                FilesCommonUtils.setNodeVersionsForV2(temp);
                // 增加文件标签信息
                FilelabelUtils.fillFilelabelForNode(user.getAccountId(), temp, fileLabelService);
                fileList.add(temp);
            }
        }
        
        folderLists.setFolders(folderList);
        folderLists.setFiles(fileList);
        String[] logMsgs = new String[]{StringUtils.trimToEmpty(name),
            String.valueOf(folderLists.getFolders().size()), String.valueOf(folderLists.getFiles().size())};
        String keyword = StringUtils.trimToEmpty(name);
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.SEARCH_NODE_LISTS,
            logMsgs,
            keyword);
        return folderLists;
    }
    
    
    private void deleteToTrash(UserToken user, INode nodeInfo)
    {
        // 只更新同步版本号
        fileBaseService.setNodeSyncVersion(nodeInfo.getOwnedBy(), nodeInfo);
        nodeInfo.setStatus(INode.STATUS_TRASH);
        nodeInfo.setModifiedBy(user.getId());
        nodeInfo.setModifiedAt(new Date());
        nodeUpdateService.updateINodeStatus(nodeInfo);
        /* notifyAllToDeleteMsg(nodeInfo); */
        nodeMessageService.notifyShareToTrashMsg(nodeInfo,user.getId());
        nodeUpdateService.updateFolderStatusByLevel(user, nodeInfo);
        fileBaseService.sendINodeEvent(user, EventType.INODE_DELETE, nodeInfo, null, null, null, null);
    }
    
    /*
     * private void notifyAllToDeleteMsg(INode subNode) { // 通知共享
     * nodeMessageService.notifyShareToDeleteMsg(subNode); // 通知外链
     * 
     * // 通知ACL
     * 
     * }
     */
    
    /**
     * 填充缩略图地址
     * 
     * @param thumbSize
     * @param userId
     * @param node
     * @throws BaseRunException
     */
    private void fillThumbnailUrl(List<Thumbnail> thumbnailList, long userId, INode node)
        throws BaseRunException
    {
        if (!FilesCommonUtils.isImage(node.getName()) || thumbnailList == null)
        {
            return;
        }
        
        DataAccessURLInfo urlInfo = null;
        ThumbnailUrl thumbnailUrl = null;
        for (Thumbnail thumbnail : thumbnailList)
        {
            urlInfo = fileBaseService.getINodeInfoDownURL(userId, node);
            thumbnailUrl = new ThumbnailUrl(
                urlInfo.getDownloadUrl() + FileBaseServiceImpl.getThumbnaliSuffix(thumbnail));
            node.addThumbnailUrl(thumbnailUrl);
        }
    }
    
    private void getenlafu(){
    	
    }
    
    @Override
    public FileINodesList search(UserToken user, long ownerId, ListFolderRequest searchRequest)
        throws BaseRunException {
        
        if (null == user) {
            LOGGER.error("user, inode is null");
            throw new BadRequestException();
        }
        
        String name = searchRequest.getName();
      
        boolean withExtraType = searchRequest.getWithExtraType();
        INode rootNode = new INode();
        rootNode.setOwnedBy(ownerId);
        rootNode.setId(INode.FILES_ROOT);
        iNodeACLService.vaildINodeOperACL(user, rootNode, AuthorityMethod.GET_INFO.name());
        
        // 编码转换
        String transName = FilesCommonUtils.transferStringForSql(name);
        
        Integer docType = null;
        StringBuilder labelBuf = new StringBuilder();
        if (StringUtils.isNotEmpty(searchRequest.getLabelIds())){
            labelBuf.append(searchRequest.getLabelIds());
        } else {
            if (labelBuf.length() > 0){
                labelBuf.deleteCharAt(labelBuf.length() - 1);
            }
        }

        int totalCount = nodeDaoV2.getINodeCountByName(ownerId, transName, withExtraType, labelBuf.toString(), docType);
        List<INode> list = nodeDaoV2.searchByName(ownerId, transName, searchRequest.getOrder(), searchRequest.getOffset(), 
            searchRequest.getLimit(), withExtraType, labelBuf.toString(), docType);
        
        FileINodesList folderLists = new FileINodesList();
        folderLists.setTotalCount(totalCount);
        folderLists.setLimit(searchRequest.getLimit());
        folderLists.setOffset(searchRequest.getOffset());
        
        List<INode> folderList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        List<INode> fileList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        
        for (INode temp : list) {
            if (FilesCommonUtils.isFolderType(temp.getType())) {
                folderList.add(temp);
            } else if (temp.getType() == INode.TYPE_FILE) {
                fillThumbnailUrl(searchRequest.getThumbnail(), user.getId(), temp);
                FilesCommonUtils.setNodeVersionsForV2(temp);
                // 增加文件标签信息
                FilelabelUtils.fillFilelabelForNode(user.getAccountId(), temp, fileLabelService);
                fileList.add(temp);
            }
        }
        
        folderLists.setFolders(folderList);
        folderLists.setFiles(fileList);
       
        
        String[] logMsgs = new String[]{StringUtils.trimToEmpty(name),
            String.valueOf(folderLists.getFolders().size()), String.valueOf(folderLists.getFiles().size())};
        String keyword = StringUtils.trimToEmpty(name);
        fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.SEARCH_NODE_LISTS,
            logMsgs, keyword);
        return folderLists;
        
    }

}
