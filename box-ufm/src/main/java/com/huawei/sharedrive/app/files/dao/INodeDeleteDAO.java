/**
 * 
 */
package com.huawei.sharedrive.app.files.dao;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.files.domain.INode;

import pw.cdmi.box.domain.Limit;

/**
 * @author q90003805
 * 
 */
public interface INodeDeleteDAO
{
    
    /**
     * 创建INode对象
     * 
     * @param iNode 必须设置id和ownedBy
     */
    void create(INode iNode);
    
    /**
     * 删除INode对象
     * 
     * @param iNode 必须设置id和ownedBy
     */
    int delete(INode iNode);
    
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
     * @param inodeId
     * @return
     */
    INode get(long ownerId, long inodeId);
    
    /**
     * 根据状态查询Inode
     * 
     * @param filter
     * @param order
     * @param limit
     * @return
     */
    List<INode> getINodeByStatus(INode filter, OrderV1 order, Limit limit);
    
    /**
     * 修改INode对象
     * 
     * @param iNode 必须设置id和ownedBy
     */
    void update(INode iNode);
    
}
