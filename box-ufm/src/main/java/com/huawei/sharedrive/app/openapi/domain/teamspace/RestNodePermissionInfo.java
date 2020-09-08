package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.huawei.sharedrive.app.acl.domain.ACL;

public class RestNodePermissionInfo
{
    private Long ownerId;
    
    private Long nodeId;
    
    private RestACL permissions;
    
    public RestNodePermissionInfo(Long ownerId, Long nodeId, ACL acl)
    {
        this.ownerId = ownerId;
        this.nodeId = nodeId;
        this.permissions = new RestACL(acl);
    }
    
    public Long getOwnerId()
    {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId)
    {
        this.ownerId = ownerId;
    }
    
    public Long getNodeId()
    {
        return nodeId;
    }
    
    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
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
