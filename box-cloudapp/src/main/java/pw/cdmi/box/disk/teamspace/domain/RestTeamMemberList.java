package pw.cdmi.box.disk.teamspace.domain;

import java.util.List;

public class RestTeamMemberList
{
    private int limit;
    
    private List<RestTeamMemberInfo> memberships;
    
    private long offset;
    
    private long totalCount;
    
    public int getLimit()
    {
        return limit;
    }
    
    public List<RestTeamMemberInfo> getMemberships()
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
    
    public void setMemberships(List<RestTeamMemberInfo> memberships)
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
