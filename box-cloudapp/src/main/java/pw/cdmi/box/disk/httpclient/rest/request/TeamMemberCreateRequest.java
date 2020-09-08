package pw.cdmi.box.disk.httpclient.rest.request;

import pw.cdmi.box.disk.teamspace.domain.RestTeamMemberCreateRequest;

public class TeamMemberCreateRequest
{
    
    private RestTeamMemberCreateRequest createRequest;
    
    private String teamId;
    
    private String token;
    
    public RestTeamMemberCreateRequest getCreateRequest()
    {
        return createRequest;
    }
    
    public void setCreateRequest(RestTeamMemberCreateRequest createRequest)
    {
        this.createRequest = createRequest;
    }
    
    public String getTeamId()
    {
        return teamId;
    }
    
    public void setTeamId(String teamId)
    {
        this.teamId = teamId;
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
