package com.huawei.sharedrive.app.acl.dao;

import java.util.List;

import com.huawei.sharedrive.app.acl.domain.ResourceRole;

/**
 * 
 * @author c00110381
 *
 */
public interface ResourceRoleDAO
{
    /**
     * 列举资源角色
     * 
     * @return
     */
    List<ResourceRole> listResourceRole();
    
    /**
     * 列举指定创建者的资源角色
     * 
     * @param createdBy
     * @return
     */
    List<ResourceRole> listResourceRole(long createdBy);
    
    /**
     * 
     * 获取资源角色
     * 
     * @param role
     * @return
     */
    ResourceRole get(String role);
    
    /**
     * 增加资源角色
     * 
     * @param role
     */
    void create(ResourceRole role);
    
    /**
     * 更新资源角色
     * 
     * @param role
     * @return
     */
    int update(ResourceRole role);
    
    /**
     * 删除资源角色
     * 
     * @param role
     * @return
     */
    int delete(ResourceRole role);
}
