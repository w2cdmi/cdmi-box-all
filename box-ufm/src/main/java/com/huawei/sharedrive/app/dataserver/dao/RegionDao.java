/**
 * 
 */
package com.huawei.sharedrive.app.dataserver.dao;

import java.util.List;

import com.huawei.sharedrive.app.dataserver.domain.Region;

import pw.cdmi.box.dao.BaseDAO;

/**
 * @author q90003805
 * 
 */
public interface RegionDao extends BaseDAO<Region, Integer>
{
    List<Region> getAll();
    
    Region getDefaultRegion();
    
    int getNextAvailableRegionId();
    
}