package com.huawei.sharedrive.uam.security.service;

import com.huawei.sharedrive.uam.security.domain.SecurityFactor;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface SecurityFactorService
{
    String RETURN_SUCCESS = "SUCCESS";
    
    String RETURN_EXIST = "EXIST";
    
    String RETURN_QUOTE = "QUOTE";
    
    String insert(SecurityFactor securityFactor);
    
    String delete(Integer type, Integer code);
    
    String update(SecurityFactor securityFactor, SecurityFactor oldSecurityFactor);
    
    Page<SecurityFactor> queryPage(SecurityFactor securityFactor, PageRequest pageRequest);
    
    Integer isExist(SecurityFactor securityFactor);
    
    SecurityFactor getSecurityFactorByCode(Integer type, Integer code);
    
    SecurityFactor getSecurityFactorByName(Integer type, String name);
}
