package pw.cdmi.box.disk.teamspace.domain;

import java.util.ArrayList;
import java.util.List;

import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.exception.InvalidParamException;

public class ListTeamSpaceRequest
{
    private static final int DEFAULT_LIMIT = 100;
    
    private static final long DEFAULT_OFFSET = 0L;
    
    private static final int MAX_LIMIT = 1000;
    
    private Long userId;

    private int type;

    private Integer limit;
    
    private Long offset;
    
    private List<Order> order;
    
    public ListTeamSpaceRequest()
    {
        limit = DEFAULT_LIMIT;
        offset = DEFAULT_OFFSET;
    }
    
    public ListTeamSpaceRequest(Integer limit, Long offset)
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
        FilesCommonUtils.checkNonNegativeIntegers(userId);
        
        if (limit != null && (limit < 0 || limit > MAX_LIMIT))
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
    
    public Long getUserId()
    {
        return userId;
    }
    
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
