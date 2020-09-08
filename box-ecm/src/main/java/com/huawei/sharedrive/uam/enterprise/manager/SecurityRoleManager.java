package com.huawei.sharedrive.uam.enterprise.manager;

import java.io.IOException;
import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface SecurityRoleManager
{
    
    long create(SecurityRole securityRole) throws IOException;
    
    void modify(SecurityRole securityRole) throws IOException;
    
    Page<SecurityRole> getFilterd(SecurityRole securityRole, PageRequest pageRequest);
    
    SecurityRole getById(long id);
    
    long getByDomainExclusiveId(SecurityRole securityRole);
    
    void updateEnterpriseInfo(SecurityRole securityRole);
    
    List<SecurityRole> getFilterdList(SecurityRole securityRole);
    
    void delete(SecurityRole securityRole);
}
