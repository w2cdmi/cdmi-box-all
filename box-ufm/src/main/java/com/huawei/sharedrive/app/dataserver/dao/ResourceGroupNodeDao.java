/**
 * 
 */
package com.huawei.sharedrive.app.dataserver.dao;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;

/**
 * 管理资源组节点
 * 
 * @author q90003805
 * 
 */
public interface ResourceGroupNodeDao
{
    void create(ResourceGroupNode node);
    
    /**
     * 删除节点
     * 
     * @param groupID
     * @param nodeName
     */
    void delete(ResourceGroupNode node);
    
    /**
     * 更加DC删除resourcegroup节点
     * 
     * @param dcid
     */
    void deleteByDC(int dcid);
    
    void update(ResourceGroupNode node);
    
    /**
     * 更新节点所属的区域
     * 
     * @param dcid
     * @param regionID
     */
    void updateRegionByDC(int dcid, int regionID);
    
    /**
     * 
     * @param managerIp
     * @return
     */
    ResourceGroupNode getResourceGroupNodeByManagerIp(String managerIp);
    
}