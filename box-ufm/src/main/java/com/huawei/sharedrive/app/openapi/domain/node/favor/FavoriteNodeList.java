package com.huawei.sharedrive.app.openapi.domain.node.favor;

import java.util.List;


public class FavoriteNodeList
{
    
    /** 总数 */
    private int totalCount;
    
    /** 分页参数：偏移 */
    private long offset;
    
    /** 分页参数：当前页数量 */
    private int limit;
    
    /** 文件集合 */
    private List<FavoriteNodeResponse> contents;
    
    public int getLimit()
    {
        return limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public List<FavoriteNodeResponse> getContents()
    {
        return contents;
    }

    public void setContents(List<FavoriteNodeResponse> contents)
    {
        this.contents = contents;
    }
    
        
}
