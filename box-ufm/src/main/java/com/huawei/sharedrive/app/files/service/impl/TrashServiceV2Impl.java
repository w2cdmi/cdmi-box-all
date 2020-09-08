/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.app.files.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.acl.dao.INodeACLDAO;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BadRequestException;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.FilesNameConflictException;
import com.huawei.sharedrive.app.exception.NoSuchParentException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.dao.INodeDAOV2;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.NodeMessageService;
import com.huawei.sharedrive.app.files.service.TrashServiceV2;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.share.dao.INodeLinkDAO;
import com.huawei.sharedrive.app.share.dao.INodeLinkDynamicDao;
import com.huawei.sharedrive.app.share.dao.INodeLinkReverseDao;
import com.huawei.sharedrive.app.share.dao.ShareDAO;
import com.huawei.sharedrive.app.share.dao.ShareToMeDAO;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.domain.INodeShare;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesDelete;
import com.huawei.sharedrive.app.spacestatistics.service.RecordingDeletedFilesService;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Service
public class TrashServiceV2Impl implements TrashServiceV2
{
    @Autowired
    private NodeMessageService nodeMessageService;
    
    @Autowired
    private RecordingDeletedFilesService recordingDeletedFilesService;
    
    @Autowired
    private UserDAOV2 userDao;
    
    private static class DeleteSumarry
    {
        private int fileCount;
        
        private long size;
        
        public int getFileCount()
        {
            return fileCount;
        }
        
        public void setFileCount(int fileCount)
        {
            this.fileCount = fileCount;
        }
        
        public long getSize()
        {
            return size;
        }
        
        public void setSize(long size)
        {
            this.size = size;
        }
        
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TrashServiceV2Impl.class);
    
    private static final int FILE_LIST_LIMIT = 100;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private INodeDAOV2 iNodeDAOV2;
    
    @Autowired
    private INodeACLDAO iNodeACLDAO;
    
    @Autowired
    private ShareToMeDAO shareToMeDAO;
    
    @Autowired
    private ShareDAO shareDAO;
    
    @Autowired
    private INodeLinkDAO iNodeLinkDao;
    
    @Autowired
    private INodeLinkReverseDao iNodeLinkReverseDao;
    
    @Autowired
    private INodeLinkDynamicDao iNodeLinkDynamicDao;
    
    @Autowired
    private INodeACLService iNodeACLService;
    
    /**
     * 不使用事务,深度优先递归删除
     */
    @Override
    public INode cleanTrash(UserToken user, Long ownerId) throws BaseRunException
    {
        INode root = new INode();
        root.setOwnedBy(ownerId);
        root.setId(INode.FILES_ROOT);
        // 检测权限
        iNodeACLService.vaildINodeOperACL(user, root, AuthorityMethod.PUT_ALL.name());
        long size = 0L;
        long fileCount = 0L;
        root.setStatus(INode.STATUS_TRASH);
        List<INode> iNodeList = iNodeDAOV2.getINodeByStatusInternal(root, null, null, true);
        DeleteSumarry summary = null;
        for (INode temp : iNodeList)
        {
            summary = deleteTrashNode(user.getId(), temp);
            size = size + summary.getSize();
            fileCount = fileCount + summary.getFileCount();
        }
        root.setSize(size);
        root.setFileCount(fileCount);
        fileBaseService.sendINodeEvent(user,
            EventType.TRASH_CLEAR,
            root,
            null,
            UserLogType.CLEAN_TRASH_ASYNC,
            null,
            null);

        return root;
    }
    
    /**
     * 不使用事务,深度优先递归删除
     */
    @Override
    public void deleteTrashItem(UserToken user, INode node) throws BaseRunException
    {
        iNodeACLService.vaildINodeOperACL(user, node, AuthorityMethod.PUT_ALL.name());
        DeleteSumarry summary = deleteTrashNode(user.getId(), node);
        // 发送日志
        node.setSize(summary.getSize());
        node.setFileCount(summary.getFileCount());
        String[] logMsgs = new String[]{String.valueOf(node.getOwnedBy()),
            String.valueOf(node.getParentId())};
        String keyword = StringUtils.trimToEmpty(node.getName());
        fileBaseService.sendINodeEvent(user,
            EventType.TRASH_INODE_DELETE,
            node,
            null,
            UserLogType.DELETE_TRASH_ITEM,
            logMsgs,
            keyword);
            
    }
    
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public FileINodesList listTrashItems(UserToken user, long ownerId, int limit, long offset,
        List<Order> orderList, List<Thumbnail> thumbnailList, boolean withExtraType) throws BaseRunException
    {
        if (null == user)
        {
            LOGGER.error("user is null");
            throw new BadRequestException();
        }
        
        INode filter = new INode();
        filter.setOwnedBy(ownerId);
        filter.setId(INode.FILES_ROOT);
        filter.setStatus(INode.STATUS_TRASH);
        // 权限校验
        iNodeACLService.vaildINodeOperACL(user, filter, AuthorityMethod.PUT_ALL.name());
        
        int total = iNodeDAOV2.getINodeCountByStatus(filter, withExtraType);
        
        List<INode> foldersINode = iNodeDAOV2.getINodeByStatus(filter,
            orderList,
            offset,
            limit,
            withExtraType);
            
        FileINodesList folderLists = new FileINodesList();
        folderLists.setTotalCount(total);
        folderLists.setLimit(limit);
        folderLists.setOffset(offset);
        
        List<INode> folderList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        List<INode> fileList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        
        for (INode tmpNode : foldersINode)
        {
            if (FilesCommonUtils.isFolderType(tmpNode.getType()))
            {
                folderList.add(tmpNode);
            }
            else if (tmpNode.getType() == INode.TYPE_FILE)
            {
                if (FilesCommonUtils.isImage(tmpNode.getName()))
                {
                    fillThumbnailUrl(thumbnailList, ownerId, tmpNode);
                }
                fileList.add(tmpNode);
            }
        }
        
        folderLists.setFolders(folderList);
        folderLists.setFiles(fileList);
        
        String[] logMsgs = new String[]{String.valueOf(ownerId), null};
        String keyword = "Total :" + fileList.size();
        
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.LIST_TRASH_ITEMS,
            logMsgs,
            keyword);
        return folderLists;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void restoreTrashItem(UserToken user, INode itemNode, String newName, INode parentNode)
        throws BaseRunException
    {
        // 获取Inode信息 检测文件状态
        INode node = fileBaseService.getINodeInfoCheckStatus(itemNode.getOwnedBy(),
            itemNode.getId(),
            INode.STATUS_TRASH);
            
        // 获取Inode信息
        INode parentFolder = null;
        if (parentNode != null)
        {
            // 如果不为根目录, 需要检查文件状态
            if (INode.FILES_ROOT != parentNode.getId())
            {
                parentFolder = fileBaseService.getParentINodeInfoCheckStatus(parentNode.getOwnedBy(),
                    parentNode.getId(),
                    INode.STATUS_NORMAL);
                    
                if (!FilesCommonUtils.isFolderType(parentFolder.getType()))
                {
                    String msg = "parentFolder is not folder, ownerid:" + itemNode.getOwnedBy() + ",id:"
                        + itemNode.getId() + "type:" + parentFolder.getType();
                    throw new NoSuchParentException(msg);
                }
            }
            else
            {
                parentFolder = new INode(itemNode.getOwnedBy(), INode.FILES_ROOT);
            }
            
        }
        else
        {
            if (INode.FILES_ROOT != node.getParentId())
            {
                parentFolder = fileBaseService.getParentINodeInfoCheckStatus(node.getOwnedBy(),
                    node.getParentId(),
                    INode.STATUS_NORMAL);
                    
                if (!FilesCommonUtils.isFolderType(parentFolder.getType()))
                {
                    String msg = "parentFolder is not folder, ownerid:" + itemNode.getOwnedBy() + ",id:"
                        + itemNode.getId() + "type:" + parentFolder.getType();
                    throw new NoSuchParentException(msg);
                }
                
            }
            else
            {
                parentFolder = new INode(node.getOwnedBy(), INode.FILES_ROOT);
            }
        }
        
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user, parentFolder, AuthorityMethod.PUT_ALL.name());
        
        if (StringUtils.isNotBlank(newName))
        {
            node.setName(newName);
        }
        
        // 恢复备份根文件夹和盘符文件夹时，如果遇到存在同名的type:0 普通文件夹，将普通文件夹自动重命名
        processSameNodeExistForRestore(user, node, parentFolder);
        
        // 设置同步版本号
        fileBaseService.setNodeSyncStatusAndVersion(user.getId(), node, parentFolder);
        if (node.getType() == INode.TYPE_FILE)
        {
            // 恢复文件
            restoreFileToFolder(user, node, parentFolder, null, node.getSyncVersion());
        }
        else if (FilesCommonUtils.isFolderType(node.getType()))
        {
            // 恢复文件夹
            restoreFolderToFolder(user, node, parentFolder, null, node.getSyncVersion());
        }
        
        // 重命名共享节点
        if (StringUtils.isNotBlank(newName))
        {
            nodeMessageService.notifyShareToUpdateMsg(node,user.getId());
        }
        
        String[] logMsgs = new String[]{String.valueOf(node.getOwnedBy()),
            String.valueOf(node.getParentId())};
        String keyword = StringUtils.trimToEmpty(node.getName());
        // 发送日志
        fileBaseService.sendINodeEvent(user,
            EventType.TRASH_INODE_RECOVERY,
            node,
            parentFolder,
            UserLogType.RESTORE_TRASH_ITEM,
            logMsgs,
            keyword);
    }
    
    private void processSameNodeExistForRestore(UserToken user, INode node, INode parentInode)
    {
        List<INode> list = iNodeDAO.getNodeByName(parentInode.getOwnedBy(),
            parentInode.getId(),
            node.getName());
        if (CollectionUtils.isNotEmpty(list))
        {
            if (!FilesCommonUtils.isBackupFolderType(node.getType()))
            {
                LOGGER.error("ConflictException，Name:" + node.getName());
                throw new FilesNameConflictException();
            }
            // 是否有备份文件夹
            boolean isExixt = false;
            for (INode item : list)
            {
                if (FilesCommonUtils.isBackupFolderType(item.getType()))
                {
                    isExixt = true;
                    break;
                }
            }
            if (isExixt)
            {
                LOGGER.error("ConflictException，Name:" + node.getName());
                throw new FilesNameConflictException();
            }
            Date date = new Date();
            int renameNumber = 1;
            String newName;
            for (INode item : list)
            {
                newName = FilesCommonUtils.getNewName(INode.TYPE_FILE, item.getName(), renameNumber);
                while (fileBaseService.isSameNameNodeExist(item.getOwnedBy(), item.getParentId(), newName))
                {
                    renameNumber++;
                    newName = FilesCommonUtils.getNewName(INode.TYPE_FILE, item.getName(), renameNumber);
                }
                fileBaseService.setNodeSyncVersion(item.getOwnedBy(), item);
                item.setName(newName);
                item.setModifiedAt(date);
                item.setModifiedBy(user.getId());
                
                iNodeDAO.updateNameAndSyncVersion(item);
                nodeMessageService.notifyShareToUpdateMsg(item,user.getId());
                renameNumber = 1;
            }
        }
    }
    
    private INode restoreFileToFolder(UserToken user, INode fileNode, INode folderNode, String name,
        long syncNum) throws BaseRunException
    {
        
        if (StringUtils.isNotBlank(name))
        {
            fileNode.setName(name);
        }
        
        // 同步版本号
        fileNode.setParentId(folderNode.getId());
        fileNode.setStatus(INode.STATUS_NORMAL);
        
        // 更新同步版本号
        fileNode.setSyncVersion(syncNum);
        
        fileNode.setModifiedBy(user.getId());
        
        // 更新文件及其版本的的状态
        iNodeDAO.updateForRestore(fileNode);
        iNodeDAO.updateSubINodeStatus(fileNode);
        
        // 恢复共享状态
        nodeMessageService.notifyRestoreShareMsg(fileNode,user.getId());
        
        return fileNode;
        
    }
    
    private INode restoreFolderToFolder(UserToken user, INode srcNode, INode folderNode, String name,
        long syncNum) throws BaseRunException
    {
        // 更新自己状态及其子状态
        srcNode.setStatus(INode.STATUS_NORMAL);
        srcNode.setParentId(folderNode.getId());
        
        // 更新同步版本号
        srcNode.setSyncVersion(syncNum);
        
        srcNode.setModifiedBy(user.getId());
        
        if (StringUtils.isNotBlank(name))
        {
            srcNode.setName(name);
        }
        
        iNodeDAO.updateForRestore(srcNode);
        
        List<INode> inodelist = iNodeDAO.getINodeByParent(srcNode, null, null);
        for (INode tmp : inodelist)
        {
            // 对节点状态更新,子文件是否更新，STATUS_DELETE 不能还原
            if (tmp.getStatus() == INode.STATUS_TRASH_DELETE)
            {
                if (tmp.getType() == INode.TYPE_FILE)
                {
                    restoreFileToFolder(user, tmp, srcNode, null, syncNum);
                }
                else if (FilesCommonUtils.isFolderType(tmp.getType()))
                {
                    restoreFolderToFolder(user, tmp, srcNode, null, syncNum);
                }
                else
                {
                    continue;
                }
            }
        }
        
        // 恢复共享状态
        nodeMessageService.notifyRestoreShareMsg(srcNode,user.getId());
        
        return srcNode;
    }
    
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
    
    /**
     * 删除回收站中的子节点,须先判断节点状态
     * 
     * @param node
     * @return
     */
    private DeleteSumarry deleteTrashSubNode(long operatorId, INode node)
    {
        DeleteSumarry summary = new DeleteSumarry();
        if (node.getStatus() == INode.STATUS_DELETE)
        {
            // 已经处于彻底删除状态
            return summary;
        }
        if (node.getStatus() == INode.STATUS_TRASH)
        {
            // 该节点自身是回收站中的一级节点,不能删除
            return summary;
        }
        if (node.getType() == INode.TYPE_VERSION)
        {
            // 版本节点,直接删除
            updateNodeToRealDeleted(operatorId, node);
            summary.setFileCount(1);
            summary.setSize(node.getSize());
            return summary;
        }
        return deleteTrashNode(operatorId, node);
    }
    
    /**
     * 深度优先遍历删除节点
     * 
     * @param node
     * @return true标识本节点彻底删除，false标识本节点回收站不可见
     */
    private DeleteSumarry deleteTrashNode(long operatorId, INode node)
    {
        Limit limit = new Limit();
        long offset = 0;
        List<INode> children = null;
        DeleteSumarry summary = new DeleteSumarry();
        DeleteSumarry tmpSum = null;
        while (true)
        {
            limit.setOffset(offset);
            limit.setLength(FILE_LIST_LIMIT);
            children = iNodeDAO.getINodeByParentInternal(node, null, limit);
            for (INode child : children)
            {
                tmpSum = deleteTrashSubNode(operatorId, child);
                summary.setFileCount(summary.getFileCount() + tmpSum.getFileCount());
                summary.setSize(summary.getSize() + tmpSum.getSize());
            }
            if (children.size() < FILE_LIST_LIMIT)
            {
                break;
            }
            offset = offset + FILE_LIST_LIMIT;
        }
        clearNodeToRealDeleted(operatorId, node);
        summary.setFileCount(summary.getFileCount() + 1);
        summary.setSize(summary.getSize() + node.getSize());
        return summary;
    }
    
    /**
     * 将节点设置为删除状态或回收站不可见状态，具体状态由node决定，同时清除该节点所有共享、外链、权限数据
     * 
     * @param node
     */
    private void clearNodeToRealDeleted(long operatorId, INode node)
    {
        deleteAllShare(node,operatorId);
        deleteNodeAcl(node);
        deleteAllLinkByNode(node);
        updateNodeToRealDeleted(operatorId, node);
        if (!FilesCommonUtils.isFolderType(node.getType()))
        {
            recordingDeletedFilesService.put(new FilesDelete(node.getOwnedBy(), node.getId(),
                userDao.get(node.getOwnedBy()).getAccountId(), node.getSize()));
        }
    }
    
    /**
     * 将节点设置为删除状态或回收站不可见状态，具体状态由node决定
     * 
     * @param node
     */
    private void updateNodeToRealDeleted(long operatorId, INode node)
    {
        node.setStatus(INode.STATUS_DELETE);
        node.setModifiedAt(new Date());
        node.setModifiedBy(operatorId);
        node.setShareStatus(INode.SHARE_STATUS_UNSHARED);
        iNodeDAO.updateStatusToRealDelete(node);
    }
    
    private void deleteNodeAcl(INode inode)
    {
        try
        {
            iNodeACLDAO.deleteByResource(inode.getOwnedBy(), inode.getId());
        }
        catch (Exception e)
        {
            LOGGER.warn("Can not remove acl for node " + inode.getOwnedBy() + '/' + inode.getId(), e);
        }
    }
    
    private void deleteAllShare(INode inode,long operatorId)
    {
        List<INodeShare> shareList = shareDAO.getShareListIgnoreStatus(inode.getOwnedBy(),operatorId, inode.getId());
        if (CollectionUtils.isEmpty(shareList))
        {
            return;
        }
        for (INodeShare tempShare : shareList)
        {
            deleteShareToMe(tempShare);
        }
        try
        {
            shareDAO.deleteByInode(inode.getOwnedBy(),operatorId, inode.getId());
        }
        catch (Exception e)
        {
            LOGGER.warn("Can not remove share for node " + inode.getOwnedBy() + '/' + inode.getId(), e);
        }
    }
    
    private void deleteShareToMe(INodeShare shareToMe)
    {
        try
        {
            shareToMeDAO.deleteByInode(shareToMe);
        }
        catch (Exception e)
        {
            LOGGER.warn("Can not remove sharetome for node " + shareToMe.getOwnerId() + '/'
                + shareToMe.getiNodeId() + '/' + shareToMe.getSharedUserId(), e);
        }
    }
    
    private void deleteAllLinkByNode(INode node)
    {
        if (StringUtils.isNotBlank(node.getLinkCode()) && !INode.LINKCODE_NEW_SET.equals(node.getLinkCode()))
        {
            INodeLink linkToDelete = new INodeLink();
            linkToDelete.setId(node.getLinkCode());
            iNodeLinkDao.delete(linkToDelete);
        }
        List<INodeLink> items = iNodeLinkReverseDao.listByNode(node.getOwnedBy(), node.getId(), null);
        for (INodeLink item : items)
        {
            iNodeLinkDynamicDao.deleteAll(item.getId());
            iNodeLinkDao.deleteV2(item);
        }
        iNodeLinkReverseDao.deleteByNode(node.getOwnedBy(), node.getId(), null);
    }
}
