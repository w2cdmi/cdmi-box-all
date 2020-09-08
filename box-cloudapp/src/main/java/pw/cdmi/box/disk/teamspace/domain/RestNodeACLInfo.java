package pw.cdmi.box.disk.teamspace.domain;

public class RestNodeACLInfo
{
    private long id;
    
    private Resource resource;
    
    private String role;
    
    private RestTeamMember user;
    
    public long getId()
    {
        return id;
    }
    
    public Resource getResource()
    {
        return resource;
    }
    
    public String getRole()
    {
        return role;
    }
    
    public RestTeamMember getUser()
    {
        return user;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setResource(Resource resource)
    {
        this.resource = resource;
    }
    
    public void setRole(String role)
    {
        this.role = role;
    }
    
    public void setUser(RestTeamMember user)
    {
        this.user = user;
    }
    
}
