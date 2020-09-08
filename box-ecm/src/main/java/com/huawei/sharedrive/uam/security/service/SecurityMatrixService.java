package com.huawei.sharedrive.uam.security.service;

import java.util.List;

import com.huawei.sharedrive.uam.security.domain.SecurityMatrix;
import com.huawei.sharedrive.uam.security.domain.SecurityMatrixQueryCondition;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface SecurityMatrixService
{
    
    String RETURN_SUCCESS = "SUCCESS";
    
    String RETURN_EXIST = "EXIST";
    
    String insert(SecurityMatrix securityMatrix);
    
    String delete(SecurityMatrix securityMatrix);
    
    String update(SecurityMatrix securityMatrix, SecurityMatrix oldSecurityMatrix);
    
    Page<SecurityMatrix> queryPage(SecurityMatrixQueryCondition queryCondition, PageRequest pageRequest);
    
    SecurityMatrix getSecurityMatrix(SecurityMatrix securityMatrix);
    
    Integer isExist(SecurityMatrix securityMatrix);
    
    Integer isCiteByPermissionValue(String permissionValue);
    
    /**
     * 
     * @param SecurityMatrix queryObject
     * @return
     */
    List<SecurityMatrix> queryMatrixByNFactor(SecurityMatrix queryObject);
    
    Integer isCiteByResExtendType(Integer type, Integer code);
    
    boolean hasEnabledSecurityMartix();
    
    String getSecurityMartixRoleNames();
}
