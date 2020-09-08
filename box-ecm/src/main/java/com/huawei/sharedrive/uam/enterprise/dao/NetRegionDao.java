package com.huawei.sharedrive.uam.enterprise.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface NetRegionDao
{
    
    long create(NetRegion netRegion);
    
    boolean isDuplicateValues(NetRegion netRegion);
    
    int getFilterdCount(NetRegion netRegion);
    
    List<NetRegion> getFilterd(NetRegion netRegion, Order order, Limit limit);
    
    NetRegion getById(long id);
    
    long getByDomainExclusiveId(NetRegion netRegion);
    
    void updateEnterpriseInfo(NetRegion netRegion);
    
    long getMaxId();
    
    void deleteNetRegion(NetRegion netRegionIp);
    
}
