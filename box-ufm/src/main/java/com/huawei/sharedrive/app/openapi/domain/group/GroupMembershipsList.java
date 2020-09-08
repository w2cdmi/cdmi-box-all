package com.huawei.sharedrive.app.openapi.domain.group;

import java.util.List;

public class GroupMembershipsList
{
    private int limit;
    
    private long offset;
    
    private long totalCount;
    
    private List<GroupMembershipsInfo> memberships;
    
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
    
    public List<GroupMembershipsInfo> getMemberships()
    {
        return memberships;
    }
    
    public void setMemberships(List<GroupMembershipsInfo> memberships)
    {
        this.memberships = memberships;
    }
    
}
