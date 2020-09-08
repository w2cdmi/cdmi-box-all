package pw.cdmi.box.disk.httpclient.rest.response;

import java.util.List;

import pw.cdmi.box.disk.group.domain.GroupMembershipsInfoV2;

public class GroupUserListResponse
{
    private int limit;
    
    private List<GroupMembershipsInfoV2> memberships;
    
    private long offset;
    
    private long totalCount;
    
    public int getLimit()
    {
        return limit;
    }
    
    public List<GroupMembershipsInfoV2> getMemberships()
    {
        return memberships;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public long getTotalCount()
    {
        return totalCount;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setMemberships(List<GroupMembershipsInfoV2> memberships)
    {
        this.memberships = memberships;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
