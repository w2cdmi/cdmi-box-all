package com.huawei.sharedrive.app.openapi.domain.share;

import java.util.List;

import com.huawei.sharedrive.app.share.domain.INodeShare;

/**
 * 共享用户列表的REST分页对象
 * 
 * @author l90005448
 * 
 */
public class SharePageV2
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
    private int totalCount;
    
    public SharePageV2(List<INodeShare> contents, int totalCount)
    {
        this.totalCount = totalCount;
        this.contents = contents;
    }
    
    public List<INodeShare> getContents()
    {
        return contents;
    }
    
    public void setContents(List<INodeShare> contents)
    {
        this.contents = contents;
    }
    
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
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
