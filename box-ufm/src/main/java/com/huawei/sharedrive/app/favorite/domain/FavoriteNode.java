package com.huawei.sharedrive.app.favorite.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 节点对象,包括文件,文件夹及文件版本
 * 
 */
public class FavoriteNode implements Serializable
{
    
    private static final long serialVersionUID = 7124022200504836063L;
    
    public static final Long TREE_ROOT_ID = (long) 0;
    
    public static final String MYSPACE = "myspace";
    
    public static final String TEAMSPACE = "teamspace";
    
    public static final String SHARE = "share";
    
    public static final String LINK = "link";
    
    public static final String CONTAINOR = "containor";
    
    private Long id;
    
    private Long ownedBy;
    
    private int type;
    
    private Long parent;
    
    private String name;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private Node node;
    
    private String params;
    
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
    
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
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
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
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
    
    public Node getNode()
    {
        return node;
    }
    
    public void setNode(Node node)
    {
        this.node = node;
    }
    
    public String getParams()
    {
        return params;
    }
    
    public void setParams(String params)
    {
        this.params = params;
    }
    
    public static final int typeStringToInt(String type)
    {
        int temp = 0;
        if (CONTAINOR.equals(type))
        {
            temp = 0;
        }
        else if (MYSPACE.equals(type))
        {
            temp = 1;
        }
        else if (SHARE.equals(type))
        {
            temp = 2;
        }
        else if (TEAMSPACE.equals(type))
        {
            temp = 3;
        }
        else if (LINK.equals(type))
        {
            temp = 4;
        }
        else if (CONTAINOR.equals(type))
        {
            temp = 0;
        }
        else
        {
            temp = -1;
        }
        return temp;
    }
    
    public static final String typeIntToString(int type)
    {
        if (type == 0)
        {
            return CONTAINOR;
        }
        if (type == 1)
        {
            return MYSPACE;
        }
        if (type == 2)
        {
            return SHARE;
        }
        if (type == 3)
        {
            return TEAMSPACE;
        }
        if (type == 4)
        {
            return LINK;
        }
        return "";
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
