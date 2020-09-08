package pw.cdmi.box.disk.favorite.domain;

import java.io.Serializable;
import java.util.List;

public class FavoriteNode implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -8293661769260665829L;
    
    public static final Long TREE_ROOT_ID = (long) 0;
    
    public static final String MYSPACE = "myspace";
    
    public static final String TEAMSPACE = "teamspace";
    
    public static final String SHARE = "share";
    
    public static final String LINK = "link";
    
    public static final String CONTAINOR = "containor";
    
    private Long id;
    
    private Long ownedBy;
    
    private String type;
    
    private Long parent;
    
    private String name;
    
    private Long createdAt;
    
    private Long modifiedAt;
    
    private Node node;
    
    private List<Param> params;
    
    private boolean previewable;
    
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
    
    public boolean isPreviewable()
    {
        return previewable;
    }
    
    public void setPreviewable(boolean previewable)
    {
        this.previewable = previewable;
    }
    
}
