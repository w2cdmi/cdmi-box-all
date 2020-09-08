/**
 * 
 */
package com.huawei.sharedrive.app.files.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.synchronous.INodeRowHandle;

import pw.cdmi.box.domain.Limit;

/**
 * @author q90003805
 *         
 */
public interface INodeDAO
{
    /**
     * 检测该节点是否存在
     * 
     * @param iNode
     * @return
     */
    boolean checkINodeExist(INode iNode);
    
    void copyTempINodeTable(INode srcNode, Long destTableSuffix);
    
    void copyTempINodeTableNoBackup(INode srcNode, Long destTableSuffix);
    
    /**
     * 创建INode对象
     * 
     * @param iNode 必须设置id和ownedBy
     */
    void create(INode iNode);
    
    /**
     * 减少文件版本数
     * 
     * @param ownedBy
     * @param fileId
     * @return
     */
    int decreaseFileVersionNum(long ownedBy, long fileId);
    
    /**
     * 删除INode对象
     * 
     * @param iNode 必须设置id和ownedBy
     */
    int delete(INode iNode);
    
    /**
     * 删除节点，通过对象并检查状态
     * 
     * @param iNode
     * @return
     */
    int deleteNodeByObjectAndCheckStatus(INode iNode);
    
    void dropTempINodeTable(long ownerId, Long destTableSuffix);
    
    /**
     * 获取指定INode对象
     * 
     * @param iNode 必须设置id和ownedBy
     * @return
     */
    INode get(INode iNode);
    
    /**
     * 获取指定INode对象
     * 
     * @param ownerId
     * @param iNodeId
     * @return
     */
    INode get(long ownerId, long iNodeId);
    
    /**
     * 获取用户所有元数据,同时写入sqlite文件
     * 
     * @param ownerId
     * @param rowHandle
     * @return
     */
    INodeRowHandle getAllINodeMetadatas(long ownerId, final INodeRowHandle rowHandle);
    
    /**
     * 获取变化元数据,同时写入sqlite文件
     * 
     * @param ownerId
     * @param modifiedAt
     * @param rowHandle
     * @return
     */
    INodeRowHandle getChangeMetadatas(long ownerId, Date modifiedAt, final INodeRowHandle rowHandle);
    
    /**
     * 获取从这之后的时间变化元数据，不列表版本内容
     * 
     * @param ownerId
     * @param modifiedAt
     * @return
     */
    List<INode> getChangeNodesAfterTime(long ownerId, Date modifiedAt);
    
    /**
     * 获取增量元数据,同时写入sqlite文件
     * 
     * @param ownerId
     * @param beginSyncVersion
     * @param endSyncVersion
     * @param rowHandle
     * @return
     */
    INodeRowHandle getDeltaINodeMetadatas(long ownerId, long beginSyncVersion, long endSyncVersion,
        final INodeRowHandle rowHandle);
        
    /**
     * 获取文件最早的N条版本记录
     * 
     * @param ownerId
     * @param nodeId
     * @param limit
     * @return
     */
    List<INode> getEarliestVersions(Long ownerId, Long nodeId, int limit);
    
    /**
     * 根据名称查找
     * 
     * @param name
     * @param parentId
     * @param ownerId
     * @return
     */
    List<INode> getFileByNameAndParentId(String name, long parentId, long ownerId);
    
    /**
     * 获取指定文件夹下所有元数据,同时写入sqlite文件
     * 
     * @param filter
     * @param rowHandle
     * @return
     */
    INodeRowHandle getFolderMetadatas(INode filter, final INodeRowHandle rowHandle);
    
    /**
     * 根据名称获取节点信息
     * 
     * @param ownerId 节点OwnerId
     * @param parentNodeId 节点父文件夹Id
     * @param name 节点名称
     * @return
     */
    INode getINodeByName(long ownerId, long parentNodeId, String name);
    
    /**
     * 通过object获取节点
     * 
     * @param ownerId
     * @param objectId
     * @return
     */
    List<INode> getINodeByObjectId(long ownerId, String objectId);
    
    /**
     * 根据父节点查询Inode,即是parentid = filter.id, 组装预览属性
     * 
     * @param filter
     * @param order
     * @param limit
     * @return
     */
    List<INode> getINodeByParent(INode filter, OrderV1 order, Limit limit);
    
    /**
     * 根据父节点查询Inode,即是parentid = filter.id
     * 
     * @param filter
     * @param order
     * @param limit
     * @return
     */
    List<INode> getINodeByParentInternal(INode filter, OrderV1 order, Limit limit);
    
    /**
     * 根据父节点查询Inode,即是parentid = filter.id
     * 
     * @param filter
     * @param order
     * @param limit
     * @return
     */
    List<INode> getINodeByParentAndType(INode filter, Limit limit);
    
    /**
     * 根据parent id 和 状态查询Inode
     * 
     * @param filter
     * @param order
     * @param limit
     * @return
     */
    List<INode> getINodeByParentAndStatus(INode filter, OrderV1 order, Limit limit);
    
    /**
     * 根据状态查询Inode, 组装预览属性
     * 
     * @param filter
     * @param order
     * @param limit
     * @return
     */
    List<INode> getINodeByStatus(INode filter, OrderV1 order, Limit limit);
    
    /**
     * 获取指定同步版本号区间的元数据
     * 
     * @param ownerId
     * @param beginSyncVersion
     * @param endSyncVersion
     * @return
     */
    List<INode> getINodeBySyncVersion(long ownerId, long beginSyncVersion, long endSyncVersion);
    
    /**
     * 获取同名节点元数据
     * 
     * @param ownerId
     * @param name
     * @return
     */
    int getINodeCountByName(long ownerId, String name);
    
    /**
     * 获取状态节点元数据
     * 
     * @param iNode
     * @return
     */
    int getINodeCountByStatus(INode iNode);
    
    /**
     * 获取状态节点元数据, 过滤版本
     * 
     * @param iNode
     * @return
     */
    int getINodeCountByStatusIgnoreVersion(INode iNode);
    
    /**
     * 根据过滤条件查询Inode
     * 
     * @param filter
     * @param order
     * @param limit
     * @return
     */
    List<INode> getINodeFilterd(INode filter, OrderV1 order, Limit limit);
    
    /**
     * 
     * 获取版本总数,包含自己及其版本节点
     * 
     * @param filter
     * @return
     */
    int getINodeTotal(INode filter);
    
    /**
     * 
     * 获取版本总数,包含自己及其版本节点
     * 
     * @param filter
     * @return
     */
    int getINodeTotalForUpdate(INode filter);
    
    /**
     * 获取指定用户当前INodeId最大值
     * 
     * @param ownedBy
     * @return
     */
    long getMaxINodeId(long ownedBy);
    
    /**
     * 获取指定用户当前SyncVersion最大值
     * 
     * @param ownedBy
     * @return
     */
    long getMaxSyncVersion(long ownedBy);
    
    /**
     * 根据名称查询正常状态节点
     * 
     * @param ownerId
     * @param parentId
     * @param name
     * @return
     */
    List<INode> getNodeByName(long ownerId, long parentId, String name);
    
    /**
     * 获取本身和子版本
     * 
     * @param filter
     * @param order
     * @param limit
     * @return
     */
    List<INode> getSubINodeAndSelf(INode filter, OrderV1 order, Limit limit);
    
    /**
     * 获取子目录
     * 
     * @param parentNode
     * @return
     */
    List<INode> getSubINodeByTypeAndStatus(INode parentNode);
    
    /**
     * 获取子数量
     * 
     * @param INode
     * @return
     */
    int getSubINodeCount(INode iNode);
    
    INodeRowHandle getTempINodes(long ownerId, Long destTableSuffix, Limit limit, INodeRowHandle rowHanger);
    
    /**
     * 获取回收站文件总大小
     * 
     * @param iNode
     * @return
     */
    long getTrashTotalSize(INode iNode);
    
    /**
     * 查询用户文件总数(含文件/文件夹/版本/回收站中的数据)
     * 
     * @param node
     * @return
     */
    long getUserTotalFiles(Long ownerId);
    
    /**
     * 获取用户已用空间(不包括回收站中正在删除的文件)
     * 
     * @param long
     * @return
     */
    long getUserTotalSpace(long ownerId);
    
    /**
     * 获取指定库表的所有文件及文件版本
     * 
     * @param dbNum
     * @param tableNum
     * @param limit
     * @return
     */
    List<INode> listFileAndVersions(int dbNum, int tableNum, Limit limit);
    
    /**
     * 顺序列举节点
     * 
     * @param userdbNumber
     * @param tableNumber
     * @param limit
     * @return
     */
    List<INode> lstContentNode(int userdbNumber, int tableNumber, Limit limit);
    
    /**
     * 列举对象
     * 
     * @param status
     * @param userdbNumber
     * @param tableNumber
     * @param lastModified
     * @param limit
     * @return
     */
    List<INode> lstDeleteNode(int userdbNumber, int tableNumber, Date lastModified, Limit limit);
    
    /**
     * 列举有內容的节点
     * 
     * @param userId
     * @param status
     * @param limit
     * @return
     */
    List<INode> lstFileAndVersionNode(long userId, Limit limit);
    
    /**
     * 彻底删除文件夹
     * 
     * @param status
     * @param userdbNumber
     * @param tableNumber
     * @param lastModified
     * @return
     */
    int reallyDeleteFolderNode(int userdbNumber, int tableNumber, Date lastModified);
    
    /**
     * 检索文件名的节点
     * 
     * @param ownerId
     * @param name
     * @param order
     * @param limit
     * @return
     */
    List<INode> searchNodeByName(long ownerId, String name, OrderV1 order, Limit limit);
    
    /**
     * 修改INode对象
     * 
     * @param iNode 必须设置id和ownedBy
     */
    int update(INode iNode);
    
    int updateForRename(INode iNode);
    
    int updateNameAndSyncVersion(INode iNode);
    
    int updateVersionNum(INode iNode);
    
    int updateForRestore(INode iNode);
    
    int updateForMove(INode iNode);
    
    int updateAllINodeStatus(INode iNode);
    
    /**
     * 彻底删除用户所有文件
     * 
     * @param ownerId
     * @return
     */
    int updateAllNodesStatusToDelete(Long ownerId);
    
    /**
     * 根据原ID更新
     * 
     * @param id
     * @param parentId
     * @param originalId
     * @return
     */
    int updateByOriginalId(INode node, long originalId);
    
    /**
     * 更新节点
     * 
     * @param iNode
     */
    void updateForUploadFile(INode iNode);
    
    // /**
    // * 通过对象ID更新节点
    // *
    // * @param iNode
    // */
    // int updateINodeByObjectId(INode iNode);
    
    /**
     * 更新链接CODE
     * 
     * @param iNode
     * @return
     */
    int updateINodeLinkCode(INode iNode);
    
    /**
     * 更新状态
     * 
     * @param iNode
     * @return
     */
    int updateINodeShareStatus(INode iNode);
    
    /**
     * 只更新节点状态
     * 
     * @param iNode
     */
    int updateINodeStatus(INode iNode);
    
    int updateStatusByParent(INode inode);
    
    int batchUpdateStatusByParentList(INode inode, List<Long> parentList);
    
    /**
     * 根据父节点查询非删除状态的INODE列表
     * 
     * @param ownerId
     * @param parentList
     * @return
     */
    List<INode> batchQueryNormalByParentAndStatus(Long ownerId, List<INode> parentList);
    
    /**
     * 
     * @param iNode
     * @return
     */
    int updateINodeStatusToDelete(INode iNode);
    
    /**
     * 修改节点状态为真实删除
     * 
     * @param iNode
     * @return
     */
    int updateStatusToRealDelete(INode iNode);
    
    /**
     * 更新节点同步状态
     * 
     * @param iNode
     * @return
     */
    int updateINodeSyncVersion(INode iNode);
    
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
     */
    int updateObjectForMerge(long size, String sha1, String objId, long ownerId);
    
    /**
     * 更新INode中的最后修改时间
     * 
     * @param iNode
     */
    void updateObjectModifiedAt(INode iNode);
    
    /**
     * 更新节点及子节点状态
     * 
     * @param iNode
     * @return
     */
    int updateSubINodeStatus(INode iNode);
    
    /**
     * 只更新子节点状态
     * 
     * @param iNode
     */
    int updateSubINodeStatusByParent(INode iNode);
    
    /**
     * 更新回收站状态，如果知道ID，则更新指定的ID及其子；不指定这根据用户更新
     * 
     * @param parentNode
     * @param id
     * @return
     */
    int updateTrashAllNodesStatus(INode parentNode, Long id);
    
    /**
     * 替换INODE的对象，如果ID发生改变了，则不替换
     * 
     * @param node
     * @param objRf
     * @return
     */
    int replaceObjectForINode(INode node, ObjectReference objRf);
    
    /**
     * 列举每张表中同一个资源组的文件的总数和总大小
     * 
     * @return
     */
    Map<Long, Long> lstFilesNumAndSizesByResourceGroup(int userdbNumber, int tableNumber,
        int resourceGroupId);

	List<INode> getChiledrenNodes(INode iNode);

}
