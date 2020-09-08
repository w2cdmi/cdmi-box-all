package com.huawei.sharedrive.uam.enterprise.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.NetRegionIp;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface NetRegionIpDao
{
    
    long create(NetRegionIp netRegionIp);
    
    boolean isDuplicateValues(NetRegionIp netRegionIp);
    
    int getFilterdCount(NetRegionIp netRegionIp);
    
    List<NetRegionIp> getFilterd(NetRegionIp netRegionIp, Order order, Limit limit);
    
    NetRegionIp getById(long id);
    
    long getByDomainExclusiveId(NetRegionIp netRegionIp);
    
    void updateEnterpriseInfo(NetRegionIp netRegionIp);
    
    void delete(long id);
    
    void deleteByCondition(NetRegionIp netRegionIp);
    
    List<NetRegionIp> getListRegionIp(NetRegionIp netRegionIp);
    
    long getMaxId();
    
    NetRegionIp getByIp(long accountId, long ipLong);
    
}
