package pw.cdmi.box.disk.teamspace.domain;

import pw.cdmi.core.exception.InvalidParamException;

public class RestTeamMemberModifyRequest
{
    private String teamRole;
    
    private String role;
    
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
    
    public void checkParameter() throws InvalidParamException
    {
        
    }
}
