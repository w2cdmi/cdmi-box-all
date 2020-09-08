package com.huawei.sharedrive.app.openapi.domain.share;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.share.domain.INodeShare;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * 高级列举共享资源 对象
 * 
 * @author l90005448
 * 
 */
public class RestShareRequestV2
{
    
    public static final int PAGE_LIMIT = 1000;
    
    private boolean desc;
    
    private int limit;
    
    private long offset;
    
    private String keyword;
    
    private Order order;
    
    private String orderField;
    
    private Thumbnail bigThumbSize;
    
    private Thumbnail smallThumbSize;
    
    /**
     * 总数
     */
    private int totalCount;
    
    /**
     * 当前共享关系列表
     */
    private List<INodeShare> contents;
    
    public RestShareRequestV2(List<INodeShare> contents, int totalCount)
    {
        this.totalCount = totalCount;
        this.contents = contents;
    }
    
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
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public Order getOrder()
    {
        return this.order;
    }
    
    public String getOrderField()
    {
        return orderField;
    }
    
    public int getPageNumber()
    {
        throw new IllegalArgumentException("unsed para pageNumber");
    }
    
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
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public Thumbnail getBigThumbnailSize()
    {
        return bigThumbSize;
    }
    
    public void setBigThumbnailSize(Thumbnail bigThumbSize)
    {
        this.bigThumbSize = bigThumbSize;
    }
    
    public Thumbnail getSmallThumbnailSize()
    {
        return smallThumbSize;
    }
    
    public void setSmallThumbnailSize(Thumbnail smallThumbSize)
    {
        this.smallThumbSize = smallThumbSize;
    }
    
    public List<INodeShare> getContent()
    {
        return contents;
    }
    
    public void setContent(List<INodeShare> contents)
    {
        this.contents = contents;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
