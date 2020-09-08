/**
 * 文件夹service
 */
package com.huawei.sharedrive.app.files.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.exception.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.dao.INodeDAOV2;
import com.huawei.sharedrive.app.files.domain.FileBasicConfig;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.files.service.NodeCopyService;
import com.huawei.sharedrive.app.files.service.NodeMessageService;
import com.huawei.sharedrive.app.files.service.NodeUpdateService;
import com.huawei.sharedrive.app.files.service.TrashServiceV2;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.box.domain.Limit;

/**
 * 目录相关操作业务实现类
 * 
 * @version CloudStor CSE Service Platform Subproject, 2014-8-20
 * @see
 * @since
 */
@Component
public class FolderServiceImpl implements FolderService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FolderServiceImpl.class);
    
    /** 当前只支持名称，大小，修改时间排序 */
    private static final String ORDERBY_PRO = "name;size;modifiedAt";
    
    @Autowired
    private FileBaseService fileBaseService; // ② 表示代理对象，不是目标对象
    
    @Autowired
    private NodeMessageService nodeMessageService;
    
    @Autowired
    private INodeACLService iNodeACLService;
    
    @Autowired
    private NodeUpdateService nodeUpdateService;
    
    @Autowired
    private NodeCopyService nodeCopyService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private INodeDAOV2 iNodeDAOV2;
    
    @Autowired
    private TrashServiceV2 trashService;
    
    
    @Override
    public INode cancelFolderSync(UserToken user, INode folderNode) throws BaseRunException
    {
        if (null == user || null == folderNode)
        {
            LOGGER.error("user filternode  or parentNode is null");
            throw new BadRequestException();
        }
        
        // 获取Inode信息
        INode curInode = fileBaseService.getINodeInfoCheckStatus(folderNode.getOwnedBy(),
            folderNode.getId(),
            INode.STATUS_NORMAL);
        
        // 检查文件类型
        if (INode.SYNC_STATUS_SETTED != curInode.getSyncStatus())
        {
            LOGGER.error("inode  not sysStatus ,ownerid:" + curInode.getOwnedBy() + ",id:" + curInode.getId()
                + ",sysStatus:" + curInode.getSyncStatus());
            throw new BadRequestException();
        }
        
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user, curInode, AuthorityMethod.PUT_ALL.name());
        
        // 取消同步标记位
        INode node = updateFolderSyncStatus(user, curInode.getOwnedBy(), curInode, INode.SYNC_STATUS_UNSET);
        String[] logMsgs = new String[]{node.getName(), String.valueOf(node.getParentId())};
        String keyword = node.getName();
        
        fileBaseService.sendINodeEvent(user,
            EventType.INODE_UPDATE_SYNC,
            node,
            null,
            UserLogType.CANCLE_INODE_SYNC,
            logMsgs,
            keyword);
        
        return node;
    }
    
    @Override
    public INode copyNodeToFolder(UserToken user, INode srcNode, INode destNode, String newName,
        boolean valiLinkAccessCode) throws BaseRunException
    {
        return copyNodeToFolderCheckType(user, srcNode, destNode, newName, INode.TYPE_ALL, valiLinkAccessCode);
    }
    
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public INode copyNodeToFolderCheckType(UserToken user, INode src, INode dest, String newName, int type,
        boolean valiLinkAccessCode) throws BaseRunException
    {
        // 检查源节点有效性
        INode srcNode = getAndCheckSrcNode(user, src, type);
        // 检查目的节点有效性
        INode destParent = getAndCheckDestNode(dest);
        checkCopyConflict(srcNode, destParent);
        iNodeACLService.vaildINodeOperACL(user, destParent, AuthorityMethod.UPLOAD_OBJECT.name());
        iNodeACLService.vaildINodeOperACL(user, srcNode, AuthorityMethod.GET_ALL.name(), valiLinkAccessCode);
        
        // 检查目标路径下是否存在同名文件
        if (StringUtils.isNotBlank(newName))
        {
            srcNode.setName(newName);
        }
        checkNameConflict(srcNode, destParent);
        
        checkNodeTypeForCopyAndMove(srcNode, destParent);
        
        if (srcNode.getType() == INode.TYPE_FILE)
        {
            String[] logMsgs = new String[]{String.valueOf(srcNode.getOwnedBy()),
                String.valueOf(srcNode.getId()), String.valueOf(destParent.getOwnedBy()),
                String.valueOf(destParent.getId())};
            // 设置同步版本号
            fileBaseService.setNodeSyncStatusAndVersion(destParent.getOwnedBy(), srcNode, destParent);
            // 复制文件
            INode node = nodeCopyService.copyFileNodeToFolder(user, srcNode, destParent);
            if (null == node)
            {
                String message = "Src node type invalid, owner id:" + srcNode.getOwnedBy() + ", node id:"
                    + srcNode.getId() + ",type:" + srcNode.getType();
                throw new NoSuchSourceException(message);
            }
            
            String keyword = StringUtils.trimToEmpty(srcNode.getName());
            fileBaseService.sendINodeEvent(user,
                EventType.INODE_COPY,
                node,
                destParent,
                UserLogType.COPY_FILE,
                logMsgs,
                keyword);
            return node;
        }
        else if (FilesCommonUtils.isFolderType(srcNode.getType()))
        {
            // 发送日志
            String[] logMsgs = new String[]{String.valueOf(srcNode.getOwnedBy()),
                String.valueOf(srcNode.getId()), String.valueOf(destParent.getOwnedBy()),
                String.valueOf(destParent.getId())};
            // 设置同步版本号
            fileBaseService.setNodeSyncStatusAndVersion(destParent.getOwnedBy(), srcNode, destParent);
            
            // 复制文件夹并获取统计信息
            INode node = nodeCopyService.copyFolderNodeToFolder(user, srcNode, destParent);
            
            srcNode.setSize(srcNode.getSize() + node.getSize());
            srcNode.setFileCount(srcNode.getFileCount() + node.getFileCount());
            
            String keyword = StringUtils.trimToEmpty(node.getName());
            fileBaseService.sendINodeEvent(user,
                EventType.INODE_COPY,
                srcNode,
                destParent,
                UserLogType.COPY_FOLDER,
                logMsgs,
                keyword);
            return node;
        }
        else
        {
            String message = "Src node type invalid, owner id:" + srcNode.getOwnedBy() + ", node id:"
                + srcNode.getId() + ",type:" + srcNode.getType();
            throw new NoSuchSourceException(message);
        }
        
    }
    
    private void checkNodeTypeForCopyAndMove(INode srcNode, INode destParent)
    {
        if(destParent.getType()!=null){
            if (srcNode.getType() == INode.TYPE_BACKUP_COMPUTER || srcNode.getType() == INode.TYPE_BACKUP_DISK)
            {
                String message = "Src node type not allowed, owner id:" + srcNode.getOwnedBy() + ", node id:"
                        + srcNode.getId() + ",type:" + srcNode.getType();
                throw new ForbiddenException(message);
            }
            if (destParent.getType() == INode.TYPE_BACKUP_COMPUTER)
            {
                String message = "dest node type not allowed, owner id:" + destParent.getOwnedBy() + ", node id:"
                        + destParent.getId() + ",type:" + destParent.getType();
                throw new ForbiddenException(message);
            }
            if (destParent.getType() == INode.TYPE_BACKUP_EMAIL)
            {
                String message = "Email dest node type not allowed, owner id:" + destParent.getOwnedBy()
                        + ", node id:" + destParent.getId() + ",type:" + destParent.getType();
                throw new ForbiddenException(message);
            }
        }

    }
    
    @Override
    public INode createFolder(UserToken user, INode folderNode) throws BaseRunException
    {
        return createFolder(user, folderNode, false);
    }
    
    @Override
    public INode createFolder(UserToken user, INode folderNode, boolean autoMerge) throws BaseRunException
    {
        if (null == user || null == folderNode)
        {
            String msg = "user or node is null";
            throw new BadRequestException(msg);
        }
        if(folderNode.getType()==null){
            folderNode.setType(INode.TYPE_FOLDER);
        }
        INode parentInode = fileBaseService.getAndCheckParentNode(folderNode);
        
        // 不过滤根节点的权限判断，需要兼顾团队空间
        iNodeACLService.vaildINodeOperACL(user, parentInode, AuthorityMethod.PUT_CREATE.name());
        
        checkBackUpFolderForCreate(folderNode, parentInode);
        
        List<INode> list = getSameNodeExist(folderNode, parentInode);
        // 创建根文件夹时，如果遇到存在同名的type:0 普通文件夹，将普通文件夹自动重命名
        if (CollectionUtils.isNotEmpty(list))
        {
            if (FilesCommonUtils.isBackupFolderType(folderNode.getType()) && !isBackUPFolderExist(list))
            {
                renameNodeList(user, list);
                doCreateFolder(user, folderNode, parentInode);
            }
            else if (FilesCommonUtils.isEmailBackupFolderType(folderNode.getType())
                && !isEmailBackUPFolderExist(list))
            {
                renameNodeList(user, list);
                doCreateFolder(user, folderNode, parentInode);
            }
            else if (autoMerge)
            {
                folderNode = getFolderFromSameNodeExist(list, folderNode.getType());
                // 如果没有重名的文件夹也返回409异常
                if (folderNode == null)
                {
                    throw new FilesNameConflictException();
                }
            }
            else
            {
                // 不支持文件夹合并,需要返回异常
                throw new FilesNameConflictException();
            }
        }
        else
        {
            doCreateFolder(user, folderNode, parentInode);
        }
        
        // 发送日志
        String[] logMsgs = new String[]{String.valueOf(folderNode.getOwnedBy()),
            String.valueOf(folderNode.getParentId())};
        String keyword = StringUtils.trimToEmpty(folderNode.getName());
        
        fileBaseService.sendINodeEvent(user,
            EventType.INODE_CREATE,
            folderNode,
            null,
            UserLogType.CREATE_FOLDER,
            logMsgs,
            keyword);
        
        return folderNode;
    }
    
    private void doCreateFolder(UserToken user, INode folderNode, INode parentInode)
    {
        fileBaseService.setNodeSyncStatusAndVersion(parentInode.getOwnedBy(), folderNode, parentInode);
        // 设置文件夹类型
        folderNode.setId(fileBaseService.buildINodeID(parentInode.getOwnedBy()));
        
        folderNode.setCreatedBy(user.getId());
        Date date = new Date();
        folderNode.setCreatedAt(date);
        folderNode.setModifiedAt(date);
        folderNode.setModifiedBy(user.getId());
        
        fileBaseService.createNode(folderNode);
    }
    
    private void checkBackUpFolderForCreate(INode node, INode parentNode)
    {
        checkEmailType(node, parentNode);
        
        // 非根目录
        if (INode.FILES_ROOT != node.getParentId())
        {
            if (INode.TYPE_BACKUP_COMPUTER == node.getType())
            {
                LOGGER.error("can not create TYPE_BACKUP_COMPUTER in no root folder , ownerId:"
                    + node.getOwnedBy() + ", id:" + node.getParentId() + ",type: " + parentNode.getType());
                throw new ForbiddenException();
            }
            // 检测类型和状态
            if (INode.TYPE_BACKUP_DISK == node.getType())
            {
                if (INode.TYPE_BACKUP_COMPUTER != parentNode.getType())
                {
                    LOGGER.error("can not create TYPE_BACKUP_DISK in no TYPE_BACKUP_COMPUTER folder , ownerId:"
                        + node.getOwnedBy() + ", id:" + node.getParentId() + ",type: " + parentNode.getType());
                    throw new ForbiddenException();
                }
            }
            if (INode.TYPE_FOLDER == node.getType())
            {
                if (INode.TYPE_BACKUP_COMPUTER == parentNode.getType())
                {
                    LOGGER.error("can not create TYPE_FOLDER in TYPE_BACKUP_COMPUTER folder , ownerId:"
                        + node.getOwnedBy() + ", id:" + node.getParentId() + ",type: " + parentNode.getType());
                    throw new ForbiddenException();
                }
            }
        }
        else
        {
            if (INode.TYPE_BACKUP_DISK == node.getType())
            {
                LOGGER.error("can not create TYPE_BACKUP_DISK in no TYPE_BACKUP_COMPUTER folder , ownerId:"
                    + node.getOwnedBy() + ", id:" + node.getParentId() + ",type: " + parentNode.getType());
                throw new ForbiddenException();
            }
        }
        
    }
    
    private void checkEmailType(INode node, INode parentNode)
    {
        if (INode.TYPE_BACKUP_EMAIL != node.getType())
        {
            return;
        }
        boolean isNormalEmailFolder = false;
        if (INode.FILES_ROOT == node.getParentId())
        {
            isNormalEmailFolder = true;
        }
        else if (parentNode.getType() == INode.TYPE_BACKUP_EMAIL)
        {
            isNormalEmailFolder = true;
        }
        if (!isNormalEmailFolder)
        {
            String errorInfo = "can not create TYPE_BACKUP_EMAIL in no root folder , ownerId:"
                + node.getOwnedBy() + ", id:" + node.getParentId() + ",type: " + parentNode.getType();
            throw new ForbiddenException(errorInfo);
        }
    }
    
    private List<INode> getSameNodeExist(INode folderNode, INode parentInode)
    {
        List<INode> list = iNodeDAO.getNodeByName(parentInode.getOwnedBy(),
            parentInode.getId(),
            folderNode.getName());
        return list;
    }
    
    private INode getFolderFromSameNodeExist(List<INode> list, byte type)
    {
        INode exixtFolder = null;
        for (INode item : list)
        {
            if (item.getType() == type)
            {
                exixtFolder = item;
                break;
            }
        }
        return exixtFolder;
    }
    
    private boolean isBackUPFolderExist(List<INode> list)
    {
        boolean isExixt = false;
        for (INode item : list)
        {
            if (INode.TYPE_BACKUP_COMPUTER == item.getType() || INode.TYPE_BACKUP_DISK == item.getType())
            {
                isExixt = true;
                break;
            }
        }
        return isExixt;
    }
    
    private boolean isEmailBackUPFolderExist(List<INode> list)
    {
        boolean isExixt = false;
        for (INode item : list)
        {
            if (INode.TYPE_BACKUP_EMAIL == item.getType())
            {
                isExixt = true;
                break;
            }
        }
        return isExixt;
    }
    
    private void renameNodeList(UserToken user, List<INode> list)
    {
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
    
    @Override
    public void deleteCreatingNode(long ownerID, String objectID) throws BaseRunException
    {
        fileBaseService.deleteCreatingNode(ownerID, objectID);
    }
    
    @Override
    public INode getNodeInfo(UserToken user, long ownerId, long nodeID) throws BaseRunException
    {
        return getNodeInfoCheckType(user, ownerId, nodeID, INode.TYPE_ALL);
    }
    
    @Override
    public INode getNodeInfoCheckType(UserToken user, long ownerId, long nodeID, int type)
        throws BaseRunException
    {
        INode fNode = fileBaseService.getINodeInfo(ownerId, nodeID);
        if (null == fNode)
        {
            LOGGER.error("inode is null,ownerid:" + ownerId + ",id:" + nodeID);
            return null;
        }
//        iNodeACLService.vaildINodeOperACL(user, fNode, AuthorityMethod.GET_INFO.name());
        
        if (fNode.getStatus() != INode.STATUS_NORMAL)
        {
            LOGGER.error("INode is not normal status,ownerid:" + fNode.getOwnedBy() + ",id:" + fNode.getId()
                + " ,getStatus:" + fNode.getStatus());
            throw new NoSuchItemsException();
        }
        doCheckNodeType(fNode, type);
        
        return fNode;
    }
    
    @Override
    public INode getNodeInfoCheckType(UserToken user, long ownerId, long parentNodeId, String nodeName,
        int type) throws BaseRunException
    {
        INode fNode = fileBaseService.getINodeInfo(ownerId, parentNodeId, nodeName);
        if (null == fNode)
        {
            LOGGER.error("inode is null,ownerid:" + ownerId + ", nodeName:" + nodeName);
            return null;
        }
        iNodeACLService.vaildINodeOperACL(user, fNode, AuthorityMethod.GET_INFO.name());
        
        if (fNode.getStatus() != INode.STATUS_NORMAL)
        {
            String msg = "INode is not normal status,ownerid:" + fNode.getOwnedBy() + ",id:" + fNode.getId()
                + " ,getStatus:" + fNode.getStatus();
            throw new NoSuchItemsException(msg);
        }
        doCheckNodeType(fNode, type);
        
        return fNode;
    }
    
    private void doCheckNodeType(INode fNode, int type)
    {
        if (fNode.getType() == INode.TYPE_VERSION)
        {
            LOGGER.error("node type error,ownerid:" + fNode.getOwnedBy() + ",id:" + fNode.getId() + ", type:"
                + fNode.getType());
            throw new NoSuchItemsException();
        }
        if (INode.TYPE_ALL != type)
        {
            if (INode.TYPE_FOLDER_ALL == type)
            {
                if (INode.TYPE_FOLDER != fNode.getType() && INode.TYPE_BACKUP_COMPUTER != fNode.getType()
                    && INode.TYPE_BACKUP_DISK != fNode.getType()
                    && INode.TYPE_BACKUP_EMAIL != fNode.getType())
                {
                    LOGGER.error("node type error,ownerid:" + fNode.getOwnedBy() + ",id:" + fNode.getId()
                        + ", type:" + fNode.getType());
                    
                    throw new NoSuchItemsException();
                }
            }
            else
            {
                if (fNode.getType() != type)
                {
                    LOGGER.error("node type error,ownerid:" + fNode.getOwnedBy() + ",id:" + fNode.getId()
                        + ", type:" + fNode.getType());
                    
                    throw new NoSuchItemsException();
                }
            }
        }
        
    }
    
    @Override
    public INode getNodeInfoCheckTypeV2(long ownerId, long nodeID) throws BaseRunException
    {
        INode fNode = fileBaseService.getINodeInfo(ownerId, nodeID);
        if (null == fNode)
        {
            LOGGER.error("inode is null,ownerid:" + ownerId + ",id:" + nodeID);
            return null;
        }
        
        if (fNode.getStatus() != INode.STATUS_NORMAL)
        {
            String message = "INode is not normal status,ownerid:" + fNode.getOwnedBy() + ",id:"
                + fNode.getId() + " ,getStatus:" + fNode.getStatus();
            throw new NoSuchItemsException(message);
        }
        String[] logMsgs = new String[]{StringUtils.trimToEmpty(fNode.getLinkCode()),
            String.valueOf(ownerId), String.valueOf(fNode.getParentId())};
        String keyword = fNode.getName();
        fileBaseService.sendINodeEvent(null,
            EventType.OTHERS,
            fNode,
            null,
            UserLogType.GET_NODEINOF_LINK,
            logMsgs,
            keyword);
        return fNode;
    }
    
    @Override
    public INode getNodeNoCheckStatus(UserToken user, long ownerId, long nodeID) throws BaseRunException
    {
        INode fNode = fileBaseService.getINodeInfo(ownerId, nodeID);
        if (null == fNode)
        {
            LOGGER.error("inode is null,ownerid:" + ownerId + ",id:" + nodeID);
            throw new NoSuchItemsException();
        }
        iNodeACLService.vaildINodeOperACL(user, fNode, AuthorityMethod.GET_INFO.name());
        
        return fNode;
    }
    
    @Override
    public FileINodesList listNodesbyFilter(UserToken user, INode filter, OrderV1 order, Limit limit)
        throws BaseRunException
    {
        if (null == user || null == filter)
        {
            LOGGER.error("user or node is null");
            throw new BadRequestException();
        }
        // 检测order合法性
        checkVaildListFoldersOrderby(order);
        
        long userId = user.getId();
        
        // 父Inode如果不是根节点需要判断状态 类型等
        if (filter.getId() != INode.FILES_ROOT)
        {
            INode folderNode = fileBaseService.getINodeInfoCheckStatus(filter.getOwnedBy(),
                filter.getId(),
                INode.STATUS_NORMAL);
            
            // 检查文件类型
            if (!FilesCommonUtils.isFolderType(folderNode.getType()))
            {
                LOGGER.error("folderNode is not a folder,ownerid:" + folderNode.getOwnedBy() + ",id:"
                    + folderNode.getId() + ",type:" + folderNode.getType());
                throw new NoSuchItemsException();
            }
            
            // 权限检测
            userId = iNodeACLService.vaildINodeOperACL(user, folderNode, AuthorityMethod.GET_INFO.name());
        }
        else
        {
            // 根节点不允许其他用户访问
            if (user.getId() != filter.getOwnedBy())
            {
                LOGGER.error("not allow other user to list the root folder，userid:" + user.getId()
                    + ",ownerid:" + filter.getOwnedBy());
                throw new AuthFailedException();
            }
            
        }
        
        int total = iNodeDAO.getSubINodeCount(filter);
        
        FileINodesList folderLists = new FileINodesList();
        folderLists.setTotalCount(total);
        folderLists.setLimit(limit.getLength());
        folderLists.setOffset(limit.getOffset());
        
        List<INode> folderList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        List<INode> fileList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        
        List<INode> foldersINode = iNodeDAO.getINodeByParentAndStatus(filter, order, limit);
        
        for (INode tmpNode : foldersINode)
        {
            
            if (FilesCommonUtils.isFolderType(tmpNode.getType()))
            {
                folderList.add(tmpNode);
            }
            else if (tmpNode.getType() == INode.TYPE_FILE)
            {
                fillThumbnailUrl(userId, tmpNode);
                fileList.add(tmpNode);
            }
        }
        
        folderLists.setFolders(folderList);
        folderLists.setFiles(fileList);
        
        return folderLists;
    }
    
    @Override
    public List<INode> listSubFolderNoders(UserToken user, INode node) throws BaseRunException
    {
        if (null == user || null == node)
        {
            LOGGER.error("user, inode is null");
            throw new InvalidParamException();
        }
        
        if (node.getId() != INode.FILES_ROOT)
        {
            node = fileBaseService.getAndCheckNode(node.getOwnedBy(), node.getId(), INode.TYPE_FOLDER_ALL);
        }
        
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user, node, AuthorityMethod.GET_INFO.name());
        
        return iNodeDAO.getSubINodeByTypeAndStatus(node);
        
    }
    
    @Override
    public INode moveNodeToFolder(UserToken user, INode iNode, INode parentNode, String newName)
        throws BaseRunException
    {
        return moveNodeToFolderCheckType(user, iNode, parentNode, newName, INode.TYPE_ALL);
    }
    
    @Override
    public INode moveNodeToFolderCheckType(UserToken user, INode iNode, INode parentNode, String newName,
        int type) throws BaseRunException
    {
        if (null == user || null == iNode)
        {
            LOGGER.error("user, inode is null");
            throw new BadRequestException();
        }
        // 检查源节点有效性
        INode srcNode = getAndCheckSrcNode(user, iNode, type);
        
        // 检查目的节点有效性
        INode destNode = getAndCheckDestNode(parentNode);
        
        String[] logMsgs = new String[]{String.valueOf(srcNode.getOwnedBy()),
            String.valueOf(srcNode.getId()), String.valueOf(destNode.getOwnedBy()),
            String.valueOf(destNode.getId())};
        
//        // 不支持跨用户移动文件
//        if (srcNode.getOwnedBy() != destNode.getOwnedBy())
//        {
//            throw new ForbiddenException();
//        }
//        
        checkMoveConflict(srcNode, destNode);
        
        // 检测目标目录 权限检测
        iNodeACLService.vaildINodeOperACL(user, destNode, AuthorityMethod.UPLOAD_OBJECT.name());
        
        // 检测元目录
        iNodeACLService.vaildINodeOperACL(user, srcNode, AuthorityMethod.DELETE_ALL.name());
        
        // 检测目标路径下是否存在同名文件
        if (StringUtils.isNotBlank(newName))
        {
            srcNode.setName(newName);
        }
        checkNameConflict(srcNode, destNode);
        
        checkNodeTypeForCopyAndMove(srcNode, destNode);
        // 同用户移动，只需要修改parenetid和名称.
        // 移动到非根目录下, 需要设置为未同步状态
        INode nNode=null;
        fileBaseService.setNodeSyncStatusAndVersion(destNode.getOwnedBy(), srcNode, destNode);
        if(srcNode.getOwnedBy()!=destNode.getOwnedBy()){
        	if(srcNode.getType()<1){
        		nNode = nodeCopyService.copyFolderNodeToFolder(user, srcNode, destNode);
        	}else{
        		nNode = nodeCopyService.copyFileNodeToFolder(user, srcNode, destNode);
        	}
        	fileBaseService.deleteINode(srcNode);
//        	trashService.deleteTrashItem(user, srcNode);
        }else{
        	nNode = fileBaseService.moveBaseNodeToFolder(user.getId(), srcNode, destNode);
        }
        
        // 重命名共享节点
        if (StringUtils.isNotBlank(newName))
        {
            nodeMessageService.notifyShareToUpdateMsg(nNode,user.getId());
        }
        
        // 发送日志
        String keyword = StringUtils.trimToEmpty(nNode.getName());
        UserLogType logType = UserLogType.MOVE_FILE;
        if (FilesCommonUtils.isFolderType(type))
        {
            logType = UserLogType.MOVE_FOLDER;
        }
        fileBaseService.sendINodeEvent(user, EventType.INODE_MOVE, nNode, destNode, logType, logMsgs, keyword);
        
        return nNode;
        
    }
    
    
    
    
    @Override
	public INode moveNodeToFolderNoCheck(UserToken user, INode iNode, INode parentNode, String newName, int type) throws BaseRunException {
		// 检查源节点有效性
		INode srcNode = fileBaseService.getINodeInfo(iNode.getOwnedBy(), iNode.getId());

		// 检查目的节点有效性
		INode destNode = getAndCheckDestNode(parentNode);

		String[] logMsgs = new String[] { String.valueOf(srcNode.getOwnedBy()), String.valueOf(srcNode.getId()), String.valueOf(destNode.getOwnedBy()), String.valueOf(destNode.getId()) };
		newName = checkAndRename(srcNode, destNode,newName);
		srcNode.setName(newName);
		checkNodeTypeForCopyAndMove(srcNode, destNode);

		INode nNode = null;
		fileBaseService.setNodeSyncStatusAndVersion(destNode.getOwnedBy(), srcNode, destNode);
		if (srcNode.getOwnedBy() != destNode.getOwnedBy()) {
			if (srcNode.getType() < 1) {
				nNode = nodeCopyService.copyFolderNodeToFolder(user, srcNode, destNode);
			} else {
				nNode = nodeCopyService.copyFileNodeToFolder(user, srcNode, destNode);
			}
			fileBaseService.deleteINode(srcNode);
		} else {
			nNode = fileBaseService.moveBaseNodeToFolder(user.getId(), srcNode, destNode);
		}

		// 重命名共享节点
		if (StringUtils.isNotBlank(newName)) {
			nodeMessageService.notifyShareToUpdateMsg(nNode, user.getId());
		}

		return nNode;

	}
    
    @SuppressWarnings("unused")
	private String checkAndRename(INode srcNode,INode destNode,String newName){
    	int renameNumber = 1;
		try {
			checkMoveConflict(srcNode, destNode);
			return newName;
		} catch (FilesNameConflictException e) {
			newName = FilesCommonUtils.getNewName(srcNode.getType(), newName, renameNumber);
			renameNumber++;
			return checkAndRename(srcNode,destNode,newName);
		}
    }
    
    
    @Override
    public INode renameNode(UserToken user, INode inode, String name) throws BaseRunException
    {
        return renameNodeCheckType(user, inode, name, INode.TYPE_ALL);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INode renameNodeCheckType(UserToken user, INode inode, String name, int type)
        throws BaseRunException
    {
        if (null == user || null == inode)
        {
            LOGGER.error("user, inode is null");
            throw new BadRequestException();
        }
        
        if (StringUtils.isBlank(name))
        {
            LOGGER.error("name is blank");
            throw new BadRequestException();
        }
        
        INode renameNode = fileBaseService.getINodeInfoCheckStatus(inode.getOwnedBy(),
            inode.getId(),
            INode.STATUS_NORMAL);
        
        checkNodeTypeForRename(renameNode, type);
        
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user, renameNode, AuthorityMethod.PUT_RENAME.name());
        
        // 检测是否有重名的
        if (fileBaseService.isSameNameNodeExistNoSelf(renameNode.getOwnedBy(),
            renameNode.getParentId(),
            renameNode.getId(),
            name))
        {
            LOGGER.info("ConflictException,rename the file");
            throw new FilesNameConflictException();
        }
        
        // 只更新同步版本号
        fileBaseService.setNodeSyncVersion(renameNode.getOwnedBy(), renameNode);
        renameNode.setName(name);
        renameNode.setModifiedAt(new Date());
        renameNode.setModifiedBy(user.getId());
        
        iNodeDAO.updateNameAndSyncVersion(renameNode);
        
        nodeMessageService.notifyShareToUpdateMsg(renameNode,user.getId());
        
        // 发送日志
        fileBaseService.sendINodeEvent(user, EventType.INODE_RENAME, renameNode, null, null, null, null);
        return renameNode;
    }
    
    private void checkNodeTypeForRename(INode renameNode, int type)
    {
        if (renameNode.getType() == INode.TYPE_VERSION)
        {
            LOGGER.error("node type error, ownerId:" + renameNode.getOwnedBy() + ", id:" + renameNode.getId()
                + ", type:" + renameNode.getType());
            throw new NoSuchItemsException();
        }
        if (INode.TYPE_ALL != type)
        {
            if (INode.TYPE_FOLDER_ALL == type)
            {
                if (INode.TYPE_FOLDER != renameNode.getType()
                    && INode.TYPE_BACKUP_COMPUTER != renameNode.getType()
                    && INode.TYPE_BACKUP_DISK != renameNode.getType()
                    && INode.TYPE_BACKUP_EMAIL != renameNode.getType())
                {
                    LOGGER.error("node type error, ownerId:" + renameNode.getOwnedBy() + ", id:"
                        + renameNode.getId() + ", type:" + renameNode.getType());
                    throw new NoSuchItemsException();
                }
            }
            else
            {
                if (renameNode.getType() != type)
                {
                    LOGGER.error("node type error, ownerId:" + renameNode.getOwnedBy() + ", id:"
                        + renameNode.getId() + ", type:" + renameNode.getType());
                    throw new NoSuchItemsException();
                }
            }
        }
    }
    
    @Override
    public FileINodesList searchNodesbyFilter(UserToken user, long ownerId, String name, OrderV1 order,
        Limit limit) throws BaseRunException
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
        iNodeACLService.vaildINodeOperACL(user, rootNode, AuthorityMethod.GET_ALL.name());
        
        // 编码转换
        String transName = FilesCommonUtils.transferStringForSql(name);
        
        int totalCount = iNodeDAO.getINodeCountByName(ownerId, transName);
        
        List<INode> foldersINode = iNodeDAO.searchNodeByName(ownerId, transName, order, limit);
        
        FileINodesList folderLists = new FileINodesList();
        folderLists.setTotalCount(totalCount);
        folderLists.setLimit(limit.getLength());
        folderLists.setOffset(limit.getOffset());
        
        List<INode> folderList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        List<INode> fileList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        
        for (INode tmpInode : foldersINode)
        {
            
            if (FilesCommonUtils.isFolderType(tmpInode.getType()))
            {
                folderList.add(tmpInode);
            }
            else if (tmpInode.getType() == INode.TYPE_FILE)
            {
                if (FilesCommonUtils.isImage(tmpInode.getName()))
                {
                    DataAccessURLInfo dataaccessurlinfo = fileBaseService.getINodeInfoDownURL(ownerId,
                        tmpInode);
                    tmpInode.setThumbnailUrl(dataaccessurlinfo.getDownloadUrl()
                        + FileBaseServiceImpl.THUMBNAIL_PREFIX_SMALL);
                    
                    DataAccessURLInfo bigDataaccessurlinfo = fileBaseService.getINodeInfoDownURL(ownerId,
                        tmpInode);
                    tmpInode.setThumbnailBigURL(bigDataaccessurlinfo.getDownloadUrl()
                        + FileBaseServiceImpl.THUMBNAIL_PREFIX_BIG);
                }
                fileList.add(tmpInode);
            }
        }
        
        folderLists.setFolders(folderList);
        folderLists.setFiles(fileList);
        
        return folderLists;
    }
    
    @Override
    public INode setSyncFolder(UserToken user, INode inode) throws BaseRunException
    {
        if (null == user || null == inode)
        {
            LOGGER.error("user, inode is null");
            throw new BadRequestException();
        }
        
        INode folderNode = fileBaseService.getINodeInfo(inode.getOwnedBy(), inode.getId());
        if (null == folderNode)
        {
            LOGGER.error("inode is null, owerid:" + inode.getOwnedBy() + ",id:" + inode.getId());
            throw new NoSuchItemsException();
        }
        
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user, folderNode, AuthorityMethod.PUT_ALL.name());
        
        INode node = updateFolderSyncStatus(user,
            folderNode.getOwnedBy(),
            folderNode,
            INode.SYNC_STATUS_SETTED);
        
        // 发送事件
        fileBaseService.sendINodeEvent(user, EventType.INODE_UPDATE_SYNC, node, null, null, null, null);
        
        return node;
    }
    
    @Override
    public void updateNodeLinkCode(UserToken user, INode folderNode, String linkCode) throws BaseRunException
    {
        updateNodeLinkCodeCheckType(user, folderNode, linkCode, INode.TYPE_ALL);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateNodeLinkCodeCheckType(UserToken user, INode iNode, String linkCode, byte type)
        throws BaseRunException
    {
        if (null == user || null == iNode)
        {
            LOGGER.error("user, inode is null");
            throw new BadRequestException();
        }
        INode tmpNode = fileBaseService.getINodeInfo(iNode.getOwnedBy(), iNode.getId());
        if (null == tmpNode)
        {
            LOGGER.error("inode is null,owerId:" + iNode.getOwnedBy() + ",id:" + iNode.getId());
            throw new NoSuchItemsException();
        }
        // 处于回收站状态文件也允许重置外链
        if ((INode.TYPE_ALL != type && tmpNode.getType() != type) || tmpNode.getType() == INode.TYPE_VERSION)
        {
            LOGGER.error("node type error, ownerId:" + iNode.getOwnedBy() + ", id:" + iNode.getId()
                + ",need type: " + type + ",type" + tmpNode.getType());
            throw new NoSuchItemsException();
        }
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user, tmpNode, AuthorityMethod.GET_ALL.name());
        iNode.setLinkCode(linkCode);
        nodeUpdateService.updateINodeLinkCode(iNode);
        if (iNode.getLinkCode() == null)
        {
            fileBaseService.sendINodeEvent(user, EventType.LINK_DELETE, iNode, null, null, null, null);
        }
        else
        {
            fileBaseService.sendINodeEvent(user, EventType.LINK_CREATE, iNode, null, null, null, null);
        }
    }
    
    @Override
    public void updateNodeShareStatus(UserToken user, INode inode, byte shareStatus) throws BaseRunException
    {
        if (inode.getShareStatus() == shareStatus)
        {
            return;
        }
        updateNodeShareStatusCheckType(user, inode, shareStatus, INode.TYPE_ALL);
    }
    
    @Override
    public void updateNodeShareStatusCheckType(UserToken user, INode iNode, byte shareStatus, byte type)
        throws BaseRunException
    {
        if (null == user || null == iNode)
        {
            LOGGER.error("user, inode is null");
            throw new BadRequestException();
        }
        
        if (shareStatus != INode.SHARE_STATUS_SHARED && shareStatus != INode.SHARE_STATUS_UNSHARED)
        {
            LOGGER.error("shareStatus  error:" + shareStatus);
            throw new BadRequestException();
        }
        
        INode tmpNode = fileBaseService.getINodeInfo(iNode.getOwnedBy(), iNode.getId());
        if (null == tmpNode)
        {
            LOGGER.error("inode is null,owerId:" + iNode.getOwnedBy() + ",id:" + iNode.getId());
            throw new NoSuchItemsException();
        }
        
        // 处于回收站状态的文件也可以更新共享关系
        
        if ((INode.TYPE_ALL != type && tmpNode.getType() != type) || tmpNode.getType() == INode.TYPE_VERSION)
        {
            LOGGER.error("node type error, ownerId:" + iNode.getOwnedBy() + ", id:" + iNode.getId()
                + ",need type: " + type + ",type" + tmpNode.getType());
            throw new NoSuchItemsException();
        }
        
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user, tmpNode, AuthorityMethod.PUT_ALL.name());
        
        iNode.setShareStatus(shareStatus);
        
        nodeUpdateService.updateINodeShareStatus(iNode);
        
    }
    
    /**
     * 检查节点是否存在冲突
     * 
     * @param srcNode
     * @param destNode
     * @throws BaseRunException
     * @throws SubFolderConflictException
     */
    private void checkCopyConflict(INode srcNode, INode destNode) throws BaseRunException
    {
        if (srcNode.getOwnedBy() == destNode.getOwnedBy())
        {
            if (srcNode.getId() == destNode.getId())
            {
                throw new SameNodeConflictException();
            }
        }
        
        if (fileBaseService.checkNodeIsSubNode(srcNode, destNode))
        {
            throw new SubFolderConflictException();
        }
    }
    
    /**
     * 检查节点是否存在冲突
     * 
     * @param srcNode
     * @param destNode
     * @throws BaseRunException
     * @throws SubFolderConflictException
     */
    private void checkMoveConflict(INode srcNode, INode destNode) throws BaseRunException
    {
        if (srcNode.getOwnedBy() == destNode.getOwnedBy())
        {
            if (srcNode.getId() == destNode.getId())
            {
                throw new SameNodeConflictException();
            }
            if (srcNode.getParentId() == destNode.getId())
            {
                throw new SameParentConflictException();
            }
        }
        
        if (fileBaseService.checkNodeIsSubNode(srcNode, destNode))
        {
            throw new SubFolderConflictException();
        }
    }
    
    private void checkNameConflict(INode srcNode, INode destNode) throws BaseRunException
    {
        if (fileBaseService.isSameNameNodeExist(destNode.getOwnedBy(), destNode.getId(), srcNode.getName()))
        {
            throw new FilesNameConflictException("File name conflict: " + srcNode.getName());
        }
    }
    
    /**
     * 判断Orderby是否合法
     * 
     * @param orderBy
     * @return
     * @throws BaseRunException
     */
    private void checkVaildListFoldersOrderby(OrderV1 orderBy) throws BaseRunException
    {
        if (null != orderBy && StringUtils.isNotBlank(orderBy.getField()))
        {
            String[] ords = ORDERBY_PRO.split(";");
            for (String ord : ords)
            {
                if (ord.equalsIgnoreCase(orderBy.getField()))
                {
                    return;
                }
            }
            
            throw new BadRequestException();
        }
    }
    
    /**
     * 填充缩略图地址
     * 
     * @param thumbSize
     * @param userId
     * @param tmpNode
     * @throws BaseRunException
     */
    private void fillThumbnailUrl(long userId, INode tmpNode) throws BaseRunException
    {
        if (!FilesCommonUtils.isImage(tmpNode.getName()))
        {
            return;
        }
        
        DataAccessURLInfo urlInfo = fileBaseService.getINodeInfoDownURL(userId, tmpNode);
        tmpNode.setThumbnailUrl(urlInfo.getDownloadUrl() + FileBaseServiceImpl.THUMBNAIL_PREFIX_SMALL);
        DataAccessURLInfo bigThumbnailUrl = fileBaseService.getINodeInfoDownURL(userId, tmpNode);
        tmpNode.setThumbnailBigURL(bigThumbnailUrl.getDownloadUrl()
            + FileBaseServiceImpl.THUMBNAIL_PREFIX_BIG);
    }
    
    private INode getAndCheckDestNode(INode parentNode) throws BaseRunException
    {
        if (parentNode.getId() == INode.FILES_ROOT)
        {
            return new INode(parentNode.getOwnedBy(), INode.FILES_ROOT);
        }
        
        INode destNode = fileBaseService.getINodeInfo(parentNode.getOwnedBy(), parentNode.getId());
        if (null == destNode)
        {
            String msg = "Dest node is null, owner id:" + parentNode.getOwnedBy() + ", node id:"
                + parentNode.getId();
            throw new NoSuchDestException(msg);
        }
        if (destNode.getStatus() != INode.STATUS_NORMAL)
        {
            String msg = "Dest node status abnormal, owner id:" + destNode.getOwnedBy() + ", node id:"
                + destNode.getId() + ", node status:" + destNode.getStatus() + ", dest type"
                + destNode.getType();
            throw new NoSuchDestException(msg);
        }
        if (!FilesCommonUtils.isFolderType(destNode.getType()))
        {
            String msg = "Dest node type invalid, owner id:" + destNode.getOwnedBy() + ", node id:"
                + destNode.getId() + ", node type:" + destNode.getType();
            throw new NoSuchDestException(msg);
        }
        return destNode;
    }
    
    private INode getAndCheckSrcNode(UserToken user, INode iNode, int type) throws BaseRunException
    {
        INode srcNode = fileBaseService.getINodeInfo(iNode.getOwnedBy(), iNode.getId());
        if (null == srcNode)
        {
            String msg = "src node does not exsit, owner id:" + iNode.getOwnedBy() + ", node id:"
                + iNode.getId();
            throw new NoSuchSourceException(msg);
        }
        
        if (FilesCommonUtils.isFolderType(srcNode.getType()))
        {
            int num = iNodeDAOV2.getSubINodeCount(srcNode, true);
            FileBasicConfig basicConfig = fileBaseService.getFileBaiscConfig(user);
            
            if (num != 0 && null != basicConfig && !basicConfig.getAllowBatch())
            {
                throw new ForbiddenException();
            }
        }
        
        doCheckNodeTypeIgnoreVersion(type, srcNode);
        
        if (INode.STATUS_NORMAL != srcNode.getStatus())
        {
            LOGGER.error("Node status abnormal, owner id:" + srcNode.getOwnedBy() + ", node id:"
                + srcNode.getId() + ", status:" + srcNode.getStatus());
            throw new NoSuchSourceException();
        }
        
        return srcNode;
    }
    
    private void doCheckNodeTypeIgnoreVersion(int type, INode srcNode)
    {
        if (INode.TYPE_ALL != type)
        {
            if (INode.TYPE_FOLDER_ALL == type)
            {
                if (INode.TYPE_FOLDER != srcNode.getType() && INode.TYPE_BACKUP_COMPUTER != srcNode.getType()
                    && INode.TYPE_BACKUP_DISK != srcNode.getType()
                    && INode.TYPE_BACKUP_EMAIL != srcNode.getType())
                {
                    LOGGER.error("Invalid node type, owner id:" + srcNode.getOwnedBy() + ", node id:"
                        + srcNode.getId() + ",type:" + srcNode.getType());
                    throw new NoSuchSourceException();
                }
            }
            else
            {
                if (srcNode.getType() != type)
                {
                    LOGGER.error("Invalid node type, owner id:" + srcNode.getOwnedBy() + ", node id:"
                        + srcNode.getId() + ",type:" + srcNode.getType());
                    throw new NoSuchSourceException();
                }
            }
        }
        
    }
    
    /**
     * 更新同步状态
     * 
     * @param inode
     * @param syncNum
     */
    private INode updateFolderSyncStatus(UserToken user, long ownerId, INode inode, byte syncStatus)
    {
        // 更新同步版本号
        fileBaseService.setNodeSyncVersion(ownerId, inode);
        inode.setSyncStatus(syncStatus);
        inode.setModifiedAt(new Date());
        inode.setModifiedBy(user.getId());
        nodeUpdateService.updateAtmoNodeSyncStatus(inode);
        return inode;
    }

	@Override
	public INode getSubFolderByName(INode parantNode, String name) {
		// TODO Auto-generated method stub
		INode subINode=iNodeDAOV2.getSubFolderByName(parantNode,name);
		return subINode;
	}

	@Override
	public INode copyNodeToFolderCheckType(UserToken user, INode src, INode dest, String newName, int type, boolean valiLinkAccessCode, INodeLink link) throws BaseRunException {
		// TODO Auto-generated method stub
		
		UserToken usersrc=new UserToken();
	    usersrc.setLinkCode(link.getId());
		 // 检查源节点有效性
        INode srcNode = getAndCheckSrcNode(usersrc, src, type);
        // 检查目的节点有效性
        INode destParent = getAndCheckDestNode(dest);
        checkCopyConflict(srcNode, destParent);
        user.setLinkCode(null);
        iNodeACLService.vaildINodeOperACL(user, destParent, AuthorityMethod.UPLOAD_OBJECT.name());
      
        iNodeACLService.vaildINodeOperACL(usersrc, srcNode, AuthorityMethod.GET_INFO.name(), valiLinkAccessCode);
        
        // 检查目标路径下是否存在同名文件
        if (StringUtils.isNotBlank(newName))
        {
            srcNode.setName(newName);
        }
        checkNameConflict(srcNode, destParent);
        
        checkNodeTypeForCopyAndMove(srcNode, destParent);
        
        if (srcNode.getType() == INode.TYPE_FILE)
        {
            String[] logMsgs = new String[]{String.valueOf(srcNode.getOwnedBy()),
                String.valueOf(srcNode.getId()), String.valueOf(destParent.getOwnedBy()),
                String.valueOf(destParent.getId())};
            // 设置同步版本号
            fileBaseService.setNodeSyncStatusAndVersion(destParent.getOwnedBy(), srcNode, destParent);
            // 复制文件
            INode node = nodeCopyService.copyFileNodeToFolder(user, srcNode, destParent);
            if (null == node)
            {
                String message = "Src node type invalid, owner id:" + srcNode.getOwnedBy() + ", node id:"
                    + srcNode.getId() + ",type:" + srcNode.getType();
                throw new NoSuchSourceException(message);
            }
            
            String keyword = StringUtils.trimToEmpty(srcNode.getName());
            fileBaseService.sendINodeEvent(user,
                EventType.INODE_COPY,
                node,
                destParent,
                UserLogType.COPY_FILE,
                logMsgs,
                keyword);
            return node;
        }
        else if (FilesCommonUtils.isFolderType(srcNode.getType()))
        {
            // 发送日志
            String[] logMsgs = new String[]{String.valueOf(srcNode.getOwnedBy()),
                String.valueOf(srcNode.getId()), String.valueOf(destParent.getOwnedBy()),
                String.valueOf(destParent.getId())};
            // 设置同步版本号
            fileBaseService.setNodeSyncStatusAndVersion(destParent.getOwnedBy(), srcNode, destParent);
            
            // 复制文件夹并获取统计信息
            INode node = nodeCopyService.copyFolderNodeToFolder(user, srcNode, destParent);
            
            srcNode.setSize(srcNode.getSize() + node.getSize());
            srcNode.setFileCount(srcNode.getFileCount() + node.getFileCount());
            
            String keyword = StringUtils.trimToEmpty(node.getName());
            fileBaseService.sendINodeEvent(user,
                EventType.INODE_COPY,
                srcNode,
                destParent,
                UserLogType.COPY_FOLDER,
                logMsgs,
                keyword);
            return node;
        }
        else
        {
            String message = "Src node type invalid, owner id:" + srcNode.getOwnedBy() + ", node id:"
                + srcNode.getId() + ",type:" + srcNode.getType();
            throw new NoSuchSourceException(message);
        }
        
	}
    
}
