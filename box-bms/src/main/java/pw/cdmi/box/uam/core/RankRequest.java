package pw.cdmi.box.uam.core;

import java.io.Serializable;
import java.util.List;

import pw.cdmi.box.domain.Order;

/**
 * 
 * 用于封装UAM httpclient 客户端发出的请求数据
 * 
 */
public class RankRequest implements Serializable
{
    
    private static final long serialVersionUID = -6290855788947715275L;
    
    /** 搜素关键字 */
    private String keyword;
    
    /** 分页参数：当前页数量 */
    private int limit;
    
    /** 分页参数：偏移量 */
    private long offset;
    
    /** 排序参数：升降序、排序字段 */
    private List<Order> order;
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public int getLimit()
    {
        return limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public List<Order> getOrder()
    {
        return order;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public void setOrder(List<Order> order)
    {
        this.order = order;
    }
    
}
