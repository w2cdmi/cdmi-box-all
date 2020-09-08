package com.huawei.sharedrive.app.openapi.domain.node;

import java.io.Serializable;

public class Node implements Serializable
{
    private static final long serialVersionUID = -8451590130421601479L;

    private Long id;
    
    private Long ownedBy;
    
    private String type;
    
    public Node()
    {
    }
    public Node(com.huawei.sharedrive.app.favorite.domain.Node node)
    {
        this.ownedBy=node.getOwnedBy();
        this.id=node.getId();
        this.type=com.huawei.sharedrive.app.favorite.domain.Node.typeIntToString(node.getType());
    }
    
    public Node(Long ownedBy, Long id)
    {
        this.ownedBy = ownedBy;
        this.id = id;
    }
    
    public Long getId()
    {
        return id;
    }
    public Long getOwnedBy()
    {
        return ownedBy;
    }
    public String getType()
    {
        return type;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }

    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
