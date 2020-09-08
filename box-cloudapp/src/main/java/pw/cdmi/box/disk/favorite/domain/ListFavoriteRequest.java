package pw.cdmi.box.disk.favorite.domain;

import java.util.ArrayList;
import java.util.List;

import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.domain.Order;

public class ListFavoriteRequest
{
    private static final int DEFAULT_LIMIT = 100;
    
    private static final long DEFAULT_OFFSET = 0L;
    
    private Integer limit;
    
    private Long offset;
    
    private String keyword;
    
    private List<Order> order;
    
    public ListFavoriteRequest()
    {
        limit = DEFAULT_LIMIT;
        offset = DEFAULT_OFFSET;
    }
    
    public ListFavoriteRequest(Integer limit, Long offset)
    {
        this.limit = limit != null ? limit : DEFAULT_LIMIT;
        this.offset = offset != null ? offset : DEFAULT_OFFSET;
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
