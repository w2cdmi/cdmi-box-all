package com.huawei.sharedrive.app.acl.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.acl.dao.ResourceRoleDAO;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.acl.service.ResourceRoleService;

@Component
public class ResourceRoleServiceImpl implements ResourceRoleService
{
    @Autowired
    private ResourceRoleDAO resourceRoleDAO;
    
    @Override
    public List<ResourceRole> listResourceRoleSetting()
    {
        return resourceRoleDAO.listResourceRole();
    }

    @Override
    public ResourceRole getResourceRole(String role)
    {
        return resourceRoleDAO.get(role);
    }
    
}
