package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public class RestTeamMemberModifyRequest
{
    private String teamRole;
    
    private String role;
    
    private Long newOwnerId;
    
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

    public Long getNewOwnerId()
    {
        return newOwnerId;
    }

    public void setNewOwnerId(Long newOwnerId)
    {
        this.newOwnerId = newOwnerId;
    }
}
