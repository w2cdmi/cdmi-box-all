/*
 * Copyright Huawei Technologies Co.,Ltd. 2013-2014. All rights reserved.
 */
package com.huawei.sharedrive.app.share.dao;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.share.domain.INodeShare;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * 被共享者调用的DAO
 * 
 * @author l90003768
 * 
 */
public interface ShareToMeDAO
{
    /**
     * 删除单挑共享关系
     * 
     * @param ownerId
     * @param nodeId
     * @param sharedUserId
     * @param sharedUserType
     */
    int delete(long ownerId, long nodeId, long sharedUserId, byte sharedUserType);
    
    /**
     * 删除资源的单条共享关系 <br/>
     * 根据ownerId, nodeId, sharedUserId和sharedUseType进行删除
     * 
     * @param iNodeShare
     */
    int deleteByInode(INodeShare iNodeShare);
    
    /**
     * 获取指定用户共享给我的正常状态资源ID列表 <br/>
     * 仅返回节点ID数据 <br/>
     * 仅包含状态正常的数据
     * 
     * @param ownerId
     * @param sharedUserId
     * @return
     */
    List<Long> getInodeIdListFromOwner(long ownerId, long sharedUserId, byte sharedUserType);
    
    /**
     * 获得节点共享信息
     * 
     * @param ownerId
     * @param nodeId
     * @param sharedUserId
     * @param sharedUserType
     * @param linkCode 
     * @return
     */
    INodeShare getINodeShare(long ownerId, long nodeId, long sharedUserId, byte sharedUserType, String linkCode);
    
    /**
     * 获取共享给我的所有正常节点列表
     * 
     * @param sharedUserId 被共享用户Id
     * @param sharedUserType 被共享用户类型
     * @return
     */
    List<INodeShare> getList(long sharedUserId, byte shareUserType);
    
    /**
     * 分页获取共享给我的正常状态共享列表
     * 
     * @param sharedUserId
     * @param page
     * @param pageSize
     * @return
     */
    List<INodeShare> getList(long sharedUserId, byte userType, String searchName, OrderV1 order, Limit limit);
    
    /**
     * 获取共享给我的节点列表
     * 
     * @param sharedId
     * @return
     */
    List<INodeShare> getListIgnoreStatus(long sharedId);
    
    /**
     * 分页获取共享给我的正常状态共享列表
     * 
     * @param sharedUserId
     * @param page
     * @param pageSize
     * @return
     */
    List<INodeShare> getListV2(long sharedUserId, byte userType, String searchName, String order, Limit limit);
    
    /**
     * 获取共享给我的正常状态共享列表
     * 
     * @param sharedId
     * @return
     */
    int getShareToMeTotals(long sharedId, byte sharedUserType, String searchName);
    
    /**
     * 保存共享关系
     * 
     * @param nodeShare
     */
    void saveINodeShare(INodeShare nodeShare);
    
    /**
     * 更新文件大小信息
     * 
     * @param sharedUserId
     * @param ownerId
     * @param nodeId
     * @param size
     */
    int updateFileSize(long sharedUserId, long ownerId, long nodeId, long size);
    
    /**
     * 更新共享资源的名称和大小
     * 
     * @param sharedUserId
     * @param ownerId
     * @param nodeId
     * @param newName
     * @param size
     */
    int updateNodeInfo(long sharedUserId, long ownerId, long nodeId, String newName, long size);
    
    /**
     * 更新文件夹或文件名称
     * 
     * @param tempShareNode
     */
    int updateNodeName(INodeShare tempShareNode);
    
    int updateRoleName(INodeShare tempNewShare);
    
    /**
     * 更新被共享者名称
     * 
     * @param sharedUserId
     * @param sharedUserName
     */
    int updateSharedUserName(long sharedUserId, String sharedUserName);
    
    /**
     * 更新共享关系状态
     * 
     * @param tempShare
     * @param status
     */
    int updateStatus(INodeShare tempShare, byte status);

    INodeShare getForwardRecord(INodeShare share);

	List<INodeShare> getListWithDelete(long id, long deptId, byte typeDep, String transferStringForSql, Order order, Limit newLimit);

	int getListWithDeleteTotal(long id, long deptId, byte typeDep, String keyword);
}
