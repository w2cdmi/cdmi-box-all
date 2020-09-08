package com.huawei.sharedrive.app.openapi.domain.share;

import java.io.Serializable;
import java.util.List;

import com.huawei.sharedrive.app.share.domain.INodeLink;

public class RestLinkedNodeList implements Serializable
{
    private static final long serialVersionUID = -6781226550738801412L;

    /**
     * 当前共享关系列表
     */
    private List<INodeLink> contents;
    
    private Integer limit;
    
    private Long offset;
    
    /**
     * 总数
     */
    private Long totalCount;
    
    public RestLinkedNodeList()
    {
    }
    
    public RestLinkedNodeList(List<INodeLink> contents, Long totalCount)
    {
        this.totalCount = totalCount;
        this.contents = contents;
    }
    
    public List<INodeLink> getContents()
    {
        return contents;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public Long getTotalCount()
    {
        return totalCount;
    }
    
    public void setContents(List<INodeLink> contents)
    {
        this.contents = contents;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(Long totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
