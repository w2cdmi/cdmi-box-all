package com.huawei.sharedrive.uam.security.dao;

import java.util.List;

import com.huawei.sharedrive.uam.security.domain.NetworkRegion;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface NetworkRegionDAO
{
    
    /**
     * @param networkRegion
     */
    void insert(NetworkRegion networkRegion);
    
    /**
     * @param id
     */
    void delete(long id);
    
    /**
     * @param networkRegion
     * @param order
     * @param limit
     * @return
     */
    List<NetworkRegion> query(NetworkRegion networkRegion, Order order, Limit limit);
    
    /**
     * @param id
     * @return
     */
    NetworkRegion getById(Long id);
    
    /**
     * @param networkRegion
     */
    void update(NetworkRegion networkRegion);
    
    /**
     * @param networkRegion
     * @return
     */
    int queryCount(NetworkRegion networkRegion);
    
    /**
     * @return
     */
    long getNextAvailableNetworkRegionId();
    
    /**
     * @param networkRegion
     * @return
     */
    int uniquelyCheck(NetworkRegion networkRegion);
    
    NetworkRegion getByIpValue(Long ipValue);
    
}
