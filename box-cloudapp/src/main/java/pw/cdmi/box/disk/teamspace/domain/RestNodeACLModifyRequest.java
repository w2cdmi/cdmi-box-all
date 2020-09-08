package pw.cdmi.box.disk.teamspace.domain;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.exception.InvalidParamException;


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
