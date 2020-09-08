/**
 * 
 */
package com.huawei.sharedrive.app.dataserver.dao;

import java.util.List;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;

/**
 * @author q90003805
 * 
 */
public interface ResourceGroupDao
{
    void create(ResourceGroup group);
    
    void delete(Integer id);
    
    /**
     * 更加DC删除resourcegroup
     * 
     * @param dcid
     */
    void deleteByDC(int dcid);
    
    ResourceGroup get(Integer id);
    
    ResourceGroup get(String managerIP, int managerPort);
    
    List<ResourceGroup> getAll();
    
    List<ResourceGroup> getAllByDC(int dcid);
    
    List<ResourceGroup> getAllByRegion(int regionID);
    
    int getNextAvailableDataCenterId();
    
    void update(ResourceGroup group);
    
    void updateDomainNameByDc(int dcid, String domainName);
    
    void updateRegionByDC(int dcid, int regionID);
    
    /**
     * 更新运行时状态
     * 
     * @param dcid
     * @param runtimeStatus
     */
    void updateRuntimeStatus(int dcid, int runtimeStatus);
    
    void updateStatistic(ResourceGroup resourceGroup);
    
    /**
     * 更新状态
     * 
     * @param dcid
     * @param status
     */
    void updateStatus(int dcid, int status);
    
}