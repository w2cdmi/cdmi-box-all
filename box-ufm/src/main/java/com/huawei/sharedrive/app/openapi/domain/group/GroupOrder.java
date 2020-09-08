package com.huawei.sharedrive.app.openapi.domain.group;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public class GroupOrder
{
    private String field;
    
    private String direction;
    
    public GroupOrder()
    {
        
    }
    
    public GroupOrder(String field, String direction)
    {
        this.field = field;
        this.direction = direction;
    }
    
    public String getField()
    {
        return field;
    }
    
    public void setField(String field)
    {
        this.field = field;
    }
    
    public String getDirection()
    {
        return direction;
    }
    
    public void setDirection(String direction)
    {
        this.direction = direction;
    }
    
    public void checkParameter()
    {
        if (!isTypeValid())
        {
            throw new InvalidParamException("Order field invalid: " + field);
        }
        if (!isDirectionValid())
        {
            throw new InvalidParamException("Order direction invalid: " + direction);
        }
    }
    
    private boolean isDirectionValid()
    {
        if (StringUtils.isBlank(direction))
        {
            return false;
        }
        GroupOrderDirection[] groupDirections = GroupOrderDirection.values();
        for (GroupOrderDirection groupDirection : groupDirections)
        {
            if (groupDirection.getDirection().equalsIgnoreCase(direction))
            {
                return true;
            }
        }
        return false;
    }
    
    private boolean isTypeValid()
    {
        if (StringUtils.isBlank(field))
        {
            return false;
        }
        GroupOrderType[] groupOrderTypes = GroupOrderType.values();
        for (GroupOrderType groupOrderType : groupOrderTypes)
        {
            if (groupOrderType.getType().equalsIgnoreCase(field))
            {
                return true;
            }
        }
        return false;
    }
    
}
