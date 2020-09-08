package pw.cdmi.box.disk.favorite.domain;

public class FavoriteNodeUFM
{
    private String type;
    
    private Long parent;
    
    private String name;
    
    private Node node;
    
    private String linkCode;
    
    public FavoriteNodeUFM()
    {
    }
    
    public FavoriteNodeUFM(FavoriteNodeCreateRequest createRequest)
    {
        this.type = createRequest.getType();
        this.parent = createRequest.getParentId();
        this.name = createRequest.getName();
        this.node = new Node(createRequest.getOwnerId(), createRequest.getNodeId());
        this.linkCode = createRequest.getLinkCode();
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public Long getParent()
    {
        return parent;
    }
    
    public void setParent(Long parent)
    {
        this.parent = parent;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Node getNode()
    {
        return node;
    }
    
    public void setNode(Node node)
    {
        this.node = node;
    }
    
    public String getLinkCode()
    {
        return linkCode;
    }
    
    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }
    
}
