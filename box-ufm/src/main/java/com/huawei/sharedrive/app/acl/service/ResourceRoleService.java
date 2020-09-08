package com.huawei.sharedrive.app.acl.service;

import java.util.List;

import com.huawei.sharedrive.app.acl.domain.ResourceRole;

public interface ResourceRoleService
{
    /**
     * 获取当前系统角色设置
     * 
     * @return
     */
    List<ResourceRole> listResourceRoleSetting();
    
    /**
     * 获取资源角色信息
     * 
     * @param role
     * @return
     */
    ResourceRole getResourceRole(String role);
}
