package pw.cdmi.box.uam.httpclient.domain;

import java.util.List;

public class RestAllTeamSpaceList
{
    private List<RestTeamSpaceInfo> teamSpaces;
    
    private int limit;
    
    private long offset;
    
    private long totalCount;
    
    public List<RestTeamSpaceInfo> getTeamSpaces()
    {
        return teamSpaces;
    }
    
    public void setTeamSpaces(List<RestTeamSpaceInfo> teamSpaceList)
    {
        this.teamSpaces = teamSpaceList;
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
