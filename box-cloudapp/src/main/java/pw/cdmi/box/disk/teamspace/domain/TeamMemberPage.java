package pw.cdmi.box.disk.teamspace.domain;

public class TeamMemberPage extends BasicACL
{
    private Long teamId;
    
    private String teamRole;

    private String userDesc;

    public Long getTeamId()
    {
        return teamId;
    }

    public String getTeamRole()
    {
        return teamRole;
    }

    public String getUserDesc()
    {
        return userDesc;
    }

    public void setTeamId(Long teamId)
    {
        this.teamId = teamId;
    }

    public void setTeamRole(String teamRole)
    {
        this.teamRole = teamRole;
    }
    
    public void setUserDesc(String userDesc)
    {
        this.userDesc = userDesc;
    }
    
    
}
