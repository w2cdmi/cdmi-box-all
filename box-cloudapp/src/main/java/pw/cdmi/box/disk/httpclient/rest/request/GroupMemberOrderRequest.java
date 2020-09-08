package pw.cdmi.box.disk.httpclient.rest.request;

import java.util.ArrayList;
import java.util.List;

import pw.cdmi.box.disk.group.domain.GroupOrder;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;

public class GroupMemberOrderRequest
{
    private Integer limit;
    
    private Long offset;
    
    private String keyword;
    
    private String groupRole;
    
    private List<GroupOrder> order;
    
    public GroupMemberOrderRequest()
    {
        
    }
    
    public GroupMemberOrderRequest(Integer limit, Long offset)
    {
        if (limit == null)
        {
            this.limit = Constants.GROUP_LIMIT_DEFAULT;
        }
        else
        {
            this.limit = limit;
        }
        if (offset == null)
        {
            this.offset = Constants.GROUP_OFFSET_DEFAULT;
        }
        else
        {
            this.offset = offset;
        }
        order = getDefaultOrderList();
    }
    
    public List<GroupOrder> getDefaultOrderList()
    {
        List<GroupOrder> orderList = new ArrayList<GroupOrder>(2);
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
