package pw.cdmi.box.disk.favorite.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pw.cdmi.box.disk.favorite.domain.Param.Name;

public class FavoriteTreeNode implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public static final int MAGIC_NUM_0 = 0;
    
    private Long id;
    
    private String type;
    
    private long parentId;
    
    private long ownedBy;
    
    private Long iNodeId;
    
    private String linkCode;
    
    private String nodeType;
    
    private String nodeName;
    
    private String name;
    
    private String extraAttr;
    
    private Date createAt;
    
    private Date modifiedAt;
    
    private Boolean isParent;
    
    private Map<String, String> params;
    
    private boolean previewable;
    
    public FavoriteTreeNode()
    {
    }
    
    public FavoriteTreeNode(FavoriteNode node)
    {
        this.id = node.getId();
        this.name = node.getName();
        
        this.parentId = node.getParent();
        this.type = node.getType();
        Node inode = node.getNode();
        if (node.getType().equals(FavoriteNode.CONTAINOR))
        {
            this.isParent = true;
        }
        else
        {
            if (null != inode)
            {
                this.nodeType = inode.getType();
                this.iNodeId = inode.getId();
                this.isParent = false;
                this.ownedBy = inode.getOwnedBy();
            }
        }
        List<Param> list = node.getParams();
        if (list != null && !list.isEmpty())
        {
            this.params = new HashMap<String, String>(list.size());
            for (Param param : list)
            {
                params.put(param.getName(), param.getValue());
            }
            this.linkCode = params.get(Name.LINKCODE.getName());
        }
        this.setPreviewable(node.isPreviewable());
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public long getParentId()
    {
        return parentId;
    }
    
    public void setParentId(long parentId)
    {
        this.parentId = parentId;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public Long getiNodeId()
    {
        return iNodeId;
    }
    
    public void setiNodeId(Long iNodeId)
    {
        this.iNodeId = iNodeId;
    }
    
    public String getLinkCode()
    {
        return linkCode;
    }
    
    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }
    
    public String getNodeType()
    {
        return nodeType;
    }
    
    public void setNodeType(String nodeType)
    {
        this.nodeType = nodeType;
    }
    
    public String getNodeName()
    {
        return nodeName;
    }
    
    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getExtraAttr()
    {
        return extraAttr;
    }
    
    public void setExtraAttr(String extraAttr)
    {
        this.extraAttr = extraAttr;
    }
    
    public Date getCreateAt()
    {
        if (createAt == null)
        {
            return null;
        }
        return (Date) createAt.clone();
    }
    
    public void setCreateAt(Date createAt)
    {
        if (createAt == null)
        {
            this.createAt = null;
        }
        else
        {
            this.createAt = (Date) createAt.clone();
        }
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt == null)
        {
            this.modifiedAt = null;
        }
        else
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
    }
    
    public Boolean getIsParent()
    {
        return isParent == null ? Boolean.FALSE : isParent;
    }
    
    public void setIsParent(Boolean isParent)
    {
        this.isParent = isParent;
    }
    
    public Map<String, String> getParams()
    {
        return params;
    }
    
    public void setParams(Map<String, String> params)
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
