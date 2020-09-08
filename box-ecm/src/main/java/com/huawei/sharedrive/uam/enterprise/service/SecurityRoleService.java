package com.huawei.sharedrive.uam.enterprise.service;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface SecurityRoleService
{
    
    long create(SecurityRole securityRole);
    
    void delete(SecurityRole securityRole);
    
    boolean isDuplicateValues(SecurityRole securityRole);
    
    Page<SecurityRole> getFilterd(SecurityRole securityRole, PageRequest pageRequest);
    
    SecurityRole getById(long id);
    
    long getByDomainExclusiveId(SecurityRole securityRole);
    
    void updateSecurityRole(SecurityRole securityRole);
    
    List<SecurityRole> getFilterdList(SecurityRole securityRole);
}
