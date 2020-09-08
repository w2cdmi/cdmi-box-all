package com.huawei.sharedrive.app.openapi.domain.group;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class GroupOrderRequest
{
    private Integer limit;
    
    private Long offset;
    
    private List<GroupOrder> order;
    
    private String type;
    
    private String keyword;
    
    public GroupOrderRequest()
    {
        limit = GroupConstants.GROUP_LIMIT_DEFAULT;
        offset = GroupConstants.GROUP_OFFSET_DEFAULT;
    }
    
    public GroupOrderRequest(Integer limit, Long offset)
    {
        this.limit = limit;
        this.offset = offset;
        order = getDefaultOrderList();
    }
    
    public void checkParameter()
    {
        if (limit == null)
        {
            limit = GroupConstants.GROUP_LIMIT_DEFAULT;
        }
        FilesCommonUtils.checkNonNegativeIntegers(limit);
        if (limit > 1000 || limit <= 0)
        {
            throw new InvalidParamException("limit error:" + limit);
        }
        if (offset == null)
        {
            offset = GroupConstants.GROUP_OFFSET_DEFAULT;
        }
        FilesCommonUtils.checkNonNegativeIntegers(offset);
        if (type == null)
        {
            type = GroupConstants.TYPE_PRIVATE;
        }
        if (!GroupConstants.belongAllType(type))
        {
            throw new InvalidParamException("type error:" + type);
        }
        if(null != this.getKeyword() && this.getKeyword().length() > 255)
        {
            throw new InvalidParamException("keyword lenth is " + this.getKeyword().length());
        }
    }
    
    public void checkOrder()
    {
        if (order == null)
        {
            order = getDefaultOrderList();
        }
        for (GroupOrder groupOrder : order)
        {
            groupOrder.checkParameter();
        }
    }
    
    private List<GroupOrder> getDefaultOrderList()
    {
        List<GroupOrder> orderList = new ArrayList<GroupOrder>(2);
        // 默认按照名称升序排列
        orderList.add(new GroupOrder("name", "asc"));
        return orderList;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    public List<GroupOrder> getOrder()
    {
        return order;
    }
    
    public void setOrder(List<GroupOrder> order)
    {
        this.order = order;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
}
