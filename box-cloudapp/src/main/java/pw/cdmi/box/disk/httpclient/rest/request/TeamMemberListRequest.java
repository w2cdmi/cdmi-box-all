package pw.cdmi.box.disk.httpclient.rest.request;

public class TeamMemberListRequest
{
    
    private Long teamId;
    
    private Integer offset;
    
    private Integer limit;
    
    private String teamRole;
    
    private String token;
    
    public Long getTeamId()
    {
        return teamId;
    }
    
    public void setTeamId(Long teamId)
    {
        this.teamId = teamId;
    }
    
    public Integer getOffset()
    {
        return offset;
    }
    
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public String getTeamRole()
    {
        return teamRole;
    }
    
    public void setTeamRole(String teamRole)
    {
        this.teamRole = teamRole;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public void setToken(String token)
    {
        this.token = token;
    }
    
}
