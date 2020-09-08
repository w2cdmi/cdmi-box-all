package com.huawei.sharedrive.app.acl.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.acl.dao.ResourceRoleDAO;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("resourceRoleDAO")
@SuppressWarnings("deprecation")
public class ResourceRoleDAOImpl extends AbstractDAOImpl implements ResourceRoleDAO
{
    @SuppressWarnings("unchecked")
    @Override
    public List<ResourceRole> listResourceRole()
    {
        return sqlMapClientTemplate.queryForList("ResourceRole.getAll");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ResourceRole> listResourceRole(long createdBy)
    {
        return sqlMapClientTemplate.queryForList("ResourceRole.getByCreatedBy");
    }

    @Override
    public ResourceRole get(String role)
    {
        return (ResourceRole) sqlMapClientTemplate.queryForObject("ResourceRole.get",role);
    }

    @Override
    public void create(ResourceRole role)
    {
        sqlMapClientTemplate.insert("ResourceRole.insert",role);
    }

    @Override
    public int update(ResourceRole role)
    {
        return sqlMapClientTemplate.update("ResourceRole.update",role);
    }

    @Override
    public int delete(ResourceRole role)
    {
        return sqlMapClientTemplate.delete("ResourceRole.delete",role);
    }
    
}
