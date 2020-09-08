package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;
import java.util.List;

import pw.cdmi.box.disk.client.domain.node.Thumbnail;
import pw.cdmi.box.domain.Order;

public class RequestListShareResourceRequest implements Serializable
{
    private static final long serialVersionUID = 8000398948772996476L;
    
    private int limit;
    
    private long offset;
    
    private String keyword;
    
    private List<Order> order;
    
    private List<Thumbnail> thumbnail;
    
    public int getLimit()
    {
        return limit;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public List<Thumbnail> getThumbnail()
    {
        return thumbnail;
    }
    
    public void setThumbnail(List<Thumbnail> thumbnail)
    {
        this.thumbnail = thumbnail;
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
