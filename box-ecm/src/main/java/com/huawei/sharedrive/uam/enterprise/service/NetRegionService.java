package com.huawei.sharedrive.uam.enterprise.service;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegionIp;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface NetRegionService
{
    
    long create(NetRegion netRegion);
    
    boolean isDuplicateValues(NetRegion netRegion);
    
    Page<NetRegion> getFilterd(NetRegion netRegion, PageRequest pageRequest);
    
    Page<NetRegionIp> getFilterd(NetRegionIp netRegion, PageRequest pageRequest);
    
    NetRegion getById(long id);
    
    long getByDomainExclusiveId(NetRegion netRegion);
    
    void updateSecurityRole(NetRegion netRegion);
    
    void updateSecurityRole(NetRegionIp netRegion);
    
    void updateSecurityRole(NetRegionIp netRegion, long id);
    
    boolean isDuplicateNetConfigValues(NetRegionIp netRegion);
    
    long create(NetRegionIp netRegion);
    
    List<NetRegion> getFilterdList(NetRegion netRegion);
    
    List<NetRegionIp> getFilterdList(NetRegionIp netRegion);
    
    List<NetRegionIp> getNetRegionList(NetRegionIp netRegionIp);
    
    void delete(long id);
    
    NetRegionIp getByIp(long accountId, long ipLong);
    
    NetRegionIp getNetRegionIpById(long id);
    
    void deleteNetRegion(NetRegion netRegion);
    
}
