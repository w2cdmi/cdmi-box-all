/**
 * 
 */
package pw.cdmi.box.uam.feedback.util;

import java.io.Serializable;
import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Pageable;
import pw.cdmi.box.http.request.RestRegionInfo;

/**
 * @author s00108907
 * 
 */
public class PageRequest implements Pageable, Serializable
{
    
    private static final long serialVersionUID = 8280485938848398236L;
    
    private int page;
    
    private int size;
    
    private Order order;
    
    public PageRequest()
    {
        this(0, 10, null);
    }
    
    /**
     * Creates a new {@link PageRequest}. Pages are zero indexed, thus providing 0 for
     * {@code page} will return the first page.
     * 
     * @param size
     * @param page
     */
    public PageRequest(int page, int size)
    {
        this(page, size, null);
    }
    
    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     * 
     * @param page
     * @param size
     * @param order
     */
    public PageRequest(int page, int size, Order order)
    {
        if (0 > page)
        {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }
        
        if (0 >= size)
        {
            throw new IllegalArgumentException("Page size must not be less than or equal to zero!");
        }
        
        this.page = page;
        this.size = size;
        this.order = order;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Pageable#getPageSize()
     */
    public int getPageSize()
    {
        return size;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Pageable#getPageNumber()
     */
    public int getPageNumber()
    {
        return page;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Pageable#getLimit()
     */
    public Limit getLimit()
    {
        Limit limit = new Limit();
        long offset = 0;
        if (page > 0)
        {
            offset = (page - 1) * size;
        }
        limit.setOffset(offset);
        limit.setLength(size);
        return limit;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Pageable#getSort()
     */
    public Order getOrder()
    {
        return order;
    }
    
    public void setPage(int page)
    {
        this.page = page;
    }
    
    public void setSize(int size)
    {
        this.size = size;
    }
    
    public void setOrder(Order order)
    {
        this.order = order;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof PageRequest)
        {
            if (this == obj)
            {
                return true;
            }
            
            PageRequest that = (PageRequest) obj;
            
            boolean pageEqual = this.page == that.page;
            boolean sizeEqual = this.size == that.size;
            
            boolean sortEqual = this.order == null ? that.order == null : this.order.equals(that.order);
            
            return pageEqual && sizeEqual && sortEqual;
        }
        return false;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        int result = 17;
        
        result = 31 * result + page;
        result = 31 * result + size;
        result = 31 * result + (null == order ? 0 : order.hashCode());
        
        return result;
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
