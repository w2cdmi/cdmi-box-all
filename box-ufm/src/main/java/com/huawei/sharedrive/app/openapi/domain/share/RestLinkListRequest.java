package com.huawei.sharedrive.app.openapi.domain.share;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.box.domain.Order;

public class RestLinkListRequest
{
    private static final int DEFAULT_LIMIT = 100;
    
    private static final long DEFAULT_OFFSET = 0L;
    
    private static final int MAX_LIMIT = 1000;
    
    // 最多可支持生成的缩略图个数
    private static final int MAX_THUMBNAIL_SIZE = 5;
    
    private Long ownedBy;
    
    private String keyword;
    
    // 查询条数
    private Integer limit;
    
    // 偏移量
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
    
    public void checkParameter() throws InvalidParamException
    {
        if (ownedBy != null)
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownedBy);
        }
        
        if (limit != null && (limit < 1 || limit > MAX_LIMIT))
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
                temp.checkLinkNodeParameter();
            }
        }
        
        if (thumbnail != null)
        {
            if (thumbnail.size() > MAX_THUMBNAIL_SIZE)
            {
                throw new InvalidParamException();
            }
            for (Thumbnail temp : thumbnail)
            {
                temp.checkParameter();
            }
        }
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
    
    public void setThumbnail(List<Thumbnail> thumbnail)
    {
        this.thumbnail = thumbnail;
    }
    
    public Long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
}
