package com.huawei.sharedrive.uam.security.dao;

import java.util.List;

import com.huawei.sharedrive.uam.security.domain.SecurityMatrix;
import com.huawei.sharedrive.uam.security.domain.SecurityMatrixQueryCondition;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface SecurityMatrixDAO
{
    
    void insert(SecurityMatrix securityMatrix);
    
    void delete(SecurityMatrix securityMatrix);
    
    void update(SecurityMatrix securityMatrix, SecurityMatrix oldSecurityMatrix);
    
    List<SecurityMatrix> getAll(SecurityMatrixQueryCondition queryCondition, Order order, Limit limit);
    
    Integer getFilterdCount(SecurityMatrixQueryCondition queryCondition);
    
    SecurityMatrix getSecurityMatrix(SecurityMatrix securityMatrix);
    
    Integer isExist(SecurityMatrix securityMatrix);
    
    Integer isCiteByPermissionValue(String permissionValue);
    
    List<SecurityMatrix> queryMatrixByNFactor(SecurityMatrix queryObject);
    
    Integer isCiteByResExtendType(Integer type, Integer code);
    
    boolean hasEnabledSecurityMartix();
    
    @SuppressWarnings("rawtypes")
    List getSecurityMartixRoleNames();
}
