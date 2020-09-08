package pw.cdmi.box.disk.teamspace.domain;

public class RestNodePermissionInfo
{
    private Long ownerId;
    
    private Long nodeId;
    
    private RestACL permissions;
    
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
