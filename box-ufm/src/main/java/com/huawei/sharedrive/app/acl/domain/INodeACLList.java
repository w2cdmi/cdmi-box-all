package com.huawei.sharedrive.app.acl.domain;

import java.util.List;

public class INodeACLList
{
    private List<INodeACL> nodeACLs;
    
    private int limit;
    
    private long offset;
    
    private long totalCount;
    
    public List<INodeACL> getNodeACLs()
    {
        return nodeACLs;
    }
    
    public void setNodeACLs(List<INodeACL> nodeACLs)
    {
        this.nodeACLs = nodeACLs;
    }
    
    public int getLimit()
    {
        return limit;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public long getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
