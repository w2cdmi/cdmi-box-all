package com.huawei.sharedrive.uam.security.dao;

import java.util.List;

import com.huawei.sharedrive.uam.security.domain.SecurityFactor;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface SecurityFactorDAO
{
    
    void insert(SecurityFactor securityFactor);
    
    void delete(Integer type, Integer code);
    
    void update(SecurityFactor securityFactor, SecurityFactor oldSecurityFactor);
    
    List<SecurityFactor> getAll(SecurityFactor securityFactor, Order order, Limit limit);
    
    Integer getFilterdCount(SecurityFactor securityFactor);
    
    Integer isExist(SecurityFactor securityFactor);
    
    SecurityFactor getSecurityFactorByCode(Integer type, Integer code);
    
    SecurityFactor getSecurityFactorByName(Integer type, String name);
}
