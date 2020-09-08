/*
 * Copyright Huawei Technologies Co.,Ltd. 2013-2014. All rights reserved.
 */
package com.huawei.sharedrive.app.share.dao;

import java.util.List;

import com.huawei.sharedrive.app.share.domain.INodeShare;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * 共享者DAO
 * 
 * @author l90003768
 * 
 */
public interface ShareDAO
{
    
    
    /**
     * 删除某资源的单条共享关系
     * 
     * @param iNodeShare
     */
    int deleteByInodeAndSharedUser(INodeShare iNodeShare);
    
    int deleteByCreated(long created);
    
    
    /**
     * 获取正常状态共享列表
     * 
     * @param ownerId
     * @param inodeId
     * @param linkCode 
     * @param l 
     * @return
     */
    List<INodeShare> getShareList(long ownerId, long createdBy, long inodeId, String linkCode);
    
    
    /**
     * 分页获取正常状态共享列表
     * 
     * @param inodeShare
     * @param order
     * @param limit
     * @return
     */
    List<INodeShare> getSharePageList(INodeShare inodeShare, List<Order> orderList, Limit limit);
    
    /**
     * 保存共享关系
     * 
     * @param inodeShare
     */
    void saveINodeShare(INodeShare inodeShare);
    
    
    /**
     * 更新共享资源的名称和大小
     * 
     * @param ownedId
     * @param inodeId
     * @param newName
     * @param size
     */
    int updateNodeInfo(long ownedId,long createdBy, long inodeId, String newName, long size);
    
    
    /**
     * 更新共享Owner名称
     * 
     * @param ownerId
     * @param newName
     */
    int updateOwnerName(long ownerId,long createdBy, String newName);
    
    int updateRoleName(INodeShare tempNewShare);
    
    /**
     * 更新状态
     * 
     * @param ownerId
     * @param inodeId
     * @param status
     */
    int updateStatus(long ownerId, long createdBy, long inodeId, byte status);
    
    /**
     * 分页获取共享给我的正常状态共享列表
     * 
     * @param sharedUserId
     * @param tag 
     * @param page
     * @param pageSize
     * @return
     */
    List<INodeShare> getListV2(long sharedUserId, byte userType, String searchName, String order, Limit limit, String tag);

    int getShareCountForPage(INodeShare inodeShare);

    List<INodeShare> getForwardRecord(INodeShare share);

	int deleteByInode(long ownerId, long createdBy, long inodeId);

	List<INodeShare> getAllByCreated(long userId);

	Long getMySharesTotals(long createdBy, byte shareUserType, String transferStringForSql, String shareType);

	int getShareCountForInode(long ownerId, long createdBy, long inodeId);

	List<INodeShare> getShareListIgnoreStatus(long ownerId, long createBy, long inodeId);
}
