package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;

public class RestNodeRoleInfo
{
    private String name;
    
    private String description;
    
    private int status;
    
    private RestACL permissions;
    
    public RestNodeRoleInfo(ResourceRole resourceRole)
    {
        this.name = resourceRole.getResourceRole();
        this.description = resourceRole.getDescription();
        this.permissions = new RestACL(new ACL(resourceRole));
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public RestACL getPermissions()
    {
        return permissions;
    }
    
    public void setPermissions(RestACL permissions)
    {
        this.permissions = permissions;
    }
    
}
