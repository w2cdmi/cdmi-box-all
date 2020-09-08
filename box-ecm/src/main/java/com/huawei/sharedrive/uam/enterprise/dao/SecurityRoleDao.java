package com.huawei.sharedrive.uam.enterprise.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface SecurityRoleDao
{
    
    long create(SecurityRole securityRole);
    
    boolean isDuplicateValues(SecurityRole securityRole);
    
    int getFilterdCount(SecurityRole securityRole);
    
    List<SecurityRole> getFilterd(SecurityRole securityRole, Order order, Limit limit);
    
    SecurityRole getById(long id);
    
    long getByDomainExclusiveId(SecurityRole securityRole);
    
    void updateEnterpriseInfo(SecurityRole securityRole);
    
    long getMaxId();
    
    void delete(SecurityRole securityRole);
    
}
