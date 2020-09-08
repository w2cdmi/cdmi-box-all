package com.huawei.sharedrive.app.openapi.domain.node.favor;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.favorite.domain.FavoriteNode;
import com.huawei.sharedrive.app.favorite.domain.Node;
import com.huawei.sharedrive.app.openapi.domain.node.NodeCreateRequest;

public class FavoriteNodeCreateRequest
{
    
    private String type;
    
    private Long parent;
    
    private String name;
    
    private NodeCreateRequest node;
    
    private String linkCode;
    
    public void checkParamter() throws BaseRunException
    {
        // 校验parent
        checkParent();
        // 校验name
        checkName();
        // 校验type
        checkType();
        // 校验linkCode
        checkLinkCode();
        // 校验node
        checkNode();
    }
    
    public String getLinkCode()
    {
        return linkCode;
    }
    
    public String getName()
    {
        return name;
    }
    
    public NodeCreateRequest getNode()
    {
        return node;
    }
    
    public Long getParent()
    {
        return parent;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setNode(NodeCreateRequest node)
    {
        this.node = node;
    }
    
    public void setParent(Long parent)
    {
        this.parent = parent;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    private void checkLinkCode()
    {
        if (FavoriteNode.LINK.equals(type))
        {
            if (StringUtils.isEmpty(linkCode))
            {
                throw new InvalidParamException("type is " + type + " and linkCode can not be null ");
            }
        }
    }
    
    private void checkName()
    {
        if (name == null)
        {
            throw new InvalidParamException("[name is " + name + "]; name can not be null");
        }
        name = name.trim();
        if (name.length() == 0 || name.length() > 255)
        {
            throw new InvalidParamException("[name is " + name + "]; name can not be null");
        }
    }
    
    private void checkNode()
    {
        if (!FavoriteNode.CONTAINOR.equals(type) && !FavoriteNode.LINK.equals(type))
        {
            if (node == null)
            {
                throw new InvalidParamException("type is " + type + "; node  can not be null");
            }
            
            if (node.getId() == null || node.getOwnedBy() == null)
            {
                throw new InvalidParamException("type is " + type
                    + "; node.getId() and node.getOwndedBy()  can not be null");
            }
        }
    }
    
    private void checkParent()
    {
        if (parent == null)
        {
            throw new InvalidParamException("parentId can not be null");
        }
    }
    
    private void checkType()
    {
        if (type == null)
        {
            throw new InvalidParamException("type can not be null");
        }
        
        if (!(type.equals(FavoriteNode.CONTAINOR) || type.equals(FavoriteNode.LINK)
            || type.equals(FavoriteNode.MYSPACE) || type.equals(FavoriteNode.SHARE) || type.equals(FavoriteNode.TEAMSPACE)))
        {
            throw new InvalidParamException("type is " + type + "; type value is not legal");
        }
        
        if (type.equals(FavoriteNode.CONTAINOR))
        {
            node = null;
            linkCode = null;
        }
    }
    
    
    public FavoriteNode builderFavoriteNode()
    {
        FavoriteNode favorNode = new FavoriteNode();
        favorNode.setName(this.getName());
        favorNode.setType(typeStringToInt(this.getType()));
        favorNode.setParent(this.getParent());
        NodeCreateRequest nodeCreate = this.getNode();
        if (null != nodeCreate)
        {
            favorNode.setNode(new Node(nodeCreate.getOwnedBy(), nodeCreate.getId()));
        }
        return favorNode;
    }
    public static final Long TREE_ROOT_ID = (long) 0;
    
    public static final String MYSPACE = "myspace";
    
    public static final String TEAMSPACE = "teamspace";
    
    public static final String SHARE = "share";
    
    public static final String LINK = "link";
    
    public static final String CONTAINOR = "containor";
    
    
    public static final int typeStringToInt(String type)
    {
        int temp = 0;
        if (null == type)
        {
            return -1;
        }
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
    
}
