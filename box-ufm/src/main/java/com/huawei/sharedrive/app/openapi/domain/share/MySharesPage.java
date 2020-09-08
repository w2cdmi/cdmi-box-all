package com.huawei.sharedrive.app.openapi.domain.share;

import java.util.List;

import com.huawei.sharedrive.app.share.domain.INodeShare;

/**
 * 我共享出去的资源分页
 * 
 * @author l90005448
 * 
 */
public class MySharesPage
{
    
    /**
     * 当前共享关系列表
     */
    private List<INodeShare> contents;
    
    private Integer limit;
    
    private Long offset;
    
    /**
     * 总数
     */
    private Long totalCount;
    
    public MySharesPage()
    {
    }
    
    public MySharesPage(List<INodeShare> contents, Long totalCount)
    {
        this.totalCount = totalCount;
        this.contents = contents;
    }
    
    public List<INodeShare> getContents()
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
    
    public void setContents(List<INodeShare> contents)
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
