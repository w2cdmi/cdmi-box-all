package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;
import java.util.List;

import pw.cdmi.box.disk.client.domain.node.Thumbnail;
import pw.cdmi.box.domain.Order;

public class RestListShareResourceRequestV2 implements Serializable
{
    private static final long serialVersionUID = 8000398948772996476L;
    
    private Integer limit;
    
    private Long offset;
    
    private String keyword;
    
    private List<Order> order;
    
    private List<Thumbnail> thumbnail;
    
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
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public List<Order> getOrder()
    {
        return order;
    }
    
    public void setOrder(List<Order> order)
    {
        this.order = order;
    }
    
    public List<Thumbnail> getThumbnail()
    {
        return thumbnail;
    }
    
    public void setThumbnail(List<Thumbnail> thumbnail)
    {
        this.thumbnail = thumbnail;
    }
    
}
