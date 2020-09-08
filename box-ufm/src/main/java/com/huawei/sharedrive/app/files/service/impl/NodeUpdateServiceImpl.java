package com.huawei.sharedrive.app.files.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.dao.batchparam.BatchParams;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.domain.ObjectUpdateInfo;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.NodeMessageService;
import com.huawei.sharedrive.app.files.service.NodeUpdateService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.core.exception.InnerException;

@Component
@Service("nodeUpdateService")
public class NodeUpdateServiceImpl implements NodeUpdateService
{
    public static final String FIRST_VER_NUM = "1";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeUpdateServiceImpl.class);
    
    private final static int FOLDER_DELETE_DEPTH = 500;
    
    // 注入上下文
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private NodeMessageService nodeMessageService;
    
    @Autowired
    private ObjectReferenceDAO objectReferenceDAO;
    
    private NodeUpdateService proxySelf; // ② 表示代理对象，不是目标对象
    
    @PostConstruct
    // ③ 初始化方法
    public void setSelf()
    {
        // 从上下文获取代理对象（如果通过proxtSelf=this是不对的，this是目标对象）
        // 此种方法不适合于prototype Bean，因为每次getBean返回一个新的Bean
        proxySelf = context.getBean(NodeUpdateServiceImpl.class);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAllINodeStatus(Long ownedBy, byte status)
    {
        INode iNode = new INode();
        iNode.setOwnedBy(ownedBy);
        iNode.setStatus(status);
        iNodeDAO.updateAllINodeStatus(iNode);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAtmoNodeStatus(INode tmpNode, byte status)
    {
        tmpNode.setStatus(status);
        iNodeDAO.updateINodeStatus(tmpNode);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAtmoNodeStatusToDelete(INode tmpNode, byte status)
    {
        tmpNode.setStatus(status);
        tmpNode.setModifiedAt(new Date());
        iNodeDAO.updateINodeStatusToDelete(tmpNode);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAtmoNodeStatusWithTrans(INode tmpNode, byte status, List<INode> waitDeleteList,
        boolean forceDelete)
    {
        if (forceDelete)
        {
            for (INode node : waitDeleteList)
            {
                iNodeDAO.updateINodeStatus(node);
            }
            waitDeleteList.clear();
            return;
        }
        tmpNode.setStatus(status);
        waitDeleteList.add(tmpNode);
        if (waitDeleteList.size() >= BatchParams.getBatchUpdateItems())
        {
            for (INode node : waitDeleteList)
            {
                iNodeDAO.updateINodeStatus(node);
            }
            waitDeleteList.clear();
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatusWithTransByParent(INode tmpNode, byte status, List<INode> waitDeleteList,
        boolean forceDelete)
    {
        
        if (forceDelete)
        { 
            tmpNode.setStatus(status);
            List<Long> parentNodeList = new ArrayList<Long>(waitDeleteList.size());
            Long tempLong;
            for (INode parentNode : waitDeleteList)
            {
                tempLong = parentNode.getId();
                parentNodeList.add(tempLong);
            }
            iNodeDAO.batchUpdateStatusByParentList(tmpNode, parentNodeList);
            waitDeleteList.clear();
            return;
        }
        tmpNode.setStatus(status);
        waitDeleteList.add(tmpNode);
        if (waitDeleteList.size() >= BatchParams.getBatchUpdateItems())
        {
            List<Long> parentNodeList = new ArrayList<Long>(waitDeleteList.size());
            Long tempLong;
            for (INode parentNode : waitDeleteList)
            {
                tempLong = parentNode.getId();
                parentNodeList.add(tempLong);
            }
            iNodeDAO.batchUpdateStatusByParentList(tmpNode, parentNodeList);
            waitDeleteList.clear();
        }
        
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INode updateAtmoNodeSyncStatus(INode inode)
    {
        iNodeDAO.updateINodeSyncVersion(inode);
        return inode;
    }
    
    /**
     * 循环遍历更新文件状态，从正常状态->删除回收站状态->删除状态
     * 
     * @param node
     * @param status
     */
    @Override
    public void updateFolderStatusByLevel(UserToken user, INode node)
    {
      
        List<INode> folderList = new ArrayList<INode>(1);
        folderList.add(node);
        List<INode> waitUpdateList = new ArrayList<INode>(BatchParams.getBatchUpdateItems());
        waitUpdateList.add(node);
        updateSubFolderStatus(node.getOwnedBy(), folderList, node, user.getCloudUserId(), waitUpdateList, 0);
        LOGGER.info("[asyncDelete]end updateFolderStatusByLevel recuice");
        proxySelf.updateStatusWithTransByParent(node,
            INode.STATUS_TRASH_DELETE,
            waitUpdateList,
            true);
    }
    
/*    private void notifyAllToDeleteMsg(INode subNode)
    {
        // 通知共享
        nodeMessageService.notifyShareToDeleteMsg(subNode);
        // 通知外链
        // 通知ACL
    }*/
    
    private void updateSubFolderStatus(long ownedBy, List<INode> folderList, INode originNode,
        long operatorId, List<INode> waitUpdateList, int relaticeDepth)
    {
        
        if(relaticeDepth > FOLDER_DELETE_DEPTH)
        {
            LOGGER.error("[deletelog]Delete max depth larger than " + FOLDER_DELETE_DEPTH);
            return;
        }
        List<INode> childList = iNodeDAO.batchQueryNormalByParentAndStatus(ownedBy, folderList);
        List<INode> childFolderList = new ArrayList<INode>(10);
        for (INode tempChild : childList)
        {
            if (tempChild.getStatus() != INode.STATUS_NORMAL)
            {
                continue;
            }
            if (tempChild.getType() == INode.TYPE_FOLDER || tempChild.getType() == INode.TYPE_BACKUP_COMPUTER
                || tempChild.getType() == INode.TYPE_BACKUP_DISK)
            {
                childFolderList.add(tempChild);
                tempChild.setSyncVersion(originNode.getSyncVersion());
                tempChild.setModifiedBy(operatorId);
                proxySelf.updateStatusWithTransByParent(tempChild,
                    INode.STATUS_TRASH_DELETE,
                    waitUpdateList,
                    false);
                nodeMessageService.notifyShareToTrashMsg(tempChild,operatorId);
            }
            else if (tempChild.getType() == INode.TYPE_FILE)
            {
                tempChild.setSyncVersion(originNode.getSyncVersion());
                tempChild.setModifiedBy(operatorId);
                proxySelf.updateStatusWithTransByParent(tempChild,
                    INode.STATUS_TRASH_DELETE,
                    waitUpdateList,
                    false);
                nodeMessageService.notifyShareToTrashMsg(tempChild,operatorId);
            }
        }
        if (!childFolderList.isEmpty())
        {
            updateSubFolderStatus(ownedBy, childFolderList, originNode, operatorId, waitUpdateList, relaticeDepth + 1);
        }
    }
    
    // @Override
    // @Transactional(propagation = Propagation.REQUIRED)
    // public void updateINode(INode node)
    // {
    // iNodeDAO.update(node);
    // }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateINodeForFlashUploadFile(INode fileNode, INode parentNode, long parentOriginalId)
    {
        // 更新父子节点
        iNodeDAO.updateByOriginalId(parentNode, parentOriginalId);
        iNodeDAO.create(fileNode);
    }
    
    @Override
    public void updateINodeForUploadFile(INode node)
    {
        node.setVersion(FIRST_VER_NUM);
        node.setStatus(INode.STATUS_NORMAL);
        iNodeDAO.updateForUploadFile(node);
    }
    
    // @Override
    // @Transactional(propagation = Propagation.REQUIRED)
    // public void updateINodeForUploadVersion(INode fileNode, INode parentNode, long
    // parentOriginalId)
    // {
    // // 同步共享外链状态
    // fileNode.setSyncStatus(parentNode.getSyncStatus());
    // fileNode.setLinkCode(parentNode.getLinkCode());
    // fileNode.setShareStatus(parentNode.getShareStatus());
    //
    // // 获取当前版本数量，必须先获取再updateByOriginalId，否则无法取到正确的版本数
    // int verNum = iNodeDAO.getINodeTotal(fileNode);
    // // 更新版本数
    // if (StringUtils.isNotBlank(parentNode.getVersion()))
    // {
    // fileNode.setVersion(String.valueOf(++verNum));
    // }
    // else
    // {
    // fileNode.setVersion(INode.FIRST_VER_NUM);
    // }
    //
    // // 更新父子节点， updateByOriginalId必须放在updateINodeByObjectId之前，否则会报主键冲突
    // int updateParentResult = iNodeDAO.updateByOriginalId(parentNode, parentOriginalId);
    // int updateChildResult = iNodeDAO.updateINodeByObjectId(fileNode);
    //
    // // 规避由于更新数据库滞后造成的DSS thrit回调超时后, 调用abortUpload接口删除元数据导致数据丢失的问题
    // if (updateParentResult != 1 || updateChildResult != 1)
    // {
    // LOGGER.error("Update failed! updateParentResult: {}, updateChildResult: {}",
    // updateParentResult,
    // updateChildResult);
    // throw new ExtRuntimeException();
    // }
    //
    // }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INode updateINodeForUploadVersionById(INode fileNode, INode parentNode,
        ObjectUpdateInfo objectUpdateInfo)
    {
        INode curFileNode = iNodeDAO.get(fileNode.getOwnedBy(), fileNode.getId());
        INode curParentNode = iNodeDAO.get(parentNode.getOwnedBy(), parentNode.getId());
        if (curFileNode == null || curParentNode == null)
        {
            LOGGER.error("curFileNode == null || curParentNode == null");
            throw new InnerException("curFileNode == null || curParentNode == null");
        }
        fileNode.copyFrom(curFileNode);
        parentNode.copyFrom(curParentNode);
        
        // 闪传方案变更, 文件内容计算由sha1变更为整文件MD5 + 取样MD5;
        // String md5 = ;
        String md5 = (FilesCommonUtils.parseMD5(objectUpdateInfo.getSha1()).get("MD5") == null) ? ""
            : FilesCommonUtils.parseMD5(objectUpdateInfo.getSha1()).get("MD5");
        long length = objectUpdateInfo.getLength();
        fileNode.setSha1(md5);
        fileNode.setSize(length);
        fileNode.setModifiedAt(new Date());
        
        // 获取当前版本数量，必须先获取再updateByOriginalId，否则无法取到正确的版本数
        int verNum = iNodeDAO.getINodeTotal(parentNode);
        
        // 同步共享外链状态
        fileNode.setSyncStatus(parentNode.getSyncStatus());
        fileNode.setSyncVersion(fileBaseService.getUserSyncVersion(fileNode.getOwnedBy()));
        fileNode.setLinkCode(parentNode.getLinkCode());
        fileNode.setShareStatus(parentNode.getShareStatus());
        fileNode.setStatus(INode.STATUS_NORMAL);
        fileNode.setType(INode.TYPE_FILE);
        
        parentNode.setType(INode.TYPE_VERSION);
        
        // 更新版本数
        if (StringUtils.isNotBlank(parentNode.getVersion()))
        {
            fileNode.setVersion(String.valueOf(++verNum));
        }
        else
        {
            fileNode.setVersion(INode.FIRST_VER_NUM);
        }
        // 通过交换Id达到交换内容目的
        long id = parentNode.getId();
        long parentId = parentNode.getParentId();
        parentNode.setId(fileNode.getId());// parentNode变成版本内容
        parentNode.setParentId(fileNode.getParentId());
        fileNode.setId(id);// fileNode存当前文件内容
        fileNode.setParentId(parentId);
        
        if (fileNode.getId() < parentNode.getId())
        {
            int fileResult = iNodeDAO.update(fileNode);
            int parentResult = iNodeDAO.update(parentNode);
            checkFileOrParentResult(fileResult, parentResult);
        }
        else
        {
            int parentResult = iNodeDAO.update(parentNode);
            int fileResult = iNodeDAO.update(fileNode);
            checkFileOrParentResult(fileResult, parentResult);
        }
        
        return fileNode;
    }
    
    private void checkFileOrParentResult(int fileResult, int parentResult)
    {
        // 规避由于更新数据库滞后造成的DSS thrit回调超时后, 调用abortUpload接口删除元数据导致数据丢失的问题
        if (fileResult != 1 || parentResult != 1)
        {
            LOGGER.error("Update failed! updateParentResult: {}, updateChildResult: {}",
                parentResult,
                fileResult);
            throw new InnerException("Update failed!");
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INode updateINodeLinkCode(INode iNode)
    {
        iNodeDAO.updateINodeLinkCode(iNode);
        return iNode;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateINodeModifiedAt(INode node)
    {
        node.setModifiedAt(new Date());
        iNodeDAO.updateObjectModifiedAt(node);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateINodeShareStatus(INode iNode)
    {
        iNodeDAO.updateINodeShareStatus(iNode);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateINodeStatus(INode node)
    {
        iNodeDAO.updateINodeStatus(node);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int updateObjectForDedup(String newObjId, int newRGId, String oldObjId, long ownerId)
    {
        return iNodeDAO.updateObjectForDedup(newObjId, newRGId, oldObjId, ownerId);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ObjectReference updateObjectForMerge(long size, String sha1, String blockMD5, String objId,
        long ownerId) throws BaseRunException
    {
        iNodeDAO.updateObjectForMerge(size, sha1, objId, ownerId);
        objectReferenceDAO.updateFingerprintAndSize(objId, size, sha1, blockMD5);
        ObjectReference objRef = objectReferenceDAO.get(objId);
        return objRef;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateObjectRefDeleteTime(ObjectReference objectReference)
    {
        objectReferenceDAO.updateLastDeleteTime(objectReference);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatusAndModifiedWithTrans(INode tmpNode, byte status, List<INode> waitUpdateList,
        boolean forceUpdate)
    {
        if (forceUpdate)
        {
            for (INode node : waitUpdateList)
            {
                iNodeDAO.updateINodeStatusToDelete(node);
            }
            return;
        }
        tmpNode.setStatus(status);
        tmpNode.setModifiedAt(new Date());
        waitUpdateList.add(tmpNode);
        if (waitUpdateList.size() >= BatchParams.getBatchUpdateItems())
        {
            for (INode node : waitUpdateList)
            {
                iNodeDAO.updateINodeStatusToDelete(node);
            }
            waitUpdateList.clear();
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateTrashNodesStatus(INode iNode, Long id)
    {
        iNodeDAO.updateTrashAllNodesStatus(iNode, null);
    }
    
}
