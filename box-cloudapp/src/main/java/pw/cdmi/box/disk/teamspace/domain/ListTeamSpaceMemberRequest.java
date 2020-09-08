package pw.cdmi.box.disk.teamspace.domain;

import java.util.ArrayList;
import java.util.List;

import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.exception.InvalidParamException;

public class ListTeamSpaceMemberRequest
{
    private static final int DEFAULT_LIMIT = 100;
    
    private static final long DEFAULT_OFFSET = 0L;
    
    private Integer limit;
    
    private Long offset;
    
    private List<Order> order;
    
    private String teamRole;
    
    private String keyword;
    
    public ListTeamSpaceMemberRequest()
    {
        limit = DEFAULT_LIMIT;
        offset = DEFAULT_OFFSET;
    }
    
    public ListTeamSpaceMemberRequest(Integer limit, Long offset)
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
    
    public void checkParameter() throws InvalidParamException
    {
        if (limit != null && limit < 1)
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
                temp.checkSpaceParameter();
            }
        }
        if (keyword != null && keyword.length() > 255)
        {
            throw new InvalidParamException("Invalid keyword: " + keyword);
        }
        
    }
    
    public String getKeyword()
    {
        return keyword;
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
    
    public String getTeamRole()
    {
        return teamRole;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
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
    
    public void setTeamRole(String teamRole)
    {
        this.teamRole = teamRole;
    }
}
