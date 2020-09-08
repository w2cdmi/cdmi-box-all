package pw.cdmi.box.disk.group.domain;

import java.util.List;

import pw.cdmi.box.domain.Order;

public class RestGroupMemberOrderRequest
{
    
    private String groupRole;
    
    private String keyword;
    
    private Long offset;
    
    private Integer limit;
    
    private List<Order> order;
    
    public RestGroupMemberOrderRequest()
    {
        
    }
    
    public RestGroupMemberOrderRequest(String keyword, Long offset, Integer limit, String groupRole,
        List<Order> order)
    {
        this.keyword = keyword;
        this.offset = offset;
        this.limit = limit;
        this.order = order;
        this.groupRole = groupRole;
    }
    
    public String getGroupRole()
    {
        return groupRole;
    }
    
    public void setGroupRole(String groupRole)
    {
        this.groupRole = groupRole;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
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
    
    public List<Order> getOrder()
    {
        return order;
    }
    
    public void setOrder(List<Order> order)
    {
        this.order = order;
    }
    
}
