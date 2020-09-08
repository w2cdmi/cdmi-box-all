package com.huawei.sharedrive.uam.enterprise.manager.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.manager.SecurityRoleManager;
import com.huawei.sharedrive.uam.enterprise.service.SecurityRoleService;
import com.huawei.sharedrive.uam.exception.ExistSecurityRoleConflictException;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

@Component
public class SecurityRoleManagerImpl implements SecurityRoleManager
{
    
    @Autowired
    private SecurityRoleService securityRoleService;
    
    @Override
    public long create(SecurityRole securityRole) throws IOException
    {
        
        if (securityRoleService.isDuplicateValues(securityRole))
        {
            throw new ExistSecurityRoleConflictException();
        }
        long id = securityRoleService.create(securityRole);
        
        return id;
        
    }
    
    @Override
    public Page<SecurityRole> getFilterd(SecurityRole filter, PageRequest pageRequest)
    {
        return securityRoleService.getFilterd(filter, pageRequest);
    }
    
    @Override
    public SecurityRole getById(long id)
    {
        return securityRoleService.getById(id);
    }
    
    @Override
    public void updateEnterpriseInfo(SecurityRole securityRole)
    {
        
        securityRoleService.updateSecurityRole(securityRole);
        
    }
    
    @Override
    public long getByDomainExclusiveId(SecurityRole securityRole)
    {
        
        return securityRoleService.getByDomainExclusiveId(securityRole);
    }
    
    @Override
    public void modify(SecurityRole enterprise) throws IOException
    {
        securityRoleService.updateSecurityRole(enterprise);
    }
    
    @Override
    public List<SecurityRole> getFilterdList(SecurityRole filter)
    {
        return securityRoleService.getFilterdList(filter);
    }
    
    @Override
    public void delete(SecurityRole securityRole)
    {
        securityRoleService.delete(securityRole);
    }
}
