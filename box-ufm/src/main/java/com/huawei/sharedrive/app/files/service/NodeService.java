/**
 * 
 */
package com.huawei.sharedrive.app.files.service;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.node.ListFolderRequest;
import pw.cdmi.box.domain.Order;

/**
 * 节点相关操作业务接口
 * 
 * @version CloudStor CSE Service Platform Subproject, 2014-5-12
 * @see
 * @since
 */
public interface NodeService
{
    
    /**
     * 删除文件/文件夹/版本文件
     * 
     * @param user
     * @param iNode
     * @throws BaseRunException
     */
    void deleteNode(UserToken user, INode iNode) throws BaseRunException;
    
    /**
     * 彻底删除用户所有文件
     * 
     * @param ownerId
     */
    void deleteUserAllNodes(Long ownerId);
    
    /**
     * 根据名称查询正常状态节点
     * 
     * @param user
     * @param ownerId
     * @param parentId
     * @param name
     * @return
     * @throws BaseRunException
     */
    List<INode> getNodeByName(UserToken user, long ownerId, long parentId, String name)
        throws BaseRunException;
    
    List<INode> getNodePath(long ownerId, long nodeId) throws BaseRunException;
    
    /**
     * 获取节点路径, 按目录结构由深至浅将iNode对象放入集合
     * 
     * @param token
     * @param ownerId
     * @param nodeId
     * @return
     * @throws BaseRunException
     */
    List<INode> getNodePath(UserToken token, long ownerId, long nodeId) throws BaseRunException;
    
    /**
     * 重命名&设置节点同步状态, 文件/文件夹重命名接口共用此方法
     * 
     * @param userToken
     * @param ownerId
     * @param nodeId
     * @param newName
     * @param isSync
     * @param type
     * @return
     * @throws BaseRunException
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    INode renameAndSetSyncStatus(UserToken userToken, long ownerId, INode node, String newName,
        Boolean isSync, byte type) throws BaseRunException;
    
    /**
     * 文件/目录搜索
     * 
     * @param user
     * @param ownerId
     * @param name
     * @param offset
     * @param limit
     * @param orderList
     * @param thumbnailList
     * @return
     * @throws BaseRunException
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    FileINodesList search(UserToken user, long ownerId, String name, long offset, int limit,
        List<Order> orderList, List<Thumbnail> thumbnailList, boolean withExtraType) throws BaseRunException;

    
    /**
     * 文件/目录搜索(过滤掉没权限的)
     * 
     * @param user
     * @param ownerId
     * @param name
     * @param offset
     * @param limit
     * @param orderList
     * @param thumbnailList
     * @return
     * @throws BaseRunException
     */
	FileINodesList searchWithAuth(UserToken user, long ownerId, String name, long offset, int limit,
			List<Order> orderList, List<Thumbnail> thumbnailList, boolean withExtraType) throws BaseRunException;

	INode renameVersionNode(UserToken userToken, long ownerId, INode node, String newName, Boolean isSync, byte type)
			throws BaseRunException;
    
    /**
     * 文件/目录搜索
     * @param user
     * @param searchRequest
     * @return
     * @throws BaseRunException
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    FileINodesList search(UserToken user, long ownerId, ListFolderRequest searchRequest) throws BaseRunException;

}
