package com.huawei.sharedrive.app.share.domain;

import java.util.List;

/**
 * 共享资源列表的REST分页对象
 * 
 * @author l90003768
 * 
 */
public class SharePage
{
    /**
     * 当前共享关系列表
     */
    private List<INodeShare> content;
    
    /**
     * 总数
     */
    private int totalCount;
    
    public SharePage(List<INodeShare> content, int totalCount)
    {
        this.totalCount = totalCount;
        this.content = content;
    }
    
    public List<INodeShare> getContent()
    {
        return content;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public void setContent(List<INodeShare> content)
    {
        this.content = content;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
