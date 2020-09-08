package pw.cdmi.box.disk.favorite.domain;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.InvalidParamException;


public class FavoriteNodeCreateRequest
{
    
    private String type;
    
    private Long ownerId;
    
    private Long nodeId;
    
    private String linkCode;
    
    private String name;
    
    private Long parentId;
    
    public void checkParamter() throws BaseRunException
    {
        if (type == null || parentId == null)
        {
            throw new InvalidParamException();
        }
        if (type.equals(FavoriteNode.LINK))
        {
            if (StringUtils.isEmpty(linkCode))
            {
                throw new InvalidParamException();
            }
        }
        else
        {
            if (ownerId == null || nodeId == null)
            {
                throw new InvalidParamException();
            }
        }
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public Long getOwnerId()
    {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId)
    {
        this.ownerId = ownerId;
    }
    
    public Long getNodeId()
    {
        return nodeId;
    }
    
    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }
    
    public String getLinkCode()
    {
        return linkCode;
    }

    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }

    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Long getParentId()
    {
        return parentId;
    }
    
    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }
    
}
