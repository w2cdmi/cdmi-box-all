package com.huawei.sharedrive.app.teamspace.dao;

import java.util.List;

import com.huawei.sharedrive.app.teamspace.domain.TeamRole;

/**
 * 
 * @author c00110381
 *
 */
public interface TeamRoleDAO
{
    /**
     * 获取所有空间角色列表
     * 
     * @return
     */
    List<TeamRole> listTeamRole();
    
    
    /**
     * 获取指定空间角色信息
     * 
     * @param role
     * @return
     */
    TeamRole getTeamRole(String role);
}
