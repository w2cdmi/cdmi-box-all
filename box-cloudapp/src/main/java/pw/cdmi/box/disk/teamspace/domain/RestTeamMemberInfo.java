package pw.cdmi.box.disk.teamspace.domain;

public class RestTeamMemberInfo
{
    private Long id;
    
    private RestTeamMember member;
    
    private Long teamId;
    
    private String teamRole;
    
    private RestTeamSpaceInfo teamspace;
    
    private String role;
    
    public Long getId()
    {
        return id;
    }
    
    public RestTeamMember getMember()
    {
        return member;
    }
    
    public Long getTeamId()
    {
        return teamId;
    }
    
    public String getTeamRole()
    {
        return teamRole;
    }
    
    public RestTeamSpaceInfo getTeamspace()
    {
        return teamspace;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public void setMember(RestTeamMember member)
    {
        this.member = member;
    }
    
    public void setTeamId(Long teamId)
    {
        this.teamId = teamId;
    }
    
    public void setTeamRole(String teamRole)
    {
        this.teamRole = teamRole;
    }
    
    public void setTeamspace(RestTeamSpaceInfo teamspace)
    {
        this.teamspace = teamspace;
    }
    
    public String getRole()
    {
        return role;
    }
    
    public void setRole(String role)
    {
        this.role = role;
    }
    
}
