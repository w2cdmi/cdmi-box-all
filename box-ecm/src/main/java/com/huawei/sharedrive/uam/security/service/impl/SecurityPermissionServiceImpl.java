package com.huawei.sharedrive.uam.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.security.dao.SecurityMatrixDAO;
import com.huawei.sharedrive.uam.security.dao.SecurityPermissionDAO;
import com.huawei.sharedrive.uam.security.domain.SecurityPermission;
import com.huawei.sharedrive.uam.security.service.SecurityPermissionService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.config.service.ConfigManager;

@Component("scurityPermissionService")
public class SecurityPermissionServiceImpl implements SecurityPermissionService
{
    
    @Autowired
    private SecurityPermissionDAO permissionDAO;
    
    @Autowired
    private SecurityMatrixDAO securityMatrixDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    @Override
    public String insert(SecurityPermission permission)
    {
        if (permissionDAO.isExist(permission) == 0)
        {
            permissionDAO.insert(permission);
            configManager.setConfig(SecurityPermissionServiceImpl.class.getSimpleName(), null);
            return RETURN_SUCCESS;
        }
        return RETURN_EXIST;
    }
    
    @Override
    public String delete(String permissionDesc)
    {
        Integer count = securityMatrixDAO.isCiteByPermissionValue(permissionDesc);
        if (count == 0)
        {
            permissionDAO.delete(permissionDesc);
            configManager.setConfig(SecurityPermissionServiceImpl.class.getSimpleName(), null);
            return RETURN_SUCCESS;
        }
        return RETURN_QUOTE;
    }
    
    @Override
    public String update(SecurityPermission permission, SecurityPermission oldPermission)
    {
        if (oldPermission.getPermissionDesc().equals(permission.getPermissionDesc()))
        {
            permissionDAO.update(permission, oldPermission);
            configManager.setConfig(SecurityPermissionServiceImpl.class.getSimpleName(), null);
            return RETURN_SUCCESS;
        }
        else
        {
            Integer count = securityMatrixDAO.isCiteByPermissionValue(oldPermission.getPermissionDesc());
            if (count == 0)
            {
                if (permissionDAO.isExist(permission) == 0)
                {
                    permissionDAO.update(permission, oldPermission);
                    configManager.setConfig(SecurityPermissionServiceImpl.class.getSimpleName(), null);
                    return RETURN_SUCCESS;
                }
                return RETURN_EXIST;
            }
            else
            {
                return RETURN_QUOTE;
            }
        }
    }
    
    @Override
    public Page<SecurityPermission> queryPage(SecurityPermission permission, PageRequest pageRequest)
    {
        int total = permissionDAO.getFilterdCount(permission);
        List<SecurityPermission> content = permissionDAO.getAll(permission,
            pageRequest.getOrder(),
            pageRequest.getLimit());
        Page<SecurityPermission> page = new PageImpl<SecurityPermission>(content, pageRequest, total);
        return page;
    }
    
    @Override
    public Integer isExist(SecurityPermission permission)
    {
        return permissionDAO.isExist(permission);
    }
    
    @Override
    public SecurityPermission getByPermissionDesc(String permissionDesc)
    {
        return permissionDAO.getByPermissionDesc(permissionDesc);
    }
    
    @Override
    public SecurityPermission getByKeyName(String keyName)
    {
        return permissionDAO.getByKeyName(keyName);
    }
}
