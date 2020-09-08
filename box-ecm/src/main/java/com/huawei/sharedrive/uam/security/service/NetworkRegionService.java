package com.huawei.sharedrive.uam.security.service;

import com.huawei.sharedrive.uam.security.domain.NetworkRegion;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface NetworkRegionService
{
    
    void save(NetworkRegion networkRegion);
    
    /**
     * 
     * @param id
     */
    void delete(long id);
    
    /**
     * 
     * @param networkRegion
     * @param request
     * @return
     */
    Page<NetworkRegion> query(NetworkRegion networkRegion, PageRequest request);
    
    /**
     * 
     * @param id
     * @return
     */
    NetworkRegion getById(Long id);
    
    /**
     * 
     * @param networkRegion
     */
    void update(NetworkRegion networkRegion);
    
    /**
     * 
     * @return
     */
    long getNextId();
    
    /**
     * 
     * @param networkRegion
     * @return
     */
    int uniquelyCheck(NetworkRegion networkRegion);
    
    NetworkRegion getByIpValue(long ipValue);
    
}
