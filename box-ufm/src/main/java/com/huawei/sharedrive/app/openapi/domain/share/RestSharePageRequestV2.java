package com.huawei.sharedrive.app.openapi.domain.share;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.Thumbnail;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Pageable;
import pw.cdmi.box.http.request.RestRegionInfo;

/**
 * Rest分页请求
 * 
 * @author l90003768
 * 
 */
public class RestSharePageRequestV2 implements Pageable
{
    
    public static final int PAGE_LIMIT = 1000;
    
    private boolean desc;
    
    private String keyword;
    
    private String shareType;
    
    private int limit;
    
    private Long offset;
    
    private String orderField;
    
    private List<Order> orderList;
    
    private List<Thumbnail> thumbnail;
    
    public String getKeyword()
    {
        return keyword;
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
    
    public Long getOffset()
    {
        return offset;
    }
    
    @Override
    public Order getOrder()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getOrderField()
    {
        return orderField;
    }
    
    public List<Order> getOrderList()
    {
        return orderList;
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
    
    public List<Thumbnail> getThumbnail()
    {
        return thumbnail;
    }
    
    public boolean isDesc()
    {
        return this.desc;
    }
    
    public void setDesc(boolean desc)
    {
        this.desc = desc;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    public void setOrderField(String orderField)
    {
        this.orderField = orderField;
    }
    
    public void setOrderList(List<Order> orderList)
    {
        this.orderList = orderList;
    }
    
    public void setThumbnail(List<Thumbnail> thumbnail)
    {
        this.thumbnail = thumbnail;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (desc ? 1231 : 1237);
        result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
        result = prime * result + limit;
        result = prime * result + ((offset == null) ? 0 : offset.hashCode());
        result = prime * result + ((orderField == null) ? 0 : orderField.hashCode());
        result = prime * result + ((orderList == null) ? 0 : orderList.hashCode());
        result = prime * result + ((thumbnail == null) ? 0 : thumbnail.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof RestSharePageRequestV2)
        {
            RestSharePageRequestV2 other = (RestSharePageRequestV2) obj;
            if (desc != other.desc)
            {
                return false;
            }
            if (limit != other.limit)
            {
                return false;
            }
            return compareField(other);
        }
        return false;
    }
    
    /**
     * 为降低圈复杂度, 将equals方法进行拆分
     * 
     * @param other
     * @return
     */
    private boolean compareField(RestSharePageRequestV2 other)
    {
        if (!compareFiledPart2(other))
        {
            return false;
        }
        if (orderField == null)
        {
            if (other.orderField != null)
            {
                return false;
            }
        }
        else if (!orderField.equals(other.orderField))
        {
            return false;
        }
        if (orderList == null)
        {
            if (other.orderList != null)
            {
                return false;
            }
        }
        else if (!orderList.equals(other.orderList))
        {
            return false;
        }
        if (thumbnail == null)
        {
            if (other.thumbnail != null)
            {
                return false;
            }
        }
        else if (!thumbnail.equals(other.thumbnail))
        {
            return false;
        }
        return true;
    }
    
    private boolean compareFiledPart2(RestSharePageRequestV2 other)
    {
        if (keyword == null)
        {
            if (other.keyword != null)
            {
                return false;
            }
        }
        else if (!keyword.equals(other.keyword))
        {
            return false;
        }
        if (offset == null)
        {
            if (other.offset != null)
            {
                return false;
            }
        }
        else if (!offset.equals(other.offset))
        {
            return false;
        }
        return true;
    }

    /**
     * TODO 简单描述该方法的实现功能（可选）.
     * @see pw.cdmi.box.domain.Pageable#setRestRegionInfo(java.util.List)
     */
    @Override
    public void setRestRegionInfo(List<RestRegionInfo> restRegionInfo) {
        // TODO Auto-generated method stub
        
    }

	public String getShareType() {
		return shareType;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}

    
}
