package pw.cdmi.box.disk.client.domain.share;

import java.util.ArrayList;
import java.util.List;

import pw.cdmi.box.disk.client.domain.node.Thumbnail;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.domain.Order;

public class RestLinkListRequest
{
    private static final int DEFAULT_LIMIT = 100;
    
    private static final long DEFAULT_OFFSET = 0L;
    
    private Long ownedBy;
    private Integer limit;
    
    private Long offset;
    
    private List<Order> order;
    
    private List<Thumbnail> thumbnail;
    
    public RestLinkListRequest()
    {
        limit = DEFAULT_LIMIT;
        offset = DEFAULT_OFFSET;
    }
    
    public RestLinkListRequest(Integer limit, Long offset)
    {
        this.limit = limit != null ? limit : DEFAULT_LIMIT;
        this.offset = offset != null ? offset : DEFAULT_OFFSET;
    }
    
    public void addThumbnail(Thumbnail thumb)
    {
        if (thumb == null)
        {
            return;
        }
        if (thumbnail == null)
        {
            thumbnail = new ArrayList<Thumbnail>(BusinessConstants.INITIAL_CAPACITIES);
        }
        thumbnail.add(thumb);
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
    
    public List<Order> getOrder()
    {
        return order;
    }
    
    public List<Thumbnail> getThumbnail()
    {
        return thumbnail;
    }

    public Long getOwnedBy()
    {
        return ownedBy;
    }

    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
}
