package com.huawei.sharedrive.app.openapi.domain.share;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.OrderV1;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Pageable;
import pw.cdmi.box.http.request.RestRegionInfo;

/**
 * Rest分页请求
 * 
 * @author l90003768
 * 
 */
public class RestSharePageRequest implements Pageable
{
    
    public static final int PAGE_LIMIT = 1000;
    
    private boolean desc;
    
    private int limit;
    
    private long offset;
    
    private Order order;
    
    private String orderField;
    
    @Override
    public Limit getLimit()
    {
        Limit tempLimit = new Limit();
        if (limit > 0 && limit <= PAGE_LIMIT)
        {
            tempLimit.setLength(this.limit);
        }
        else
        {
            tempLimit.setLength(PAGE_LIMIT);
        }
        tempLimit.setOffset(this.offset);
        return tempLimit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    @Override
    public Order getOrder()
    {
        return this.order;
    }
    
    public String getOrderField()
    {
        return orderField;
    }
    
    @Override
    public int getPageNumber()
    {
        throw new IllegalArgumentException("unsed para pageNumber");
    }
    
    @Override
    public int getPageSize()
    {
        return this.limit;
    }
    
    public boolean isDesc()
    {
        return this.desc;
    }
    
    public void setDesc(boolean desc)
    {
        this.desc = desc;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(int offset)
    {
        this.offset = offset;
    }
    
    public void setOrder(Order order)
    {
        this.order = order;
    }
    
    public void setOrderField(String orderField)
    {
        this.orderField = orderField;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (desc ? 1231 : 1237);
        result = prime * result + limit;
        result = prime * result + (int) (offset ^ (offset >>> 32));
        result = prime * result + ((order == null) ? 0 : order.hashCode());
        result = prime * result + ((orderField == null) ? 0 : orderField.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof RestSharePageRequest)
        {
            RestSharePageRequest other = (RestSharePageRequest) obj;
            if (desc != other.desc || limit != other.limit || offset != other.offset)
            {
                return false;
            }
            if (order == null)
            {
                if (other.order != null)
                {
                    return false;
                }
            }
            else if (!order.equals(other.order))
            {
                return false;
            }
            if (orderField == null)
            {
                if (other.orderField != null)
                {
                    return false;
                }
            }
            else if (!orderField.equals(other.orderField))
            {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * TODO 简单描述该方法的实现功能（可选）.
     * @see pw.cdmi.box.domain.Pageable#setRestRegionInfo(java.util.List)
     */
    @Override
    public void setRestRegionInfo(List<RestRegionInfo> restRegionInfo) {
        // TODO Auto-generated method stub
        
    }
    
}
