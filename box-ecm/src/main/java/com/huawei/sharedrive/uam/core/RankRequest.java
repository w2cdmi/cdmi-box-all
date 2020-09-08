package com.huawei.sharedrive.uam.core;

import java.io.Serializable;
import java.util.List;

import pw.cdmi.box.domain.Order;

public class RankRequest implements Serializable
{
    
    private static final long serialVersionUID = -6290855788947715275L;
    
    private String keyword;
    
    private int limit;
    
    private long offset;
    
    private List<Order> order;
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public int getLimit()
    {
        return limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public List<Order> getOrder()
    {
        return order;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public void setOrder(List<Order> order)
    {
        this.order = order;
    }
    
}
