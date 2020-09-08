package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;

public class RestTeamMemberInfo
{
    private Long id;
    
    private Long teamId;
    
    private String teamRole;
    
    private String role;
    
    private RestTeamMember member;
    
    private RestTeamSpaceInfo teamspace;
    
    public RestTeamMemberInfo()
    {
        
    }
    
    public RestTeamMemberInfo(TeamSpaceMemberships teamMember)
    {
        this.id = teamMember.getId();
        this.teamId = teamMember.getCloudUserId();
        this.teamRole = teamMember.getTeamRole();
        this.role = teamMember.getRole();
        this.member = new RestTeamMember();

        this.member.setType(teamMember.getUserType());
        if(!TeamSpaceMemberships.TYPE_SYSTEM.equals(teamMember.getUserType()))
        {
            this.member.setId(String.valueOf(teamMember.getUserId()));
        }
        
        if (teamMember.getMember() != null)
        {
            this.member.setLoginName(teamMember.getMember().getLoginName());
            this.member.setName(teamMember.getMember().getName());
//            this.member.setDescription(teamMember.getMember().getDepartment());
        }
        this.setTeamspace(new RestTeamSpaceInfo(teamMember.getTeamSpace()));
    }
    
    public Long getTeamId()
    {
        return teamId;
    }
    
    public void setTeamId(Long teamId)
    {
        this.teamId = teamId;
    }
    
    public String getTeamRole()
    {
        return teamRole;
    }
    
    public void setTeamRole(String teamRole)
    {
        this.teamRole = teamRole;
    }
    
    public String getRole()
    {
        return role;
    }
    public void setRole(String role)
    {
        this.role = role;
    }
    
    public RestTeamMember getMember()
    {
        return member;
    }
    
    public void setMember(RestTeamMember member)
    {
        this.member = member;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public RestTeamSpaceInfo getTeamspace()
    {
        return teamspace;
    }
    
    public void setTeamspace(RestTeamSpaceInfo teamspace)
    {
        this.teamspace = teamspace;
    }
    
}
