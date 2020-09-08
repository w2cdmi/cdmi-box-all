package pw.cdmi.box.disk.httpclient.rest.response;

import java.util.List;

public class GroupListResponse
{
    private List<RestGroupResponse> groups;
    
    private int limit;
    
    private long offset;
    
    private long totalCount;
    
    public List<RestGroupResponse> getGroups()
    {
        return groups;
    }
    
    public int getLimit()
    {
        return limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public long getTotalCount()
    {
        return totalCount;
    }
    
    public void setGroups(List<RestGroupResponse> groups)
    {
        this.groups = groups;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
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
