package com.huawei.sharedrive.app.openapi.domain.share;

import java.io.Serializable;
import java.util.List;

import com.huawei.sharedrive.app.share.domain.INodeLink;

/**
 *  指定节点 所有的外链列表
 * 
 */
public class RestNodeLinksList implements Serializable
{
    private static final long serialVersionUID = 2983201573064550058L;

    /**
     * 列表
     */
    private List<INodeLink> links;
    
    /**
     * 总数
     */
    private Long totalCount;
    
    public RestNodeLinksList()
    {
    }
    
    public RestNodeLinksList(List<INodeLink> links, Long totalCount)
    {
        this.totalCount = totalCount;
        this.links = links;
    }

    public List<INodeLink> getLinks()
    {
        return links;
    }

    public void setLinks(List<INodeLink> links)
    {
        this.links = links;
    }

    public Long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Long totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
