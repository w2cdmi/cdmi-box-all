package pw.cdmi.box.disk.client.domain.share;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Pageable;
import pw.cdmi.box.http.request.RestRegionInfo;

public class RestSharePageRequest implements Pageable
{
    
    private static final long serialVersionUID = 8000398948772996476L;
    
    public static final int PAGE_LIMIT = 1000;
    
    private boolean desc;
    
    private int limit;
    
    private long offset;
    
    private Order order;
    
    private String orderField;
    
    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof RestSharePageRequest)
        {
            if (this == obj)
            {
                return true;
            }
            RestSharePageRequest that = (RestSharePageRequest) obj;
            
            boolean descEqual = this.desc == that.desc;
            boolean limitEqual = this.limit == that.limit;
            boolean offsetEqual = this.offset == that.offset;
            boolean sortEqual = this.order == null ? that.order == null : this.order.equals(that.order);
            boolean fieldEqual = this.orderField == null ? that.orderField == null
                : this.orderField.equals(that.orderField);
            
            return descEqual && limitEqual && offsetEqual && sortEqual && fieldEqual;
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return orderField == null ? 0 : orderField.hashCode();
    }
    
    @Override
    public Limit getLimit()
    {
        Limit tempLimit = new Limit();
        if (limit > 0 && limit <= PAGE_LIMIT)
        {
            tempLimit.setLength(this.limit);
        }
        else
        {
            tempLimit.setLength(PAGE_LIMIT);
        }
        tempLimit.setOffset(this.offset);
        return tempLimit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    @Override
    public Order getOrder()
    {
        return this.order;
    }
    
    public String getOrderField()
    {
        return orderField;
    }
    
    @Override
    public int getPageNumber()
    {
        throw new IllegalArgumentException("unsed para pageNumber");
    }
    
    @Override
    public int getPageSize()
    {
        return this.limit;
    }
    
    public boolean isDesc()
    {
        return this.desc;
    }
    
    public void setDesc(boolean desc)
    {
        this.desc = desc;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(int offset)
    {
        this.offset = offset;
    }
    
    public void setOrder(Order order)
    {
        this.order = order;
    }
    
    public void setOrderField(String orderField)
    {
        this.orderField = orderField;
    }

    /**
     * TODO 简单描述该方法的实现功能（可选）.
     * @see pw.cdmi.box.domain.Pageable#setRestRegionInfo(java.util.List)
     */
    @Override
    public void setRestRegionInfo(List<RestRegionInfo> restRegionInfo) {
        // TODO Auto-generated method stub
        
    }
    
}
