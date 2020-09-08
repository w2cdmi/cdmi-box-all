package com.huawei.sharedrive.app.share.dao;

import java.util.List;

import com.huawei.sharedrive.app.share.domain.INodeLink;

public interface INodeLinkDAO
{
    /**
     * 检测INodeLink是否存在
     * 
     * @param iNode
     * @return boolean
     */
    boolean checkINodeLinkExist(INodeLink iNodeLink);
    
    /**
     * 创建INodeLink对象
     * 
     * @param iNodeLink
     */
    void create(INodeLink iNodeLink);
    
    /**
     * 创建INodeLink对象
     * 
     * @param iNodeLink
     */
    void createV2(INodeLink iNodeLink);
    
    /**
     * 删除指定外链码的外链
     * 
     * @param iNodeLink
     */
    void delete(INodeLink iNodeLink);
    
    /**
     * 删除指定节点ID的外链
     * 
     * @param iNodeLink
     */
    int deleteByOwner(long ownerId);
    
    /**
     * 删除指定外链码的外链
     * 
     * @param iNodeLink
     */
    void deleteV2(INodeLink iNodeLink);
    
    /**
     * 获取指定外链码的外链
     * 
     * @param iNodeLink，需要设置ID属性
     * @return 外链对象
     */
    INodeLink get(INodeLink iNodeLink);
    
    /**
     * 获取指定外链码的外链
     * 
     * @param iNodeLink，需要设置ID属性
     * @return 外链对象
     */
    INodeLink getV2(INodeLink iNodeLink);
    
    /**
     * 更新外链信息，包括提取码和有效期
     * 
     * @param iNodeLink
     */
    void update(INodeLink iNodeLink);
    
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
    
    
}
