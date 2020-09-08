package pw.cdmi.box.disk.teamspace.domain;

public class NodeACLPage extends BasicACL
{
    private String desc;
    
    private Long nodeId;

    private Long ownerId;

    public String getDesc()
    {
        return desc;
    }

    public Long getNodeId()
    {
        return nodeId;
    }

    public Long getOwnerId()
    {
        return ownerId;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
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
