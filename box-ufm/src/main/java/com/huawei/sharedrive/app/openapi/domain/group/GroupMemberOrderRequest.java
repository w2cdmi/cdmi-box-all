package com.huawei.sharedrive.app.openapi.domain.group;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class GroupMemberOrderRequest
{
    private Integer limit;
    
    private Long offset;
    
    private String keyword;
    
    private String groupRole;
    
    private List<GroupOrder> order;
    
    public GroupMemberOrderRequest()
    {
        limit = GroupConstants.GROUP_LIMIT_DEFAULT;
        offset = GroupConstants.GROUP_OFFSET_DEFAULT;
    }
    
    public GroupMemberOrderRequest(Integer limit, Long offset)
    {
        if (limit == null)
        {
            this.limit = GroupConstants.GROUP_LIMIT_DEFAULT;
        }
        else
        {
            this.limit = limit;
        }
        if (offset == null)
        {
            this.offset = GroupConstants.GROUP_OFFSET_DEFAULT;
        }
        else
        {
            this.offset = offset;
        }
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
        if (order == null)
        {
            order = getDefaultOrderList();
        }
        if (groupRole == null)
        {
            groupRole = "all";
        }
        if (!GroupConstants.belongAllRole(groupRole))
        {
            throw new InvalidParamException("gropRole error: " + groupRole);
        }
        for (GroupOrder groupOrder : order)
        {
            groupOrder.checkParameter();
        }
    }
    
    public List<GroupOrder> getDefaultOrderList()
    {
        List<GroupOrder> orderList = new ArrayList<GroupOrder>(2);
        // 默认按照时间降序排列
        orderList.add(new GroupOrder("groupRole", "asc"));
        orderList.add(new GroupOrder("username", "asc"));
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
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public String getGroupRole()
    {
        return groupRole;
    }
    
    public void setGroupRole(String groupRole)
    {
        this.groupRole = groupRole;
    }
    
}
