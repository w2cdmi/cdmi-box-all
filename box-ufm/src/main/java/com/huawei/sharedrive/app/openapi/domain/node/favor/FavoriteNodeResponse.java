package com.huawei.sharedrive.app.openapi.domain.node.favor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.favorite.domain.FavoriteNode;
import com.huawei.sharedrive.app.openapi.domain.node.Node;

import pw.cdmi.core.utils.JsonUtils;

public class FavoriteNodeResponse implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    
    private Long ownedBy;
    
    private String type;
    
    private Long parent;
    
    private String name;
    
    private Long createdAt;
    
    private Long modifiedAt;
    
    private Node node;
    
    private List<Param> params;
    
    private Boolean previewable;
    
    public FavoriteNodeResponse()
    {
    }
    
    @SuppressWarnings("unchecked")
    public FavoriteNodeResponse(FavoriteNode favoriteNode)
    {
        this.id = favoriteNode.getId();
        
        this.ownedBy = favoriteNode.getOwnedBy();
        
        this.type = FavoriteNode.typeIntToString(favoriteNode.getType());
        
        this.parent = favoriteNode.getParent();
        
        this.name = favoriteNode.getName();
        
        if (null != favoriteNode.getCreatedAt())
        {
            this.createdAt = favoriteNode.getCreatedAt().getTime();
        }
        
        if (null != favoriteNode.getModifiedAt())
        {
            this.modifiedAt = favoriteNode.getModifiedAt().getTime();
        }
        if (null != favoriteNode.getNode())
        {
            this.node = new Node(favoriteNode.getNode());
            Byte nodeType = favoriteNode.getNode().getType();
            if (nodeType != null && com.huawei.sharedrive.app.favorite.domain.Node.FILE_NUM == nodeType)
            {
                this.setPreviewable(favoriteNode.isPreviewable());
            }
        }
        else
        {
            this.node = new Node();
        }
        if (StringUtils.isNotBlank(favoriteNode.getParams()))
        {
            this.params = (List<Param>) JsonUtils.stringToList(favoriteNode.getParams(),
                List.class,
                Param.class);
        }
        else
        {
            this.params = new ArrayList<Param>(0);
        }
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public Long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
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
    
    public Long getCreatedAt()
    {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public Long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public void setModifiedAt(Long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    public Node getNode()
    {
        return node;
    }
    
    public void setNode(Node node)
    {
        this.node = node;
    }
    
    public List<Param> getParams()
    {
        return params;
    }
    
    public void setParams(List<Param> params)
    {
        this.params = params;
    }
    
    public Boolean getPreviewable()
    {
        return previewable;
    }
    
    public void setPreviewable(Boolean previewable)
    {
        this.previewable = previewable;
    }
    
}
