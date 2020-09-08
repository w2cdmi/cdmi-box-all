package pw.cdmi.box.disk.httpclient.rest.request;

import java.util.ArrayList;
import java.util.List;

import pw.cdmi.box.disk.group.domain.GroupOrder;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;

public class GroupOrderRequest
{
    private Integer limit;
    
    private Long offset;
    
    private List<GroupOrder> order;
    
    private String type;
    
    private String keyword;
    
    public GroupOrderRequest()
    {
        
    }
    
    public GroupOrderRequest(String defaultField)
    {
        limit = Constants.GROUP_LIMIT_DEFAULT;
        offset = Constants.GROUP_OFFSET_DEFAULT;
        type = Constants.GROUP_TYPE_DEFAULT;
        order = getDefaultOrderList(defaultField);
    }
    
    public GroupOrderRequest(Integer limit, Long offset, String defaultField)
    {
        this.limit = limit;
        this.offset = offset;
        order = getDefaultOrderList(defaultField);
    }
    
    private List<GroupOrder> getDefaultOrderList(String defaultField)
    {
        List<GroupOrder> orderList = new ArrayList<GroupOrder>(1);
        orderList.add(new GroupOrder(defaultField, "asc"));
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
