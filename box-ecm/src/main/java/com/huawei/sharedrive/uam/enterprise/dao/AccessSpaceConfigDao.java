package com.huawei.sharedrive.uam.enterprise.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface AccessSpaceConfigDao
{
    
    void create(AccessSpaceConfig netRegion);
    
    void delete(String id);
    
    long getByDomainExclusiveId(AccessSpaceConfig netRegion);
    
    AccessSpaceConfig getById(String id);
    
    List<AccessSpaceConfig> getFilterd(AccessSpaceConfig netRegion, Order order, Limit limit);
    
    int getFilterdCount(AccessSpaceConfig netRegion);
    
    List<AccessSpaceConfig> getListByOperation(long accountId, long intValue);
    
    AccessSpaceConfig getObject(AccessSpaceConfig spaceConfig);
    
    boolean isDuplicateValues(AccessSpaceConfig netRegion);
    
    void update(AccessSpaceConfig netRegion);
    
    void deleteByCondition(AccessSpaceConfig accessSpaceConfig);
    
    /**
     * 根据用户角色获取用户权限
     * @param accountId  用户属性哪个企业id
     * @param safeRoleId 用户角色id
     * @param intValue	角色配置值对应：uam.accessSpaceConfig的operation字段取模值为0【operation%intValue=0】
     * 						
     * @return
     */
    List<AccessSpaceConfig> getListBySafeRoleId(long safeRoleId,long accountId, long intValue);
    
}
