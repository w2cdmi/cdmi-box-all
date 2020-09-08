/**
 * 
 */
package com.huawei.sharedrive.isystem.cluster.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.cluster.domain.Region;

import pw.cdmi.box.dao.BaseDAO;

/**
 * @author q90003805
 * 
 */
public interface RegionDao extends BaseDAO<Region, Integer>
{
    List<Region> getAll();
    
    Region getDefaultRegion();
    
    Region findByCode(String code);
    
    int getNextAvailableRegionId();
    
}