package com.huawei.sharedrive.app.share.dao;

import java.util.List;

import com.huawei.sharedrive.app.share.domain.INodeLinkDynamic;

public interface INodeLinkDynamicDao
{
    
    INodeLinkDynamic get(String linkCode, String identity);
    
    List<INodeLinkDynamic> list(INodeLinkDynamic iNodeLinkDynamic);
    
    /**
     * 
     * @param iNodeLink
     */
    void create(INodeLinkDynamic iNodeLinkDynamic);
    
    /**
     * 更新外链信息，包括提取码和有效期
     * 
     * @param iNodeLink
     */
    int updatePassword(INodeLinkDynamic iNodeLinkDynamic);
    
    /**
     * 更新外链信息，有效期
     * 
     * @param iNodeLink
     */
    int updateExpiredAt(INodeLinkDynamic iNodeLinkDynamic);
    
    /**
     * 升级提取码的加密方式
     * 
     * @param iNodeLink
     */
    int upgradePassword(INodeLinkDynamic iNodeLinkDynamic);
    
    /**
     * 删除指定节点ID的外链
     * 
     * @param iNodeLink
     */
    int delete(String linkCode, String identity);
    
    /**
     * 删除指定节点ID的外链
     * 
     * @param iNodeLink
     */
    int deleteAll(String linkCode);
    
}
