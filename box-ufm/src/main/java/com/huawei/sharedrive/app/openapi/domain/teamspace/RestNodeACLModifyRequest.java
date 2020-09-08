package com.huawei.sharedrive.app.openapi.domain.teamspace;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public class RestNodeACLModifyRequest
{
    private String role;
    
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
        if (StringUtils.isBlank(role))
        {
            throw new InvalidParamException("role is null");
        }
        
    }
}
