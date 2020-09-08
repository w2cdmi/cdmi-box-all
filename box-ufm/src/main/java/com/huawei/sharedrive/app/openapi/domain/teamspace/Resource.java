package com.huawei.sharedrive.app.openapi.domain.teamspace;

public class Resource
{
    private Long ownerId;
    
    private Long nodeId;
    
    public Resource(Long ownerId, Long nodeId)
    {
        this.ownerId = ownerId;
        this.nodeId = nodeId;
    }
    
    public Resource()
    {
        
    }
    
    public Long getNodeId()
    {
        return nodeId;
    }
    
    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }
    
    public Long getOwnerId()
    {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId)
    {
        this.ownerId = ownerId;
    }
}
