package com.huawei.sharedrive.app.openapi.domain.node.favor;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.BusinessConstants;

import pw.cdmi.box.domain.Order;

public class ListFavoriteRequest
{
    
    private static final int MAX_LIMIT = 1000;
    
    // 查询条数
    private Integer limit;
    
    // 偏移量
    private Long offset;
    
    private String keyword;
    
    private List<Order> order;
    
    public ListFavoriteRequest()
    {
    }
    
    public ListFavoriteRequest(Integer limit, Long offset)
    {
        this.limit = limit;
        this.offset = offset;
    }
    
    public ListFavoriteRequest(Integer limit, Long offset, String keyword)
    {
        this.limit = limit;
        this.offset = offset;
        this.keyword = keyword;
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
    
    public void checkParameter() throws InvalidParamException
    {
        if (limit != null && (limit < 1 || limit > MAX_LIMIT))
        {
            throw new InvalidParamException();
        }
        if (offset != null && offset < 0)
        {
            throw new InvalidParamException();
        }
        
        if (order != null)
        {
            for (Order temp : order)
            {
                temp.checkFavoriteParameter();
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
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
}
