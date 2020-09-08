package com.huawei.sharedrive.uam.teamspace.domain;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.uam.util.BusinessConstants;

import pw.cdmi.box.domain.Order;

public abstract class BaseListRequest
{
    private static final int DEFAULT_LIMIT = 100;
    
    private static final long DEFAULT_OFFSET = 0L;
    
    protected Integer limit;
    
    protected Long offset;
    
    protected List<Order> order;
    
    public BaseListRequest()
    {
        limit = DEFAULT_LIMIT;
        offset = DEFAULT_OFFSET;
    }
    
    public BaseListRequest(Integer limit, Long offset)
    {
        this.limit = limit != null ? limit : DEFAULT_LIMIT;
        this.offset = offset != null ? offset : DEFAULT_OFFSET;
    }
    
    public void addOrder(Order orderV2)
    {
        if (orderV2 == null)
        {
            return;
        }
        if (order == null)
        {
            order = new ArrayList<Order>(BusinessConstants.INITIAL_CAPACITIES);
        }
        order.add(orderV2);
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
