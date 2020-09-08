package com.huawei.sharedrive.app.openapi.domain.node;

import java.io.Serializable;

public class NodeCreateRequest implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -2112456525699588883L;

    private Long id;
    
    private Long ownedBy;

    public NodeCreateRequest()
    {
    }

    public NodeCreateRequest(Long ownedBy, Long id)
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

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
}
