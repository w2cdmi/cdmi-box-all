package com.huawei.sharedrive.app.share.dao;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.share.domain.INodeLink;

import pw.cdmi.box.domain.Limit;

public interface INodeLinkReverseDao
{
    /**
     * 创建INodeLink对象
     * 
     * @param iNodeLink
     */
    void createV2(INodeLink iNodeLink);
    
    /**
     * 删除指定节点ID的外链
     * 
     * @param iNodeLink
     */
    int deleteByOwner(long ownerId);
    
    /**
     * 删除指定节点ID的外链
     * 
     * @param iNodeLink
     */
    int deleteByNode(long ownerId, long nodeId, Byte accessCodeMode);
    
    /**
     * 删除指定外链码的外链
     * 
     * @param iNodeLink
     */
    void deleteV2(INodeLink iNodeLink);
    
    /**
     * 更新外链信息，包括提取码和有效期
     * 
     * @param iNodeLink
     */
    void updateV2(INodeLink iNodeLink);
    
    /**
     * 升级提取码的加密方式
     * 
     * @param iNodeLink
     */
    void upgradePassword(INodeLink iNodeLink);
    
    List<INodeLink> listByNode(long ownerId, long nodeId, Byte accessCodeMode);
    
    int getCountByNode(long ownerId, long nodeId, Byte accessCodeMode);
    
    List<INodeLink> listByOwner(INodeLink filter, OrderV1 order, Limit limit);
    
    int getCountByOwner(INodeLink filter);

	List<String> listAllLinkCodes(long ownerBy);
    
}
