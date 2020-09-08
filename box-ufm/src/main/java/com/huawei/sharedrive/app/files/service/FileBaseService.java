package com.huawei.sharedrive.app.files.service;

import java.util.List;

import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.FileBasicConfig;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectFingerprintIndex;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

import pw.cdmi.box.domain.Limit;

public interface FileBaseService
{
    
    long buildINodeID(long ownerId);
    
    void checkMaxVersions(Long ownerId, Long nodeId) throws BaseRunException;
    
    boolean checkNodeIsSubNode(INode srcFolder, INode dstNode) throws BaseRunException;
    
    boolean checkObjectInUserStorRegion(long ownerId, ObjectReference objRef);
    
    /**
     * 用户空间及文件数校验
     * 
     * @param ownerId
     * @param appId
     * @throws BaseRunException
     */
    void checkSpaceAndFileCount(Long ownerId, long accountId) throws BaseRunException;
    
    /**
     * 用户可用空间校验
     * 
     * @param accountId
     * @param userId
     * @param fileSize
     * @throws BaseRunException
     */
    void checkSpaceAndFileCount(Long userId, long accountId, long fileSize) throws BaseRunException;
    
    /**
     * 文件版本数超过限制时, 删除最早的历史版本
     * 
     * @param node
     * @throws BaseRunException
     */
    void cleanEarliestVersions(INode node) throws BaseRunException;
    
    INode createINode(INode inode) throws BaseRunException;
    
    void createNode(INode node);
    
    INode createNode(long userId, INode srcFile, INode destFolder);
    
    void createObjectFrIndex(ObjectReference objRef, int regionId);
    
    ObjectReference createObjectRef(INode fileNode);
    
    int updateKiaLabel(ObjectReference objRef);
    
    int decreaseRefObjectCount(ObjectReference objRf);
    
    void deleteCreatingNode(long ownerID, String objectID) throws BaseRunException;
    
    void deleteINode(INode node);
    
    void deleteObjectFrIndex(ObjectReference objectReference);
    
    int deleteObjectRef(ObjectReference objRef);
    
    INode getAndCheckNode(Long ownerId, Long nodeId, byte type) throws BaseRunException;
    
    INode getAndCheckNode(UserToken token, Long ownerId, Long nodeId, byte type) throws BaseRunException;
    
    INode getAndCheckNodeForDeleteFile(Long ownerId, Long nodeId) throws BaseRunException;
    
    /**
     * 获取节点信息, 并校验状态和类型(用于获取节点路径接口)
     * 
     * @param ownerId 节点所有者ID
     * @param nodeId 节点ID
     * @return 节点信息
     * @throws BaseRunException 当节点不存在、节点状态不为正常/回收站/回收站子节点状态、类型为文件版本类型时抛出异常
     */
    INode getAndCheckNodeForGetNodePath(Long ownerId, Long nodeId) throws BaseRunException;
    
    INode getAndCheckParentNode(INode node) throws BaseRunException;
    
    /**
     * 获取节点下载地址
     * 
     * @param accessRegionId
     * @param userId
     * @param inode
     * @return
     * @throws BaseRunException
     */
    DataAccessURLInfo getDownURLByNearAccess(Integer accessRegionId, long userId, INode inode)
        throws BaseRunException;
    
    /**
     * 提供公共方法获取文件的基础不配置信息：是否覆盖acl，是否允许批量操作
     * 
     * @param user
     * @return
     */
    FileBasicConfig getFileBaiscConfig(UserToken user);
    
    INode getINodeInfo(long ownerId, long inodeId) throws BaseRunException;
    
    INode getINodeInfo(long ownerId, long parentNodeId, String nodeName) throws BaseRunException;
    
    INode getINodeInfoCheckStatus(long ownerId, long inodeId, byte status) throws BaseRunException;
    
    DataAccessURLInfo getINodeInfoDownURL(long userId, INode inode) throws BaseRunException;
    
    DataAccessURLInfo getINodeInfoDownURL(long userId, INode inode, String objectId) throws BaseRunException;
    
    DataAccessURLInfo getINodeInfoDownURLWithoutName(long userId, INode inode) throws BaseRunException;
    
    List<INode> getNodeByObjectId(long ownerId, String objectId);
    
    ObjectReference getObjectRefByMD5CheckRID(int resourceGroupID, String md5, String blockMD5, long size)
        throws BaseRunException;
    
    ObjectReference getObjectRefByObjectIDCheckRID(int regionID, String objectId, long size)
        throws BaseRunException;
    
    ObjectReference getObjectRefBysha1CheckRID(int resourceGroupID, String sha1, long size)
        throws BaseRunException;
    
    INode getParentINodeInfoCheckStatus(long ownerId, long inodeId, byte status) throws BaseRunException;
    
    long getUserSyncVersion(long userId);
    
    int increaseObjectRefCount(ObjectReference objRf);
    
    boolean isSameNameNodeExist(Long ownerId, Long folderId, String name) throws BaseRunException;
    
    boolean isSameNameNodeExistNoSelf(Long ownerId, Long parentId, Long nodeId, String name)
        throws BaseRunException;
    
    INode moveBaseNodeToFolder(long userId, INode iNode, INode folderNode);
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    void sendINodeEvent(UserToken user, EventType type, INode srcNode, INode destNode,
        UserLogType userLogType, String[] logParams, String keyword);
    
    void setNodeSyncStatus(INode node, INode folderNode);
    
    void setNodeSyncStatusAndVersion(long userId, INode node, INode folderNode);
    
    void setNodeSyncVersion(long userId, INode node);
    
    void setSubNodeSyncVersionByRecursive(INode parentNode);
    
    
    /**
     * 列举不在这些资源组的对象
     * @param userId
     * @param limit
     * @param lstResourceGroups，不在这些资源组的对象
     * @return
     */
    List<INode> lstContentsNodeFilterRGs(long userId, Limit limit,List<ResourceGroup> lstResourceGroups );
    
    /**
     * 替换对象的Object,如果ID发生变化则不替换
     * @param node
     * @param objRf
     * @return
     */
    boolean replaceObjectForINode(INode node,ObjectReference objRf);

	List<INode> getChildrensInods(Long ownerId, long id, byte typeAll);

	void checkVersionFileSize(long userId, long size) throws BaseRunException;

	void checkVersionFileType(long userId, String type) throws BaseRunException;

	List<ObjectFingerprintIndex> getObjectPf(String md5, int regionId);

	INode getINodeInfo(long ownerId, long inodeid, byte type) throws BaseRunException;

}
