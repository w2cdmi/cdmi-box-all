package com.huawei.sharedrive.app.files.service;

import java.util.List;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.domain.ObjectUpdateInfo;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

public interface NodeUpdateService
{
    void updateAllINodeStatus(Long ownedBy, byte status);
    
    void updateAtmoNodeStatus(INode tmpNode, byte status);
    
    /**
     * 根据层级将文件夹子节点放入回收站
     * 
     * @param user
     * @param node
     * @param status
     * @return
     */
    void updateFolderStatusByLevel(UserToken user, INode node);
    
    /**
     * 彻底清除，需要更新lastmodifyAt时间
     * 
     * @param tmpNode
     * @param status
     */
    void updateAtmoNodeStatusToDelete(INode tmpNode, byte status);
    
    void updateAtmoNodeStatusWithTrans(INode tmpNode, byte status, List<INode> waitDeleteList,
        boolean forceDelete);
        
    /**
     * 根据父文件夹ID将所有子节点删除
     * 
     * @param tmpNode
     * @param status
     * @param waitDeleteList
     * @param forceDelete
     */
    void updateStatusWithTransByParent(INode tmpNode, byte status, List<INode> waitDeleteList,
        boolean forceDelete);
        
    INode updateAtmoNodeSyncStatus(INode iNode);
    
    // void updateINode(INode node);
    
    void updateINodeForFlashUploadFile(INode fileNode, INode parentNode, long parentOriginalId);
    
    void updateINodeForUploadFile(INode node);
    
    // void updateINodeForUploadVersion(INode fileNode, INode parentNode, long
    // parentOriginalId);
    
    INode updateINodeForUploadVersionById(INode fileNode, INode parentNode,
        ObjectUpdateInfo objectUpdateInfo);
        
    INode updateINodeLinkCode(INode iNode);
    
    void updateINodeModifiedAt(INode node);
    
    void updateINodeShareStatus(INode iNode);
    
    void updateINodeStatus(INode node);
    
    /**
     * 重删时更新INode的对象相关信息，对象ID,资源组ID
     * 
     * @param newObjId
     * @param newRGId
     * @param oldObjId
     * @param ownerId
     * @return 成功更新的记录数
     */
    int updateObjectForDedup(String newObjId, int newRGId, String oldObjId, long ownerId);
    
    /**
     * DC合并分片后，回调更形sha1
     * 
     * @param size
     * @param sha1
     * @param objId
     * @param ownerId
     * @return
     * @throws BaseRunException
     */
    ObjectReference updateObjectForMerge(long size, String sha1, String blockMD5, String objId, long ownerId)
        throws BaseRunException;
        
    void updateObjectRefDeleteTime(ObjectReference objectReference);
    
    void updateStatusAndModifiedWithTrans(INode tmpNode, byte status, List<INode> waitUpdateList,
        boolean forceUpdate);
        
    void updateTrashNodesStatus(INode iNode, Long id);
    
}
