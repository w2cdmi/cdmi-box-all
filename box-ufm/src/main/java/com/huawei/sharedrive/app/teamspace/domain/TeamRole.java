package com.huawei.sharedrive.app.teamspace.domain;

/**
 * 团队角色对象，只支持admin,manager,member两种角色
 * @author c00110381
 *
 */
public class TeamRole
{
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_MANAGER = "manager";
    public static final String ROLE_MEMBER = "member";
    
    private String teamRoleString;
    private String description;
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public String getTeamRole()
    {
        return teamRoleString;
    }
    public void setTeamRole(String teamRole)
    {
        this.teamRoleString = teamRole;
    }
    
}
