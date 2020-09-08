package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.huawei.sharedrive.app.acl.domain.INodeACL;

public class RestNodeACLInfo
{
    private long id;
    
    private Resource resource;
    
    private String role;
    
    private RestTeamMember user;
    
    public RestNodeACLInfo()
    {
        
    }
    
    public RestNodeACLInfo(INodeACL inodeRole)
    {
        this.id = inodeRole.getId();
        setResource(new Resource(inodeRole.getOwnedBy(), inodeRole.getiNodeId()));
        this.role = inodeRole.getResourceRole();
        this.user = new RestTeamMember();
        this.user.setId(inodeRole.getAccessUserId());
        this.user.setType(inodeRole.getUserType());
        
        if (inodeRole.getUser() != null)
        {
            this.user.setLoginName(inodeRole.getUser().getLoginName());
            this.user.setName(inodeRole.getUser().getName());
//            this.user.setDescription(inodeRole.getUser().getDepartment());
        }
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public String getRole()
    {
        return role;
    }
    
    public void setRole(String role)
    {
        this.role = role;
    }
    
    public RestTeamMember getUser()
    {
        return user;
    }
    
    public void setUser(RestTeamMember user)
    {
        this.user = user;
    }

    public Resource getResource()
    {
        return resource;
    }

    public void setResource(Resource resource)
    {
        this.resource = resource;
    }
    
}
