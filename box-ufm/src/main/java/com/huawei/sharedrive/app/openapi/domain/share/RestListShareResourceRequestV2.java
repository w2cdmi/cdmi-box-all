package com.huawei.sharedrive.app.openapi.domain.share;

import java.io.Serializable;
import java.util.List;

import com.huawei.sharedrive.app.core.domain.Thumbnail;

import pw.cdmi.box.domain.Order;

/**
 * 列举共享资源 对象
 * 
 * @author l90005448
 * 
 */
public class RestListShareResourceRequestV2 implements Serializable
{
    private static final long serialVersionUID = 8000398948772996476L;
    
    private Integer limit;
    
    private Long offset;
    
    private String keyword;
    
    private String shareType;	//"wxmp": 小程序请求，"": 以前请求
    
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

	public String getShareType() {
		return shareType;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}


    
}
