/**
 * 
 */
package com.huawei.sharedrive.isystem.cluster.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup;

/**
 * @author q90003805
 * 
 */
public interface ResourceGroupDao
{
    ResourceGroup get(String managerIP, int managerPort);
    
    ResourceGroup get(Integer id);
    
    List<ResourceGroup> getAll();
    
    List<ResourceGroup> getAllByDC(int dcid);
    
    List<ResourceGroup> getAllByRegion(int regionID);
    
    /**
     * 更加DC删除resourcegroup
     * @param dcid
     */
    void deleteByDC(int dcid);
    
    void updateRegionByDC(int dcid, int regionID);
    
    /**
     * 更新状态
     * @param dcid
     * @param status
     */
    void updateStatus(int dcid, int status);
    
    /**
     * 更新数据中心读写状态：0读写，1只读
     * @param dcid
     * @param status
     */
    void updataRWStatus(int dcid, int status);
    
    void delete(Integer id);
    
    void create(ResourceGroup group);
    
    void update(ResourceGroup group);
    
    void updateStatistic(ResourceGroup resourceGroup);
    
    int getNextAvailableDataCenterId();
    
}