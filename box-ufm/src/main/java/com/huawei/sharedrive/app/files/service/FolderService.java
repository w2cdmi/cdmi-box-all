/**
 * 
 */
package com.huawei.sharedrive.app.files.service;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.share.domain.INodeLink;

import pw.cdmi.box.domain.Limit;

/**
 * @author s00108907
 * 
 */
public interface FolderService
{
    /**
     * 取消同步
     * 
     * @param user
     * @param folderNode
     * @throws BaseRunException
     */
    INode cancelFolderSync(UserToken user, INode folderNode) throws BaseRunException;
    
    /**
     * 复制节点到其他目录,由于是新增节点，不用调用共享的重命名接口
     * 
     * @param user
     * @param src
     * @param dest
     * @param newName
     * @return
     * @throws BaseRunException
     */
    
    INode copyNodeToFolder(UserToken user, INode src, INode dest, String newName, boolean valiLinkAccessCode)
        throws BaseRunException;
    
    /**
     * 复制时check节点类型,由于是新增节点，不用调用共享的重命名接口
     * 
     * @param user
     * @param iNode
     * @param parentNode
     * @param newName
     * @param type
     * @return
     * @throws BaseRunException
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    INode copyNodeToFolderCheckType(UserToken user, INode iNode, INode parentNode, String newName, int type,
        boolean valiLinkAccessCode) throws BaseRunException;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    INode copyNodeToFolderCheckType(UserToken user, INode iNode, INode parentNode, String newName, int type,
        boolean valiLinkAccessCode,INodeLink link) throws BaseRunException;
    
    /**
     * 创建文件夹
     * 
     * @param user
     * @param folderNode
     * @return
     * @throws BaseRunException
     */
    INode createFolder(UserToken user, INode folderNode) throws BaseRunException;
    
    INode createFolder(UserToken user, INode folderNode, boolean autoMerge) throws BaseRunException;
    
    /**
     * 内部使用：删除处于creating状态的节点，供清除垃圾数据使用
     * 
     * @param ownerID
     * @param objectID
     * @throws BaseRunException
     */
    void deleteCreatingNode(long ownerID, String objectID) throws BaseRunException;
    
    /**
     * 获取节点信息
     * 
     * @param user
     * @param ownerId
     * @param nodeID
     * @return
     * @throws BaseRunException
     */
    INode getNodeInfo(UserToken user, long ownerId, long nodeID) throws BaseRunException;
    
    /**
     * 获取节点信息:判断类型
     * 
     * @param user
     * @param ownerId
     * @param nodeID
     * @param type
     * @return
     * @throws BaseRunException
     */
    INode getNodeInfoCheckType(UserToken user, long ownerId, long nodeID, int type) throws BaseRunException;
    
    /**
     * 获取节点信息:判断类型
     * 
     * @param user
     * @param ownerId
     * @param nodeID
     * @param type
     * @return
     * @throws BaseRunException
     */
    INode getNodeInfoCheckType(UserToken user, long ownerId, long parentNodeId, String nodeName, int type)
        throws BaseRunException;
    
    INode getNodeInfoCheckTypeV2(long ownerId, long nodeID) throws BaseRunException;
    
    /**
     * 获取节点信息：不校验状态
     * 
     * @param user
     * @param ownerId
     * @param nodeID
     * @return
     * @throws BaseRunException
     */
    INode getNodeNoCheckStatus(UserToken user, long ownerId, long nodeID) throws BaseRunException;
    
    /**
     * 列举子节点 即列举parentid = inode.id的节点
     * 
     * @param user
     * @param inode
     * @param order
     * @param limit
     * @param size
     * @return
     * @throws BaseRunException
     */
    FileINodesList listNodesbyFilter(UserToken user, INode inode, OrderV1 order, Limit limit)
        throws BaseRunException;
    
    /**
     * 列举子文件夹
     * 
     * @param user
     * @param inode
     * @return
     * @throws BaseRunException
     */
    List<INode> listSubFolderNoders(UserToken user, INode inode) throws BaseRunException;
    
    /**
     * 移动节点到目录
     * 
     * @param user
     * @param iNode
     * @param parentNode
     * @param newName
     * @return
     * @throws BaseRunException
     */
    INode moveNodeToFolder(UserToken user, INode iNode, INode parentNode, String newName)
        throws BaseRunException;
    
    /**
     * 移动check类型
     * 
     * @param user
     * @param iNode
     * @param parentNode
     * @param newName
     * @param type
     * @return
     * @throws BaseRunException
     */
    INode moveNodeToFolderCheckType(UserToken user, INode iNode, INode parentNode, String newName, int type)
        throws BaseRunException;
    
    /**
     * rename文件夹或者文件
     * 
     * @param user
     * @param inode
     * @param name
     * @return
     * @throws BaseRunException
     */
    INode renameNode(UserToken user, INode inode, String name) throws BaseRunException;
    
    /**
     * rename：判断文件夹或者文件
     * 
     * @param user
     * @param inode
     * @param name
     * @param type
     * @return
     * @throws BaseRunException
     */
    INode renameNodeCheckType(UserToken user, INode inode, String name, int type) throws BaseRunException;
    
    /**
     * 检索文件名
     * 
     * @param user
     * @param ownerId
     * @param name
     * @param order
     * @param limit
     * @return
     * @throws BaseRunException
     */
    FileINodesList searchNodesbyFilter(UserToken user, long ownerId, String name, OrderV1 order, Limit limit)
        throws BaseRunException;
    
    /**
     * 设置为文件夹为同步目录
     * 
     * @param user
     * @param inode
     * @return
     * @throws BaseRunException
     */
    INode setSyncFolder(UserToken user, INode inode) throws BaseRunException;
    
    /**
     * 更新外链码
     * 
     * @param user
     * @param folderNode
     * @param linkCode
     * @throws BaseRunException
     */
    void updateNodeLinkCode(UserToken user, INode folderNode, String linkCode) throws BaseRunException;
    
    /**
     * 更新外链码（判断文件夹或者文件）
     * 
     * @param user
     * @param folderNode
     * @param linkCode
     * @param type
     * @throws BaseRunException
     */
    void updateNodeLinkCodeCheckType(UserToken user, INode folderNode, String linkCode, byte type)
        throws BaseRunException;
    
    /**
     * 更新共享状态
     * 
     * @param user
     * @param folderNode
     * @param shareStatus
     * @throws BaseRunException
     */
    
    void updateNodeShareStatus(UserToken user, INode folderNode, byte shareStatus) throws BaseRunException;
    
    /**
     * 更新共享状态（判断文件夹或者文件）
     * 
     * @param user
     * @param folderNode
     * @param shareStatus
     * @param type
     * @throws BaseRunException
     */
    void updateNodeShareStatusCheckType(UserToken user, INode folderNode, byte shareStatus, byte type)
        throws BaseRunException;

    INode getSubFolderByName(INode parantNode, String string);

	INode moveNodeToFolderNoCheck(UserToken user, INode iNode, INode parentNode, String newName, int type) throws BaseRunException;
    
}
