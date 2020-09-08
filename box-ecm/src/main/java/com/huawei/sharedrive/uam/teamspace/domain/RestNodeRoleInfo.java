package com.huawei.sharedrive.uam.teamspace.domain;

public class RestNodeRoleInfo
{
    private String name;
    
    private String description;
    
    private int status;
    
    private RestACL permissions;
    
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
