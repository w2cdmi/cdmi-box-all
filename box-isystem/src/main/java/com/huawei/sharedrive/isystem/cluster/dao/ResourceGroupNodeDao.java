/**
 * 
 */
package com.huawei.sharedrive.isystem.cluster.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode;

/**
 * 管理资源组节点
 * 
 * @author q90003805
 * 
 */
public interface ResourceGroupNodeDao
{
    /**
     * 更加DC删除resourcegroup节点
     * 
     * @param dcid
     */
    void deleteByDC(int dcid);
    
    /**
     * 更新节点所属的区域
     * 
     * @param dcid
     * @param regionID
     */
    void updateRegionByDC(int dcid, int regionID);
    
    /**
     * 删除节点
     * 
     * @param groupID
     * @param nodeName
     */
    void delete(ResourceGroupNode node);
    
    void create(ResourceGroupNode node);
    
    void update(ResourceGroupNode node);
    
    List<ResourceGroupNode> getResourceGroupNodeByDcID(int dcid);
    
    ResourceGroupNode getResourceGroupNodeByManagerIP(String managerIP);
    
    ResourceGroupNode getResourceGroupNodeByInnerIP(String inneraddr);
    
}