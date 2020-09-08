package com.huawei.sharedrive.app.openapi.domain.user;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.BusinessConstants;

import pw.cdmi.box.domain.Order;

/**
 * 列举用户请求对象
 * 
 * @version  CloudStor CSE Service Platform Subproject, 2014-12-18
 * @see  
 * @since  
 */
public class ListUserRequest
{
    private static final int DEFAULT_LIMIT = 100;
    
    private static final long DEFAULT_OFFSET = 0L;
    
    private static final int MAX_LIMIT = 1000;
    
    // 查询条数
    private Integer limit;
    
    // 偏移量
    private Long offset;
    
    private List<Order> order;
    
    public ListUserRequest()
    {
        limit = DEFAULT_LIMIT;
        offset = DEFAULT_OFFSET;
    }
    
    public ListUserRequest(Integer limit, Long offset)
    {
        this.limit = limit != null ? limit : DEFAULT_LIMIT;
        this.offset = offset != null ? offset : DEFAULT_OFFSET;
    }
    
    public void addOrder(Order orderForUser)
    {
        if (orderForUser == null)
        {
            return;
        }
        if (order == null)
        {
            order = new ArrayList<Order>(BusinessConstants.INITIAL_CAPACITIES);
        }
        order.add(orderForUser);
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if(null == limit)
        {
            limit = 100;
        }
        else if (limit < 1 || limit > MAX_LIMIT)
        {
            throw new InvalidParamException("invalid limit: " + limit);
        }
        if(null == offset)
        {
            offset = 0L;
        }
        else if(offset < 0)
        {
            throw new InvalidParamException("offset invalid");
        }
        
        if (order != null)
        {
            for (Order temp : order)
            {
                temp.checkParameter();
            }
        }
        
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public List<Order> getOrder()
    {
        return order;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    public void setOrder(List<Order> order)
    {
        this.order = order;
    }
    
}
