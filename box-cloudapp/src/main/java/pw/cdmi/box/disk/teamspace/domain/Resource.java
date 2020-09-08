package pw.cdmi.box.disk.teamspace.domain;

public class Resource
{
    private Long ownerId;
    
    private Long nodeId;
    
    public Resource()
    {
        
    }
    
    public Resource(Long ownerId, Long nodeId)
    {
        this.ownerId = ownerId;
        this.nodeId = nodeId;
    }
    
    public Long getNodeId()
    {
        return nodeId;
    }
    
    public Long getOwnerId()
    {
        return ownerId;
    }
    
    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }
    
    public void setOwnerId(Long ownerId)
    {
        this.ownerId = ownerId;
    }
}
