package pw.cdmi.box.disk.group.domain;

import java.util.List;

import pw.cdmi.box.domain.Order;

public class RestGroupOrderRequest
{
    private String keyword;
    
    private String type;
    
    private Long offset;
    
    private Integer limit;
    
    private List<Order> order;
    
    public RestGroupOrderRequest()
    {
        
    }
    
    public RestGroupOrderRequest(String keyword, Long offset, Integer limit, String type, List<Order> order)
    {
        this.keyword = keyword;
        this.offset = offset;
        this.limit = limit;
        this.type = type;
        this.order = order;
    }
    
    public void checkParameter()
    {
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public List<Order> getOrder()
    {
        return order;
    }
    
    public void setOrder(List<Order> order)
    {
        this.order = order;
    }
    
}
